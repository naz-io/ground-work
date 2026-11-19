package com.nabadi.groundwork.data.remote

import com.nabadi.groundwork.data.remote.model.RemoteAck
import com.nabadi.groundwork.data.remote.model.RemoteErrorDetail
import com.nabadi.groundwork.data.remote.model.RemoteErrorResponse
import com.nabadi.groundwork.data.remote.model.RemoteFieldNote
import com.nabadi.groundwork.data.remote.model.RemoteFieldNoteStatus
import com.nabadi.groundwork.data.remote.model.RemoteSite
import com.nabadi.groundwork.data.remote.model.RemoteSitePriority
import com.nabadi.groundwork.data.remote.model.RemoteSiteStatus
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

/**
 * Pins the backend's JSON format with literal JSON, so a change to field names,
 * timestamp format, or enum spelling fails here instead of at the server.
 */
class RemoteWireFormatTest {

    @Test
    fun `site encodes to the backend format`() {
        val site = RemoteSite(
            id = "site-001",
            name = "North Warehouse",
            description = "Main storage facility.",
            location = "Sector A",
            priority = RemoteSitePriority.HIGH,
            status = RemoteSiteStatus.ACTIVE,
            createdAt = Instant.parse("2025-01-01T09:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T10:15:00.123Z"),
        )

        assertJsonEquals(
            """
            {
              "id": "site-001",
              "name": "North Warehouse",
              "description": "Main storage facility.",
              "location": "Sector A",
              "priority": "high",
              "status": "active",
              "created_at": "2025-01-01T09:00:00Z",
              "updated_at": "2025-01-01T10:15:00.123Z"
            }
            """,
            RemoteJson.encodeToString(RemoteSite.serializer(), site),
        )
    }

    @Test
    fun `unassigned field note sends an explicit null site id`() {
        val note = RemoteFieldNote(
            id = "note-001",
            siteId = null,
            title = "",
            body = "Loose fencing.",
            status = RemoteFieldNoteStatus.DRAFT,
            createdAt = Instant.parse("2025-01-01T09:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T09:00:00Z"),
        )

        assertJsonEquals(
            """
            {
              "id": "note-001",
              "site_id": null,
              "title": "",
              "body": "Loose fencing.",
              "status": "draft",
              "created_at": "2025-01-01T09:00:00Z",
              "updated_at": "2025-01-01T09:00:00Z"
            }
            """,
            RemoteJson.encodeToString(RemoteFieldNote.serializer(), note),
        )
    }

    @Test
    fun `enum values use the backend's lowercase names`() {
        assertEquals(
            listOf("\"low\"", "\"normal\"", "\"high\""),
            RemoteSitePriority.entries.map {
                RemoteJson.encodeToString(RemoteSitePriority.serializer(), it)
            },
        )
        assertEquals(
            listOf("\"active\"", "\"archived\""),
            RemoteSiteStatus.entries.map {
                RemoteJson.encodeToString(RemoteSiteStatus.serializer(), it)
            },
        )
        assertEquals(
            listOf("\"draft\"", "\"active\"", "\"archived\""),
            RemoteFieldNoteStatus.entries.map {
                RemoteJson.encodeToString(RemoteFieldNoteStatus.serializer(), it)
            },
        )
    }

    @Test
    fun `site and field note survive a JSON round trip`() {
        val site = RemoteSite(
            id = "site-001",
            name = "North Warehouse",
            description = "",
            location = "Sector A",
            priority = RemoteSitePriority.LOW,
            status = RemoteSiteStatus.ARCHIVED,
            createdAt = Instant.parse("2025-01-01T09:00:00.001Z"),
            updatedAt = Instant.parse("2025-01-01T10:15:00.999Z"),
        )
        val note = RemoteFieldNote(
            id = "note-001",
            siteId = "site-001",
            title = "North gate",
            body = "Line one\nLine \"two\"",
            status = RemoteFieldNoteStatus.ARCHIVED,
            createdAt = Instant.parse("2025-01-01T09:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T09:30:00Z"),
        )

        assertEquals(
            site,
            RemoteJson.decodeFromString(
                RemoteSite.serializer(),
                RemoteJson.encodeToString(RemoteSite.serializer(), site),
            ),
        )
        assertEquals(
            note,
            RemoteJson.decodeFromString(
                RemoteFieldNote.serializer(),
                RemoteJson.encodeToString(RemoteFieldNote.serializer(), note),
            ),
        )
    }

    @Test
    fun `ack decodes and ignores fields the client does not know`() {
        val ack = RemoteJson.decodeFromString(
            RemoteAck.serializer(),
            """{ "id": "site-001", "revision": 3, "server_time": "2025-01-01T10:15:00Z" }""",
        )

        assertEquals(RemoteAck(id = "site-001", revision = 3), ack)
    }

    @Test
    fun `error body decodes its code and message`() {
        val error = RemoteJson.decodeFromString(
            RemoteErrorResponse.serializer(),
            """{ "error": { "code": "validation_failed", "message": "name is required" } }""",
        )

        assertEquals(
            RemoteErrorResponse(
                RemoteErrorDetail(code = "validation_failed", message = "name is required"),
            ),
            error,
        )
    }

    @Test(expected = SerializationException::class)
    fun `a timestamp that is not ISO-8601 fails to decode`() {
        RemoteJson.decodeFromString(
            RemoteFieldNote.serializer(),
            """
            {
              "id": "note-001", "site_id": null, "title": "", "body": "x", "status": "draft",
              "created_at": "1735722000000", "updated_at": "2025-01-01T09:00:00Z"
            }
            """,
        )
    }

    @Test(expected = SerializationException::class)
    fun `an unknown enum value fails to decode`() {
        RemoteJson.decodeFromString(
            RemoteSite.serializer(),
            """
            {
              "id": "site-001", "name": "n", "description": "", "location": "l",
              "priority": "critical", "status": "active",
              "created_at": "2025-01-01T09:00:00Z", "updated_at": "2025-01-01T09:00:00Z"
            }
            """,
        )
    }

    private fun assertJsonEquals(expected: String, actual: String) {
        assertEquals(
            RemoteJson.parseToJsonElement(expected),
            RemoteJson.parseToJsonElement(actual),
        )
    }
}
