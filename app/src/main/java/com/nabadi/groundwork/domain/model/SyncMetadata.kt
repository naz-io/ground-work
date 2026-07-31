package com.nabadi.groundwork.domain.model

data class SyncMetadata(
    val state: SyncState = SyncState.SYNCED,
    val lastSyncedAt: Long? = null,
    val failure: SyncFailure? = null,
) {
    fun markPendingCreate(): SyncMetadata {
        return copy(
            state = SyncState.PENDING_CREATE,
            failure = null,
        )
    }

    fun markPendingUpdate(): SyncMetadata {
        return when {
            state == SyncState.PENDING_CREATE -> copy(
                failure = null,
            )
            failedCreate -> copy(
                state = SyncState.PENDING_CREATE,
                failure = null,
            )
            else -> copy(
                state = SyncState.PENDING_UPDATE,
                failure = null,
            )
        }
    }

    fun markPendingDelete(): SyncMetadata {
        return when (state) {
            SyncState.PENDING_CREATE -> this
            else -> copy(
                state = SyncState.PENDING_DELETE,
                failure = null,
            )
        }
    }

    fun markSynced(syncedAt: Long): SyncMetadata {
        return copy(
            state = SyncState.SYNCED,
            lastSyncedAt = syncedAt,
            failure = null,
        )
    }

    fun markFailed(message: String): SyncMetadata {
        return copy(
            state = SyncState.FAILED,
            failure = SyncFailure(
                operation = requireNotNull(state.toSyncOperation()) {
                    "Only pending sync operations can fail."
                },
                message = message,
            ),
        )
    }

    val failedCreate: Boolean
        get() = state == SyncState.FAILED && failure?.operation == SyncOperation.CREATE

    private fun SyncState.toSyncOperation(): SyncOperation? =
        when (this) {
            SyncState.PENDING_CREATE -> SyncOperation.CREATE
            SyncState.PENDING_UPDATE -> SyncOperation.UPDATE
            SyncState.PENDING_DELETE -> SyncOperation.DELETE
            SyncState.SYNCED,
            SyncState.FAILED,
            -> null
        }
}
