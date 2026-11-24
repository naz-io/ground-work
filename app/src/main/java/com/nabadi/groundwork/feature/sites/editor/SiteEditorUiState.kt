package com.nabadi.groundwork.feature.sites.editor

import androidx.annotation.StringRes
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus

data class SiteEditorUiState(
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val priority: SitePriority = SitePriority.NORMAL,
    val status: SiteStatus = SiteStatus.ACTIVE,
    val isEditing: Boolean = false,
    val isNameEdited: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val error: SiteEditorError? = null,
    val completion: SiteEditorCompletion? = null,
){
    /** True after a save, delete, or discard succeeds, while the editor navigates away. */
    val isClosing: Boolean
        get() = completion != null

    // A closing editor is still on screen, so it counts as busy:
    // no second save, delete, or discard can start meanwhile.
    val isBusy: Boolean
        get() = isLoading || isSaving || isDeleting || isClosing

    val canSave: Boolean
        get() = !isBusy && name.isNotBlank()

    val showNameRequiredError: Boolean
        get() = isNameEdited && name.isBlank()
}

/** How the editor finished; the Route navigates when this is set. */
enum class SiteEditorCompletion {
    SAVED,
    DELETED,
    DISCARDED,
}

enum class SiteEditorError(@StringRes val messageRes: Int) {
    NOT_FOUND(R.string.site_editor_error_not_found),
    LOAD_FAILED(R.string.site_editor_error_load_failed),
    SAVE_FAILED(R.string.site_editor_error_save_failed),
    DELETE_FAILED(R.string.site_editor_error_delete_failed),
}
