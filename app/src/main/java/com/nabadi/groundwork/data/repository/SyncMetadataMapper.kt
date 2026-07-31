package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.SyncMetadataEntity
import com.nabadi.groundwork.domain.model.SyncMetadata
import com.nabadi.groundwork.domain.model.SyncState

fun SyncMetadataEntity.toDomain() = SyncMetadata(
    state = SyncState.fromStorage(state),
    lastSyncedAt = lastSyncedAt,
    errorMessage = errorMessage,
)

fun SyncMetadata.toEntity() = SyncMetadataEntity(
    state = state.name,
    lastSyncedAt = lastSyncedAt,
    errorMessage = errorMessage,
)
