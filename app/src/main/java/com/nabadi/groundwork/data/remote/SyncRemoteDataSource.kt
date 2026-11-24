package com.nabadi.groundwork.data.remote

import com.nabadi.groundwork.data.remote.model.RemoteAck
import com.nabadi.groundwork.data.remote.model.RemoteErrorResponse
import com.nabadi.groundwork.data.remote.model.RemoteFieldNote
import com.nabadi.groundwork.data.remote.model.RemoteSite
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import kotlinx.serialization.SerializationException
import java.io.IOException
import javax.inject.Inject

/**
 * Pushes local changes to the sync backend and reports each outcome as a
 * [RemoteResult], so the sync engine never handles JSON, status codes, or
 * transport exceptions itself.
 */
internal class SyncRemoteDataSource @Inject constructor(
    private val transport: RemoteTransport,
) {

    /** Creates or updates [site]; succeeds with the server's new revision. */
    suspend fun upsertSite(site: Site): RemoteResult<Long> = upsert(
        path = RemotePaths.site(site.id.value),
        id = site.id.value,
        body = RemoteJson.encodeToString(RemoteSite.serializer(), site.toRemote()),
    )

    /** Creates or updates [fieldNote]; succeeds with the server's new revision. */
    suspend fun upsertFieldNote(fieldNote: FieldNote): RemoteResult<Long> = upsert(
        path = RemotePaths.fieldNote(fieldNote.id.value),
        id = fieldNote.id.value,
        body = RemoteJson.encodeToString(RemoteFieldNote.serializer(), fieldNote.toRemote()),
    )

    suspend fun deleteSite(id: SiteId): RemoteResult<Unit> =
        delete(RemotePaths.site(id.value))

    suspend fun deleteFieldNote(id: FieldNoteId): RemoteResult<Unit> =
        delete(RemotePaths.fieldNote(id.value))

    private suspend fun upsert(path: String, id: String, body: String): RemoteResult<Long> {
        val response = send(RemoteRequest(RemoteMethod.PUT, path, body))
            ?: return RemoteResult.Transient(NETWORK_FAILURE)

        if (response.status !in 200..299) return response.toFailure()

        // A success the client cannot read is a server-side problem; retry rather
        // than mark the local record as failed.
        val ack = try {
            RemoteJson.decodeFromString(RemoteAck.serializer(), response.body)
        } catch (_: SerializationException) {
            return RemoteResult.Transient("Unreadable acknowledgement from server")
        }
        if (ack.id != id) {
            return RemoteResult.Transient("Server acknowledged ${ack.id} instead of $id")
        }

        return RemoteResult.Success(ack.revision)
    }

    private suspend fun delete(path: String): RemoteResult<Unit> {
        val response = send(RemoteRequest(RemoteMethod.DELETE, path))
            ?: return RemoteResult.Transient(NETWORK_FAILURE)

        return when {
            response.status in 200..299 -> RemoteResult.Success(Unit)
            // Already gone is what a delete wants.
            response.status == 404 -> RemoteResult.Success(Unit)
            else -> response.toFailure()
        }
    }

    /** Returns null when the request did not complete; cancellation still propagates. */
    private suspend fun send(request: RemoteRequest): RemoteResponse? = try {
        transport.execute(request)
    } catch (_: IOException) {
        null
    }

    private fun RemoteResponse.toFailure(): RemoteResult<Nothing> {
        val detail = try {
            RemoteJson.decodeFromString(RemoteErrorResponse.serializer(), body).error
        } catch (_: SerializationException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
        val message = detail?.message ?: "HTTP $status"

        return if (status.isRetryable()) {
            RemoteResult.Transient(message)
        } else {
            RemoteResult.Rejected(
                status = status,
                code = detail?.code ?: "http_$status",
                message = message,
            )
        }
    }

    // A 4xx means the request itself is wrong, except a timeout (408) or rate
    // limit (429). Anything else unexpected (5xx, stray 1xx/3xx) is worth a retry.
    private fun Int.isRetryable(): Boolean =
        this !in 400..499 || this == 408 || this == 429

    private companion object {
        const val NETWORK_FAILURE = "Could not reach the server"
    }
}
