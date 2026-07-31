package com.nabadi.groundwork.domain.model

enum class SiteStatus {
    ACTIVE,
    ARCHIVED,

    ;

    companion object {
        fun fromStorage(value: String): SiteStatus =
            entries.firstOrNull { it.name == value } ?: ARCHIVED
    }
}
