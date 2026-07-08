package com.nabadi.groundwork.feature.fieldnotes

import androidx.annotation.StringRes
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNoteStatus


@get:StringRes
internal val FieldNoteStatus.labelResId: Int
    get() = when (this) {
        FieldNoteStatus.ACTIVE -> R.string.field_note_status_active
        FieldNoteStatus.DRAFT -> R.string.field_note_status_draft
        FieldNoteStatus.ARCHIVED -> R.string.field_note_status_archived
    }
