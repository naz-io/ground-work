package com.nabadi.groundwork.feature.fieldnotes

data class FieldNoteEditorUiState(
    val title: String = "",
    val body: String = "",
    val isSaving: Boolean = false,
    val isLocalDraft: Boolean = true,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = !isSaving
                && (title.isNotBlank() || body.isNotBlank())
                && errorMessage == null
}
