package com.nabadi.groundwork.domain.model

enum class SitePriority {
    LOW,
    NORMAL,
    HIGH,

    ;

    companion object {
        fun fromStorage(value: String): SitePriority =
            entries.firstOrNull { it.name == value } ?: NORMAL
    }
}
