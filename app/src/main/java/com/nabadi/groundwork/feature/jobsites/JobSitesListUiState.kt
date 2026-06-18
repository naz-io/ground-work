package com.nabadi.groundwork.feature.jobsites

import com.nabadi.groundwork.domain.model.JobSite
import com.nabadi.groundwork.domain.model.JobSitePriority
import com.nabadi.groundwork.domain.model.JobSiteStatus

data class JobSitesListUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedStatus: JobSiteStatus? = null,
    val selectedPriority: JobSitePriority? = null,
    val jobSites: List<JobSite> = emptyList(),
    val errorMessage: String? = null,
) {
    val isSearching: Boolean
        get() = searchQuery.isNotBlank()

    val isFiltering: Boolean
        get() = selectedStatus != null || selectedPriority != null

    val hasActiveCriteria: Boolean
        get() = isSearching || isFiltering

    val isEmpty: Boolean
        get() = !isLoading && !isError && !hasActiveCriteria && jobSites.isEmpty()

    val isNoMatch: Boolean
        get() = !isLoading && !isError && hasActiveCriteria && jobSites.isEmpty()

    val isError: Boolean
        get() = errorMessage != null
}
