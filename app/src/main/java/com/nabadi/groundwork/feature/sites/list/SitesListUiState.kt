package com.nabadi.groundwork.feature.sites.list

import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus

data class SitesListUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedStatus: SiteStatus? = null,
    val selectedPriority: SitePriority? = null,
    val sites: List<Site> = emptyList(),
    val noteCountsBySiteId: Map<SiteId, Int> = emptyMap(),
    val errorMessage: String? = null,
) {
    val isError: Boolean
        get() = errorMessage != null

    val isSearching: Boolean
        get() = searchQuery.isNotBlank()

    val isFiltering: Boolean
        get() = selectedStatus != null || selectedPriority != null

    val hasActiveCriteria: Boolean
        get() = isSearching || isFiltering

    private val hasNoVisibleSites: Boolean
        get() = !isLoading && !isError && sites.isEmpty()

    val shouldShowEmptyState: Boolean
        get() = hasNoVisibleSites && !hasActiveCriteria

    val shouldShowNoMatchesState: Boolean
        get() = hasNoVisibleSites && hasActiveCriteria

    val shouldShowContent: Boolean
        get() = !isLoading && !isError && (sites.isNotEmpty() || shouldShowNoMatchesState)

    fun noteCountFor(siteId: SiteId): Int = noteCountsBySiteId[siteId] ?: 0
}
