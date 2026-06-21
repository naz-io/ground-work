package com.nabadi.groundwork.feature.sites

import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus

data class SiteEditorUiState(
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val priority: SitePriority = SitePriority.NORMAL,
    val status: SiteStatus = SiteStatus.ACTIVE,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
){
    val isBusy: Boolean
        get() = isLoading || isSaving || isDeleting

    val canSave: Boolean
        get() = !isBusy && name.isNotBlank()
}
