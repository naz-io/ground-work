package com.nabadi.groundwork.domain.model

enum class SyncState {
    SYNCED,
    PENDING_CREATE,
    PENDING_UPDATE,
    PENDING_DELETE,
    FAILED,
}