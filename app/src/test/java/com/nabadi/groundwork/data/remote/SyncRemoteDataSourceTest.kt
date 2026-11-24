package com.nabadi.groundwork.data.remote

import com.nabadi.groundwork.data.remote.fake.FakeGroundWorkServer
import com.nabadi.groundwork.data.remote.fake.SimulatedFailure
import com.nabadi.groundwork.data.remote.model.RemoteSite
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.feature.fieldnotes.TestFieldNote
import com.nabadi.groundwork.feature.sites.TestSite
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncRemoteDataSourceTest {

    private val server = FakeGroundWorkServer()
    private val dataSource = SyncRemoteDataSource(server)

    // Against the fake server: the real JSON path end to end.

    @Test
    fun `upserting a site sends its wire format and returns the revision`() = runTest {
        val site = TestSite.site(id = "site-001")

        val result = dataSource.upsertSite(site)

        assertEquals(RemoteResult.Success(1L), result)
        assertEquals(
            RemoteJson.encodeToJsonElement(RemoteSite.serializer(), site.toRemote()),
            server.storedSite("site-001"),
        )
    }

    @Test
    fun `upserting a changed field note returns the next revision`() = runTest {
        val note = TestFieldNote.fieldNote(id = "note-001", siteId = null)
        dataSource.upsertFieldNote(note)

        val result = dataSource.upsertFieldNote(note.copy(body = "Updated body"))

        assertEquals(RemoteResult.Success(2L), result)
    }

    @Test
    fun `a network failure is transient`() = runTest {
        server.failNext(SimulatedFailure.NetworkError)

        val result = dataSource.upsertSite(TestSite.site())

        assertTrue(result is RemoteResult.Transient)
    }

    @Test
    fun `a server error is transient and keeps the server's message`() = runTest {
        server.failNext(SimulatedFailure.ServerError(status = 503))

        val result = dataSource.upsertSite(TestSite.site())

        assertEquals(RemoteResult.Transient("Simulated server error"), result)
    }

    @Test
    fun `a validation failure is rejected with the server's code and message`() = runTest {
        val result = dataSource.upsertSite(TestSite.site(name = " "))

        assertEquals(
            RemoteResult.Rejected(422, "validation_failed", "Site name is required"),
            result,
        )
    }

    @Test
    fun `a field note for a site the server lacks is rejected`() = runTest {
        val result = dataSource.upsertFieldNote(TestFieldNote.fieldNote(siteId = "site-missing"))

        assertTrue(result is RemoteResult.Rejected && result.code == "unknown_site")
    }

    @Test
    fun `deleting a site that still has notes is rejected`() = runTest {
        dataSource.upsertSite(TestSite.site(id = "site-001"))
        dataSource.upsertFieldNote(TestFieldNote.fieldNote(siteId = "site-001"))

        val result = dataSource.deleteSite(SiteId("site-001"))

        assertTrue(result is RemoteResult.Rejected && result.status == 409)
    }

    @Test
    fun `deleting succeeds whether or not the record exists`() = runTest {
        dataSource.upsertFieldNote(TestFieldNote.fieldNote(id = "note-001", siteId = null))

        val existing = dataSource.deleteFieldNote(FieldNoteId("note-001"))
        val missing = dataSource.deleteFieldNote(FieldNoteId("note-001"))

        assertEquals(RemoteResult.Success(Unit), existing)
        assertEquals(RemoteResult.Success(Unit), missing)
    }

    // Against a stub transport: responses the fake server never produces.

    @Test
    fun `timeouts and rate limits are transient`() = runTest {
        listOf(408, 429).forEach { status ->
            val result = stubbed(RemoteResponse(status)).upsertSite(TestSite.site())

            assertTrue("HTTP $status should be transient", result is RemoteResult.Transient)
        }
    }

    @Test
    fun `a 404 on delete means already gone but on upsert is a rejection`() = runTest {
        val source = stubbed(RemoteResponse(404))

        assertEquals(RemoteResult.Success(Unit), source.deleteSite(SiteId("site-001")))
        assertTrue(source.upsertSite(TestSite.site()) is RemoteResult.Rejected)
    }

    @Test
    fun `an unreadable or mismatched acknowledgement is transient`() = runTest {
        val unreadable = stubbed(RemoteResponse(200, "not json"))
            .upsertSite(TestSite.site(id = "site-001"))
        val noBody = stubbed(RemoteResponse(204))
            .upsertSite(TestSite.site(id = "site-001"))
        val wrongId = stubbed(RemoteResponse(200, """{ "id": "site-002", "revision": 1 }"""))
            .upsertSite(TestSite.site(id = "site-001"))

        listOf(unreadable, noBody, wrongId).forEach { result ->
            assertTrue("$result should be transient", result is RemoteResult.Transient)
        }
    }

    @Test
    fun `a rejection without a readable error body falls back to the status`() = runTest {
        val result = stubbed(RemoteResponse(400, "<html>Bad Request</html>"))
            .upsertSite(TestSite.site())

        assertEquals(RemoteResult.Rejected(400, "http_400", "HTTP 400"), result)
    }

    @Test
    fun `cancellation is not swallowed as a failure`() = runTest {
        val source = SyncRemoteDataSource(
            object : RemoteTransport {
                override suspend fun execute(request: RemoteRequest): RemoteResponse =
                    throw CancellationException("Sync cancelled")
            },
        )

        val thrown = try {
            source.upsertSite(TestSite.site())
            null
        } catch (e: CancellationException) {
            e
        }

        assertTrue(thrown is CancellationException)
    }

    private fun stubbed(response: RemoteResponse) = SyncRemoteDataSource(
        object : RemoteTransport {
            override suspend fun execute(request: RemoteRequest): RemoteResponse = response
        },
    )
}
