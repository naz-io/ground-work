package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.domain.model.SyncState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PersistedEnumMapperTest {

    @Test
    fun `decodes a matching persisted enum value`() {
        assertEquals(
            SyncState.PENDING_UPDATE,
            "PENDING_UPDATE".toPersistedEnumOrNull<SyncState>(),
        )
    }

    @Test
    fun `returns null for an unknown persisted enum value`() {
        assertNull("PENDING_UPLOAD".toPersistedEnumOrNull<SyncState>())
    }

    @Test
    fun `callers can use safe fallbacks for unknown persisted values`() {
        assertEquals(
            SitePriority.NORMAL,
            "CRITICAL".toPersistedEnumOrNull<SitePriority>() ?: SitePriority.NORMAL,
        )
        assertEquals(
            SiteStatus.ARCHIVED,
            "ON_HOLD".toPersistedEnumOrNull<SiteStatus>() ?: SiteStatus.ARCHIVED,
        )
        assertEquals(
            FieldNoteStatus.ARCHIVED,
            "REVIEW".toPersistedEnumOrNull<FieldNoteStatus>() ?: FieldNoteStatus.ARCHIVED,
        )
    }
}
