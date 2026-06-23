package com.nabadi.groundwork.feature.fieldnotes.editor

data class FieldNoteEditorUiState(
    val title: String = "",
    val body: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isLocalDraft: Boolean = true,
    val errorMessage: String? = null,
) {
    val isBusy: Boolean
        get() = isLoading || isSaving || isDeleting

    val canSave: Boolean
        get() = !isBusy && (title.isNotBlank() || body.isNotBlank())
}