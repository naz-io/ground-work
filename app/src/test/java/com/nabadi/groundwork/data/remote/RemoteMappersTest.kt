package com.nabadi.groundwork.data.remote

import com.nabadi.groundwork.data.remote.model.RemoteFieldNote
import com.nabadi.groundwork.data.remote.model.RemoteFieldNoteStatus
import com.nabadi.groundwork.data.remote.model.RemoteSite
import com.nabadi.groundwork.data.remote.model.RemoteSitePriority
import com.nabadi.groundwork.data.remote.model.RemoteSiteStatus
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.domain.model.SyncMetadata
import com.nabadi.groundwork.domain.model.SyncState
import com.nabadi.groundwork.feature.fieldnotes.TestFieldNote
import com.nabadi.groundwork.feature.sites.TestSite
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class RemoteMappersTest {

    @Test
    fun `site maps to the remote model with ISO instants`() {
        val site = TestSite.site(
            id = "site-001",
            name = "North Warehouse",
            description = "Main storage facility.",
            location = "Sector A",
            priority = SitePriority.HIGH,
            status = SiteStatus.ARCHIVED,
            createdAt = CREATED_AT.toEpochMilli(),
            updatedAt = UPDATED_AT.toEpochMilli(),
        )

        assertEquals(
            RemoteSite(
                id = "site-001",
                name = "North Warehouse",
                description = "Main storage facility.",
                location = "Sector A",
                priority = RemoteSitePriority.HIGH,
                status = RemoteSiteStatus.ARCHIVED,
                createdAt = CREATED_AT,
                updatedAt = UPDATED_AT,
            ),
            site.toRemote(),
        )
    }

    @Test
    fun `field note maps to the remote model with its site id`() {
        val note = TestFieldNote.fieldNote(
            id = "note-001",
            siteId = "site-001",
            title = "North gate",
            body = "Loose fencing.",
            status = FieldNoteStatus.DRAFT,
            createdAt = CREATED_AT.toEpochMilli(),
            updatedAt = UPDATED_AT.toEpochMilli(),
        )

        assertEquals(
            RemoteFieldNote(
                id = "note-001",
                siteId = "site-001",
                title = "North gate",
                body = "Loose fencing.",
                status = RemoteFieldNoteStatus.DRAFT,
                createdAt = CREATED_AT,
                updatedAt = UPDATED_AT,
            ),
            note.toRemote(),
        )
    }

    @Test
    fun `unassigned field note maps to a null site id`() {
        assertNull(TestFieldNote.fieldNote(siteId = null).toRemote().siteId)
    }

    @Test
    fun `local sync state does not change what is sent`() {
        val synced = TestSite.site(syncMetadata = SyncMetadata(state = SyncState.SYNCED))
        val pending = synced.copy(syncMetadata = SyncMetadata(state = SyncState.PENDING_UPDATE))

        assertEquals(synced.toRemote(), pending.toRemote())
    }

    @Test
    fun `every domain enum value has a remote counterpart with the same meaning`() {
        assertEquals(
            listOf(RemoteSitePriority.LOW, RemoteSitePriority.NORMAL, RemoteSitePriority.HIGH),
            SitePriority.entries.map { it.toRemote() },
        )
        assertEquals(
            listOf(RemoteSiteStatus.ACTIVE, RemoteSiteStatus.ARCHIVED),
            SiteStatus.entries.map { it.toRemote() },
        )
        assertEquals(
            listOf(
                RemoteFieldNoteStatus.DRAFT,
                RemoteFieldNoteStatus.ACTIVE,
                RemoteFieldNoteStatus.ARCHIVED,
            ),
            FieldNoteStatus.entries.map { it.toRemote() },
        )
    }

    private companion object {
        val CREATED_AT: Instant = Instant.parse("2025-01-01T09:00:00Z")
        val UPDATED_AT: Instant = Instant.parse("2025-01-01T10:15:00.123Z")
    }
}
