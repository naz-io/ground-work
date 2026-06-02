package com.nabadi.groundwork.feature.fieldnotes

data class FieldNoteEditorUiState(
    val title: String = "",
    val body: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isLocalDraft: Boolean = true,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = !isLoading
                && !isSaving
                && (title.isNotBlank() || body.isNotBlank())
                && errorMessage == null
}
