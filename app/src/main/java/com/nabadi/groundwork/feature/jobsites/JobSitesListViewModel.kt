package com.nabadi.groundwork.feature.jobsites
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.JobSitePriority
import com.nabadi.groundwork.domain.model.JobSiteStatus
import com.nabadi.groundwork.domain.repository.JobSiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class JobSitesListViewModel @Inject constructor(
    jobSiteRepository: JobSiteRepository,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow<JobSiteStatus?>(null)
    private val selectedPriority = MutableStateFlow<JobSitePriority?>(null)

    val uiState: StateFlow<JobSitesListUiState> =
        combine(
            jobSiteRepository.observeJobSites(),
            searchQuery,
            selectedStatus,
            selectedPriority,
        ) { jobSites, searchQuery, selectedStatus, selectedPriority ->
            val normalizedSearchQuery = searchQuery.trim()
            val filteredJobSites = jobSites.filter { jobSite ->
                val matchesStatus = selectedStatus == null || jobSite.status == selectedStatus
                val matchesPriority =
                    selectedPriority == null || jobSite.priority == selectedPriority
                val matchesSearchQuery = normalizedSearchQuery.isBlank() ||
                        jobSite.name.contains(normalizedSearchQuery, ignoreCase = true) ||
                        jobSite.description.contains(normalizedSearchQuery, ignoreCase = true) ||
                        jobSite.location.contains(normalizedSearchQuery, ignoreCase = true)

                matchesStatus && matchesPriority && matchesSearchQuery
            }

            JobSitesListUiState(
                isLoading = false,
                searchQuery = searchQuery,
                selectedStatus = selectedStatus,
                selectedPriority = selectedPriority,
                jobSites = filteredJobSites,
            )
        }.catch {
            emit(
                JobSitesListUiState(
                    isLoading = false,
                    searchQuery = searchQuery.value,
                    selectedStatus = selectedStatus.value,
                    selectedPriority = selectedPriority.value,
                    errorMessage = "Unable to load job sites.",
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = JobSitesListUiState(),
        )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onStatusFilterChange(status: JobSiteStatus?) {
        selectedStatus.value = status
    }

    fun onPriorityFilterChange(priority: JobSitePriority?) {
        selectedPriority.value = priority
    }

    fun onClearCriteriaClick() {
        searchQuery.value = ""
        selectedStatus.value = null
        selectedPriority.value = null
    }
}
