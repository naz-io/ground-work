package com.nabadi.groundwork.feature.sites.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import com.nabadi.groundwork.domain.repository.SiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SitesListViewModel @Inject constructor(
    siteRepository: SiteRepository,
    fieldNoteRepository: FieldNoteRepository,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow<SiteStatus?>(null)
    private val selectedPriority = MutableStateFlow<SitePriority?>(null)

    val uiState: StateFlow<SitesListUiState> =
        combine(
            siteRepository.observeSites(),
            fieldNoteRepository.observeFieldNotes(),
            searchQuery,
            selectedStatus,
            selectedPriority,
        ) { sites, fieldNotes, searchQuery, selectedStatus, selectedPriority ->
            val normalizedSearchQuery = searchQuery.trim()
            val filteredSites = sites.filter { site ->
                val matchesStatus = selectedStatus == null || site.status == selectedStatus
                val matchesPriority =
                    selectedPriority == null || site.priority == selectedPriority
                val matchesSearchQuery = normalizedSearchQuery.isBlank() ||
                        site.name.contains(normalizedSearchQuery, ignoreCase = true) ||
                        site.description.contains(normalizedSearchQuery, ignoreCase = true) ||
                        site.location.contains(normalizedSearchQuery, ignoreCase = true)

                matchesStatus && matchesPriority && matchesSearchQuery
            }

            SitesListUiState(
                isLoading = false,
                searchQuery = searchQuery,
                selectedStatus = selectedStatus,
                selectedPriority = selectedPriority,
                sites = filteredSites,
                noteCountsBySiteId = fieldNotes
                    .mapNotNull { it.siteId }
                    .groupingBy { it }
                    .eachCount(),
            )
        }.catch {
            emit(
                SitesListUiState(
                    isLoading = false,
                    searchQuery = searchQuery.value,
                    selectedStatus = selectedStatus.value,
                    selectedPriority = selectedPriority.value,
                    errorMessage = "Unable to load sites.",
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SitesListUiState(),
        )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onStatusFilterChange(status: SiteStatus?) {
        selectedStatus.value = status
    }

    fun onPriorityFilterChange(priority: SitePriority?) {
        selectedPriority.value = priority
    }

    fun onClearCriteriaClick() {
        searchQuery.value = ""
        selectedStatus.value = null
        selectedPriority.value = null
    }
}
