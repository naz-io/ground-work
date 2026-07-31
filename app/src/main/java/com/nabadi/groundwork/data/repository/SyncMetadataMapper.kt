package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.SyncMetadataEntity
import com.nabadi.groundwork.domain.model.SyncMetadata
import com.nabadi.groundwork.domain.model.SyncFailure
import com.nabadi.groundwork.domain.model.SyncOperation
import com.nabadi.groundwork.domain.model.SyncState

fun SyncMetadataEntity.toDomain() = SyncMetadata(
    state = SyncState.fromStorage(state),
    lastSyncedAt = lastSyncedAt,
    failure = SyncOperation.fromStorage(failedOperation)
        ?.let { operation ->
            errorMessage?.let { message ->
                SyncFailure(operation = operation, message = message)
            }
        },
)

fun SyncMetadata.toEntity() = SyncMetadataEntity(
    state = state.name,
    lastSyncedAt = lastSyncedAt,
    errorMessage = failure?.message,
    failedOperation = failure?.operation?.name,
)
