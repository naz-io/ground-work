package com.nabadi.groundwork.data.local

data class SyncMetadataEntity(
    val state: String = "SYNCED",
    val lastSyncedAt: Long? = null,
    val errorMessage: String? = null,
    val failedOperation: String? = null,
    val failureOccurredAt: Long? = null,
)
