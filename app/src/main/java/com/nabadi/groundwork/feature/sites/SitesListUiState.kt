package com.nabadi.groundwork.feature.sites

import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus

data class SitesListUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedStatus: SiteStatus? = null,
    val selectedPriority: SitePriority? = null,
    val sites: List<Site> = emptyList(),
    val errorMessage: String? = null,
) {
    val isSearching: Boolean
        get() = searchQuery.isNotBlank()

    val isFiltering: Boolean
        get() = selectedStatus != null || selectedPriority != null

    val hasActiveCriteria: Boolean
        get() = isSearching || isFiltering

    val isEmpty: Boolean
        get() = !isLoading && !isError && !hasActiveCriteria && sites.isEmpty()

    val isNoMatch: Boolean
        get() = !isLoading && !isError && hasActiveCriteria && sites.isEmpty()

    val isError: Boolean
        get() = errorMessage != null
}
