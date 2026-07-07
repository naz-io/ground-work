package com.nabadi.groundwork.feature.fieldnotes.editor

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
    val errorMessage: String? = null,
) {
    val isBusy: Boolean
        get() = isLoading || isSaving || isDeleting

    val canSave: Boolean
        get() = !isBusy && (title.isNotBlank() || body.isNotBlank())
}

data class SiteOptionUiState(
    val id: SiteId,
    val name: String,
)