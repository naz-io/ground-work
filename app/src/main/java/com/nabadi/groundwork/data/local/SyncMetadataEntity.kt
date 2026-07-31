package com.nabadi.groundwork.data.local

import com.nabadi.groundwork.domain.model.SyncState

data class SyncMetadataEntity(
    val state: String = SyncState.SYNCED.name,
    val lastSyncedAt: Long? = null,
    val errorMessage: String? = null,
    val failedOperation: String? = null,
)
