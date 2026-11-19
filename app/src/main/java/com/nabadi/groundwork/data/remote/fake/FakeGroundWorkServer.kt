package com.nabadi.groundwork.data.remote.fake

import com.nabadi.groundwork.data.remote.RemoteJson
import com.nabadi.groundwork.data.remote.RemoteMethod
import com.nabadi.groundwork.data.remote.RemoteRequest
import com.nabadi.groundwork.data.remote.RemoteResponse
import com.nabadi.groundwork.data.remote.RemoteTransport
import com.nabadi.groundwork.data.remote.model.RemoteAck
import com.nabadi.groundwork.data.remote.model.RemoteErrorDetail
import com.nabadi.groundwork.data.remote.model.RemoteErrorResponse
import com.nabadi.groundwork.data.remote.model.RemoteFieldNote
import com.nabadi.groundwork.data.remote.model.RemoteSite
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.io.IOException
import kotlin.time.Duration

/**
 * An in-memory stand-in for the sync backend.
 *
 * It parses every request body as JSON and stores records as JSON objects, so
 * wire-format mistakes fail here the way they would against a real server. It
 * enforces the server-side rules the client must respect (required fields,
 * notes may only reference known sites, a site in use cannot be deleted) and
 * numbers each record's revisions.
 *
 * State lives in memory and is lost when the process dies; this is a
 * simulation, not a backend.
 */
internal class FakeGroundWorkServer : RemoteTransport {

    private val mutex = Mutex()
    private val sites = mutableMapOf<String, StoredRecord>()
    private val fieldNotes = mutableMapOf<String, StoredRecord>()
    private val plannedFailures = ArrayDeque<SimulatedFailure>()

    /** Added before every request is handled, to simulate a slow network. */
    @Volatile
    var latency: Duration = Duration.ZERO

    /** Makes the next [times] requests fail with [failure], without applying them. */
    suspend fun failNext(failure: SimulatedFailure, times: Int = 1) {
        require(times > 0) { "times must be positive" }
        mutex.withLock { repeat(times) { plannedFailures.addLast(failure) } }
    }

    suspend fun storedSite(id: String): JsonObject? = mutex.withLock { sites[id]?.json }

    suspend fun storedFieldNote(id: String): JsonObject? = mutex.withLock { fieldNotes[id]?.json }

    override suspend fun execute(request: RemoteRequest): RemoteResponse {
        if (latency > Duration.ZERO) delay(latency)

        return mutex.withLock {
            when (val failure = plannedFailures.removeFirstOrNull()) {
                null -> route(request)
                is SimulatedFailure.NetworkError -> throw IOException("Simulated network failure")
                is SimulatedFailure.ServerError -> errorResponse(failure.status, "server_error", "Simulated server error")
                is SimulatedFailure.Rejection -> errorResponse(failure.status, failure.code, failure.message)
            }
        }
    }

    private fun route(request: RemoteRequest): RemoteResponse {
        val segments = request.path.trim('/').split('/')
        if (segments.size != 2 || segments[1].isBlank()) return notFound(request)
        val (collection, id) = segments

        return when (collection to request.method) {
            "sites" to RemoteMethod.PUT -> putSite(id, request.body)
            "sites" to RemoteMethod.DELETE -> deleteSite(id)
            "field-notes" to RemoteMethod.PUT -> putFieldNote(id, request.body)
            "field-notes" to RemoteMethod.DELETE -> deleteFieldNote(id)
            else -> notFound(request)
        }
    }

    private fun putSite(id: String, body: String?): RemoteResponse {
        val (site, json) = decode(body, RemoteSite.serializer()) ?: return invalidBody()
        if (site.id != id) return idMismatch()
        if (site.name.isBlank()) return errorResponse(422, "validation_failed", "Site name is required")

        return store(sites, id, json)
    }

    private fun putFieldNote(id: String, body: String?): RemoteResponse {
        val (note, json) = decode(body, RemoteFieldNote.serializer()) ?: return invalidBody()
        if (note.id != id) return idMismatch()
        if (note.title.isBlank() && note.body.isBlank()) {
            return errorResponse(422, "validation_failed", "A field note needs a title or a body")
        }
        if (note.siteId != null && note.siteId !in sites) {
            return errorResponse(422, "unknown_site", "Site ${note.siteId} does not exist")
        }

        return store(fieldNotes, id, json)
    }

    private fun deleteSite(id: String): RemoteResponse {
        val inUse = fieldNotes.values.any { record ->
            RemoteJson.decodeFromJsonElement(RemoteFieldNote.serializer(), record.json).siteId == id
        }
        if (inUse) return errorResponse(409, "site_in_use", "Site $id still has field notes")

        sites.remove(id)
        return RemoteResponse(status = 204)
    }

    private fun deleteFieldNote(id: String): RemoteResponse {
        fieldNotes.remove(id)
        return RemoteResponse(status = 204)
    }

    /** Stores [json]; a repeat with identical content keeps its revision, so retries are true no-ops. */
    private fun store(
        records: MutableMap<String, StoredRecord>,
        id: String,
        json: JsonObject,
    ): RemoteResponse {
        val existing = records[id]
        val revision = when {
            existing == null -> 1L
            existing.json == json -> existing.revision
            else -> existing.revision + 1
        }
        records[id] = StoredRecord(json = json, revision = revision)

        return RemoteResponse(
            status = 200,
            body = RemoteJson.encodeToString(RemoteAck.serializer(), RemoteAck(id = id, revision = revision)),
        )
    }

    private fun <T> decode(body: String?, serializer: KSerializer<T>): Pair<T, JsonObject>? {
        if (body.isNullOrBlank()) return null
        return try {
            val json = RemoteJson.parseToJsonElement(body).jsonObject
            RemoteJson.decodeFromJsonElement(serializer, json) to json
        } catch (_: SerializationException) {
            null
        } catch (_: IllegalArgumentException) {
            // Thrown by jsonObject when the body is valid JSON but not an object.
            null
        }
    }

    private fun invalidBody() = errorResponse(400, "invalid_body", "Request body is not a valid record")

    private fun idMismatch() = errorResponse(400, "id_mismatch", "Path id and body id differ")

    private fun notFound(request: RemoteRequest) =
        errorResponse(404, "not_found", "No route for ${request.method} ${request.path}")

    private fun errorResponse(status: Int, code: String, message: String) = RemoteResponse(
        status = status,
        body = RemoteJson.encodeToString(
            RemoteErrorResponse.serializer(),
            RemoteErrorResponse(RemoteErrorDetail(code = code, message = message)),
        ),
    )

    private data class StoredRecord(val json: JsonObject, val revision: Long)
}

/** A failure the fake server can be told to produce instead of handling a request. */
internal sealed interface SimulatedFailure {
    /** The request never reaches the server (or its response is lost). */
    data object NetworkError : SimulatedFailure

    /** The server is temporarily unable to handle the request. */
    data class ServerError(val status: Int = 503) : SimulatedFailure

    /** The server permanently refuses the request. */
    data class Rejection(
        val status: Int = 422,
        val code: String = "rejected",
        val message: String = "Simulated rejection",
    ) : SimulatedFailure
}
