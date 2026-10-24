package com.nabadi.groundwork.feature.sites.detail

import androidx.annotation.StringRes
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus

data class SiteDetailUiState(
    val siteId: SiteId? = null,
    val name: String = "",
    val location: String = "",
    val priority: SitePriority = SitePriority.NORMAL,
    val status: SiteStatus = SiteStatus.ACTIVE,
    val description: String = "",
    val fieldNotes: List<FieldNote> = emptyList(),
    val isLoading: Boolean = false,
    val error: SiteDetailError? = null,
    val notesLoadFailed: Boolean = false,
) {
    val isBusy: Boolean
        get() = isLoading

    val isError: Boolean
        get() = error != null

    val canEdit: Boolean
        get() = siteId != null && !isBusy && !isError

    val canAddFieldNote: Boolean
        get() = siteId != null && !isBusy && !isError

    val hasNotes: Boolean
        get() = fieldNotes.isNotEmpty()

    val noteCount: Int
        get() = fieldNotes.size
}

enum class SiteDetailError(@StringRes val messageRes: Int) {
    NOT_FOUND(R.string.site_detail_error_not_found),
    LOAD_FAILED(R.string.site_detail_error_load_failed),
}
