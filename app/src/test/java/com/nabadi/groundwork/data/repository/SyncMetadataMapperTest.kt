package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.SyncMetadataEntity
import com.nabadi.groundwork.domain.model.SyncState
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncMetadataMapperTest {

    @Test
    fun `toDomain maps an unknown stored state to failed`() {
        val metadata = SyncMetadataEntity(state = "PENDING_UPLOAD").toDomain()

        assertEquals(SyncState.FAILED, metadata.state)
    }
}
