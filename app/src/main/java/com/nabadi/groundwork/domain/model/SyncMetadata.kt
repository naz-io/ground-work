package com.nabadi.groundwork.domain.model

data class SyncMetadata(
    val state: SyncState = SyncState.SYNCED,
    val lastSyncedAt: Long? = null,
    val errorMessage: String? = null,
) {
    fun markPendingCreate(): SyncMetadata {
        return copy(
            state = SyncState.PENDING_CREATE,
            errorMessage = null,
        )
    }

    fun markPendingUpdate(): SyncMetadata {
        return when (state) {
            SyncState.PENDING_CREATE -> copy(errorMessage = null)
            else -> copy(
                state = SyncState.PENDING_UPDATE,
                errorMessage = null,
            )
        }
    }

    fun markPendingDelete(): SyncMetadata {
        return when (state) {
            SyncState.PENDING_CREATE -> this
            else -> copy(
                state = SyncState.PENDING_DELETE,
                errorMessage = null,
            )
        }
    }

    fun markSynced(syncedAt: Long): SyncMetadata {
        return copy(
            state = SyncState.SYNCED,
            lastSyncedAt = syncedAt,
            errorMessage = null,
        )
    }

    fun markFailed(message: String): SyncMetadata {
        return copy(
            state = SyncState.FAILED,
            errorMessage = message,
        )
    }
}
