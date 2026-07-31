package com.nabadi.groundwork.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncStateTest {

    @Test
    fun `fromStorage returns the matching state`() {
        assertEquals(SyncState.PENDING_UPDATE, SyncState.fromStorage("PENDING_UPDATE"))
    }

    @Test
    fun `fromStorage maps an unknown value to failed`() {
        assertEquals(SyncState.FAILED, SyncState.fromStorage("PENDING_UPLOAD"))
    }
}
