package com.nabadi.groundwork.domain.model

enum class SyncState {
    SYNCED,
    PENDING_CREATE,
    PENDING_UPDATE,
    PENDING_DELETE,
    FAILED,

    ;

    companion object {
        fun fromStorage(value: String): SyncState =
            entries.firstOrNull { it.name == value } ?: FAILED
    }
}
