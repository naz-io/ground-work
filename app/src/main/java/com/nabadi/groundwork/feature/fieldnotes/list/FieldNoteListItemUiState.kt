package com.nabadi.groundwork.feature.fieldnotes.list

import com.nabadi.groundwork.domain.model.FieldNote

data class FieldNoteListItemUiState(
    val note: FieldNote,
    val siteName: String?,
)