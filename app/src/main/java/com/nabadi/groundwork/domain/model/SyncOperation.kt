package com.nabadi.groundwork.domain.model

enum class SyncOperation {
    CREATE,
    UPDATE,
    DELETE,
    ;

    companion object {
        fun fromStorage(value: String?): SyncOperation? =
            value?.let { storedValue ->
                entries.firstOrNull { it.name == storedValue }
            }
    }
}
