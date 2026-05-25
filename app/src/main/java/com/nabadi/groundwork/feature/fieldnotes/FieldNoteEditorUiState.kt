package com.nabadi.groundwork.feature.fieldnotes

data class FieldNoteEditorUiState(
    val title: String = "",
    val body: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)
