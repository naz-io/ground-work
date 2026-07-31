package com.nabadi.groundwork.domain.model

enum class FieldNoteStatus {
    DRAFT,
    ACTIVE,
    ARCHIVED,

    ;

    companion object {
        fun fromStorage(value: String): FieldNoteStatus =
            entries.firstOrNull { it.name == value } ?: ARCHIVED
    }
}
