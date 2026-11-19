package com.nabadi.groundwork.data.remote.fake

import com.nabadi.groundwork.data.remote.RemoteJson
import com.nabadi.groundwork.data.remote.RemoteMethod
import com.nabadi.groundwork.data.remote.RemotePaths
import com.nabadi.groundwork.data.remote.RemoteRequest
import com.nabadi.groundwork.data.remote.RemoteResponse
import com.nabadi.groundwork.data.remote.model.RemoteAck
import com.nabadi.groundwork.data.remote.model.RemoteErrorResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

class FakeGroundWorkServerTest {

    private val server = FakeGroundWorkServer()

    @Test
    fun `put site stores it and acknowledges revision 1`() = runTest {
        val response = server.putSite(siteJson())

        assertEquals(200, response.status)
        assertEquals(RemoteAck(id = SITE_ID, revision = 1), response.ack())
        assertEquals(RemoteJson.parseToJsonElement(siteJson()), server.storedSite(SITE_ID))
    }

    @Test
    fun `repeating an identical put keeps one record at the same revision`() = runTest {
        server.putSite(siteJson())

        val repeat = server.putSite(siteJson())

        assertEquals(RemoteAck(id = SITE_ID, revision = 1), repeat.ack())
    }

    @Test
    fun `changing a record advances its revision`() = runTest {
        server.putSite(siteJson(name = "North Warehouse"))

        val update = server.putSite(siteJson(name = "North Warehouse B"))

        assertEquals(RemoteAck(id = SITE_ID, revision = 2), update.ack())
        assertEquals(
            RemoteJson.parseToJsonElement(siteJson(name = "North Warehouse B")),
            server.storedSite(SITE_ID),
        )
    }

    @Test
    fun `malformed or incomplete bodies are rejected as invalid`() = runTest {
        val notJson = server.putSite("{ not json")
        val missingField = server.putSite("""{ "id": "$SITE_ID", "name": "North" }""")
        val notAnObject = server.putSite("[]")
        val noBody = server.execute(RemoteRequest(RemoteMethod.PUT, RemotePaths.site(SITE_ID)))

        listOf(notJson, missingField, notAnObject, noBody).forEach { response ->
            assertEquals(400, response.status)
            assertEquals("invalid_body", response.errorCode())
        }
        assertNull(server.storedSite(SITE_ID))
    }

    @Test
    fun `body id must match the path id`() = runTest {
        val response = server.execute(
            RemoteRequest(RemoteMethod.PUT, RemotePaths.site("other-site"), siteJson()),
        )

        assertEquals(400, response.status)
        assertEquals("id_mismatch", response.errorCode())
    }

    @Test
    fun `a site without a name is rejected`() = runTest {
        val response = server.putSite(siteJson(name = "  "))

        assertEquals(422, response.status)
        assertEquals("validation_failed", response.errorCode())
    }

    @Test
    fun `a field note without title or body is rejected`() = runTest {
        val response = server.putFieldNote(fieldNoteJson(title = "", body = " "))

        assertEquals(422, response.status)
        assertEquals("validation_failed", response.errorCode())
    }

    @Test
    fun `an untitled field note with a body is accepted`() = runTest {
        val response = server.putFieldNote(fieldNoteJson(title = "", body = "Loose fencing."))

        assertEquals(200, response.status)
    }

    @Test
    fun `a field note may only reference a site the server has`() = runTest {
        val beforeSite = server.putFieldNote(fieldNoteJson(siteId = SITE_ID))
        server.putSite(siteJson())
        val afterSite = server.putFieldNote(fieldNoteJson(siteId = SITE_ID))

        assertEquals(422, beforeSite.status)
        assertEquals("unknown_site", beforeSite.errorCode())
        assertEquals(200, afterSite.status)
    }

    @Test
    fun `deleting removes the record and repeating the delete still succeeds`() = runTest {
        server.putFieldNote(fieldNoteJson())

        val first = server.execute(RemoteRequest(RemoteMethod.DELETE, RemotePaths.fieldNote(NOTE_ID)))
        val repeat = server.execute(RemoteRequest(RemoteMethod.DELETE, RemotePaths.fieldNote(NOTE_ID)))

        assertEquals(204, first.status)
        assertEquals(204, repeat.status)
        assertNull(server.storedFieldNote(NOTE_ID))
    }

    @Test
    fun `a site with field notes cannot be deleted until they are unassigned`() = runTest {
        server.putSite(siteJson())
        server.putFieldNote(fieldNoteJson(siteId = SITE_ID))

        val whileInUse = server.deleteSite()
        server.putFieldNote(fieldNoteJson(siteId = null))
        val afterUnassign = server.deleteSite()

        assertEquals(409, whileInUse.status)
        assertEquals("site_in_use", whileInUse.errorCode())
        assertEquals(204, afterUnassign.status)
        assertNull(server.storedSite(SITE_ID))
    }

    @Test
    fun `a simulated network error throws once and leaves state unchanged`() = runTest {
        server.failNext(SimulatedFailure.NetworkError)

        val failure = try {
            server.putSite(siteJson())
            null
        } catch (e: IOException) {
            e
        }

        assertTrue(failure is IOException)
        assertNull(server.storedSite(SITE_ID))

        assertEquals(200, server.putSite(siteJson()).status)
    }

    @Test
    fun `a simulated server error returns 503 without applying the request`() = runTest {
        server.failNext(SimulatedFailure.ServerError(), times = 2)

        val first = server.putSite(siteJson())
        val second = server.putSite(siteJson())
        val third = server.putSite(siteJson())

        assertEquals(listOf(503, 503, 200), listOf(first, second, third).map { it.status })
        assertEquals("server_error", first.errorCode())
        assertEquals(RemoteAck(id = SITE_ID, revision = 1), third.ack())
    }

    @Test
    fun `a simulated rejection returns its status and code`() = runTest {
        server.failNext(SimulatedFailure.Rejection(status = 403, code = "forbidden"))

        val response = server.putSite(siteJson())

        assertEquals(403, response.status)
        assertEquals("forbidden", response.errorCode())
        assertNull(server.storedSite(SITE_ID))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `latency delays every request`() = runTest {
        server.latency = 2.seconds

        server.putSite(siteJson())

        assertEquals(2_000L, testScheduler.currentTime)
    }

    @Test
    fun `unknown routes return 404`() = runTest {
        val unknownCollection = server.execute(RemoteRequest(RemoteMethod.PUT, "/reports/r-1", "{}"))
        val missingId = server.execute(RemoteRequest(RemoteMethod.DELETE, "/sites/"))

        assertEquals(404, unknownCollection.status)
        assertEquals(404, missingId.status)
        assertNotNull(unknownCollection.errorCode())
    }

    private suspend fun FakeGroundWorkServer.putSite(body: String) =
        execute(RemoteRequest(RemoteMethod.PUT, RemotePaths.site(SITE_ID), body))

    private suspend fun FakeGroundWorkServer.putFieldNote(body: String) =
        execute(RemoteRequest(RemoteMethod.PUT, RemotePaths.fieldNote(NOTE_ID), body))

    private suspend fun FakeGroundWorkServer.deleteSite() =
        execute(RemoteRequest(RemoteMethod.DELETE, RemotePaths.site(SITE_ID)))

    private fun RemoteResponse.ack(): RemoteAck =
        RemoteJson.decodeFromString(RemoteAck.serializer(), body)

    private fun RemoteResponse.errorCode(): String =
        RemoteJson.decodeFromString(RemoteErrorResponse.serializer(), body).error.code

    private fun siteJson(name: String = "North Warehouse") = """
        {
          "id": "$SITE_ID", "name": "$name", "description": "", "location": "Sector A",
          "priority": "high", "status": "active",
          "created_at": "2025-01-01T09:00:00Z", "updated_at": "2025-01-01T10:15:00Z"
        }
    """.trimIndent()

    private fun fieldNoteJson(
        siteId: String? = null,
        title: String = "North gate",
        body: String = "Loose fencing.",
    ) = """
        {
          "id": "$NOTE_ID", "site_id": ${siteId?.let { "\"$it\"" } ?: "null"},
          "title": "$title", "body": "$body", "status": "draft",
          "created_at": "2025-01-01T09:00:00Z", "updated_at": "2025-01-01T09:00:00Z"
        }
    """.trimIndent()

    private companion object {
        const val SITE_ID = "site-001"
        const val NOTE_ID = "note-001"
    }
}
