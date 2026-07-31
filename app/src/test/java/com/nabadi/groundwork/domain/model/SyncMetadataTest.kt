package com.nabadi.groundwork.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SyncMetadataTest {

    @Test
    fun `markPendingCreate sets create state and clears a previous error`() {
        val metadata = SyncMetadata(errorMessage = "Timed out")

        val result = metadata.markPendingCreate()

        assertEquals(SyncState.PENDING_CREATE, result.state)
        assertNull(result.errorMessage)
    }

    @Test
    fun `markPendingUpdate keeps an unsynced create as a create`() {
        val result = SyncMetadata(state = SyncState.PENDING_CREATE).markPendingUpdate()

        assertEquals(SyncState.PENDING_CREATE, result.state)
    }

    @Test
    fun `markPendingUpdate sets update state for an existing record`() {
        val result = SyncMetadata(state = SyncState.SYNCED).markPendingUpdate()

        assertEquals(SyncState.PENDING_UPDATE, result.state)
        assertNull(result.errorMessage)
    }

    @Test
    fun `markPendingDelete keeps an unsynced create unchanged`() {
        val metadata = SyncMetadata(state = SyncState.PENDING_CREATE)

        val result = metadata.markPendingDelete()

        assertEquals(metadata, result)
    }

    @Test
    fun `markPendingDelete sets delete state for a synced record`() {
        val result = SyncMetadata(state = SyncState.SYNCED).markPendingDelete()

        assertEquals(SyncState.PENDING_DELETE, result.state)
        assertNull(result.errorMessage)
    }

    @Test
    fun `markSynced records the completion time and clears an error`() {
        val result = SyncMetadata(
            state = SyncState.FAILED,
            errorMessage = "Timed out",
        ).markSynced(syncedAt = 123L)

        assertEquals(SyncState.SYNCED, result.state)
        assertEquals(123L, result.lastSyncedAt)
        assertNull(result.errorMessage)
    }

    @Test
    fun `markFailed records the error`() {
        val result = SyncMetadata(state = SyncState.PENDING_UPDATE).markFailed("No connection")

        assertEquals(SyncState.FAILED, result.state)
        assertEquals("No connection", result.errorMessage)
    }
}
