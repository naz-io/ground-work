package com.nabadi.groundwork.feature.fieldnotes.editor

import androidx.annotation.StringRes
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.SiteId

data class FieldNoteEditorUiState(
    val title: String = "",
    val siteId: SiteId? = null,
    val availableSites: List<SiteOptionUiState> = emptyList(),
    val body: String = "",
    val status: FieldNoteStatus = FieldNoteStatus.DRAFT,
    val updatedAt: Long? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val siteOptionsLoadFailed: Boolean = false,
    val error: FieldNoteEditorError? = null,
    val completion: FieldNoteEditorCompletion? = null,
) {
    /** True after a save, delete, or discard succeeds, while the editor navigates away. */
    val isClosing: Boolean
        get() = completion != null

    // A closing editor is still on screen, so it counts as busy:
    // no second save, delete, or discard can start meanwhile.
    val isBusy: Boolean
        get() = isLoading || isSaving || isDeleting || isClosing

    val canSave: Boolean
        get() = !isBusy && (title.isNotBlank() || body.isNotBlank())
}

data class SiteOptionUiState(
    val id: SiteId,
    val name: String,
)

/** How the editor finished; the Route navigates when this is set. */
enum class FieldNoteEditorCompletion {
    SAVED,
    DELETED,
    DISCARDED,
}

enum class FieldNoteEditorError(@StringRes val messageRes: Int) {
    NOT_FOUND(R.string.field_note_editor_error_not_found),
    LOAD_FAILED(R.string.field_note_editor_error_load_failed),
    SAVE_FAILED(R.string.field_note_editor_error_save_failed),
    DELETE_FAILED(R.string.field_note_editor_error_delete_failed),
}
