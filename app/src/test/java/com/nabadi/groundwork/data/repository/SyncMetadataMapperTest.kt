package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.SyncMetadataEntity
import com.nabadi.groundwork.domain.model.SyncFailure
import com.nabadi.groundwork.domain.model.SyncOperation
import com.nabadi.groundwork.domain.model.SyncState
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncMetadataMapperTest {

    @Test
    fun `toDomain maps an unknown stored state to failed`() {
        val metadata = SyncMetadataEntity(state = "PENDING_UPLOAD").toDomain()

        assertEquals(SyncState.FAILED, metadata.state)
    }

    @Test
    fun `mappers preserve failed operation`() {
        val metadata = SyncMetadataEntity(
            state = SyncState.FAILED.name,
            failedOperation = SyncOperation.DELETE.name,
            errorMessage = "Server unavailable",
            failureOccurredAt = 123L,
        )

        assertEquals(
            SyncFailure(SyncOperation.DELETE, "Server unavailable", 123L),
            metadata.toDomain().failure,
        )
        assertEquals(metadata, metadata.toDomain().toEntity())
    }

    @Test
    fun `toDomain keeps a missing timestamp for legacy failures`() {
        val metadata = SyncMetadataEntity(
            state = SyncState.FAILED.name,
            failedOperation = SyncOperation.UPDATE.name,
            errorMessage = "Timed out",
        )

        assertEquals(
            SyncFailure(SyncOperation.UPDATE, "Timed out", failedAt = null),
            metadata.toDomain().failure,
        )
    }
}
