package com.nabadi.groundwork.feature.sites.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import com.nabadi.groundwork.domain.repository.SiteRepository
import com.nabadi.groundwork.ui.format.minuteTicks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import javax.inject.Inject

@HiltViewModel
class SitesListViewModel @Inject constructor(
    siteRepository: SiteRepository,
    fieldNoteRepository: FieldNoteRepository,
    clock: Clock,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow<SiteStatus?>(null)
    private val selectedPriority = MutableStateFlow<SitePriority?>(null)

    val uiState: StateFlow<SitesListUiState> =
        combine(
            siteRepository.getSitesStream(),
            fieldNoteRepository.getFieldNotesStream(),
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
        }.combine(clock.minuteTicks()) { state, nowMillis ->
            // Combined after filtering so a minute tick only refreshes the time.
            state.copy(nowMillis = nowMillis)
        }.catch {
            emit(
                SitesListUiState(
                    isLoading = false,
                    searchQuery = searchQuery.value,
                    selectedStatus = selectedStatus.value,
                    selectedPriority = selectedPriority.value,
                    error = SitesListError.LOAD_FAILED,
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SitesListUiState(),
        )

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateStatusFilter(status: SiteStatus?) {
        selectedStatus.value = status
    }

    fun updatePriorityFilter(priority: SitePriority?) {
        selectedPriority.value = priority
    }

    fun clearCriteria() {
        searchQuery.value = ""
        selectedStatus.value = null
        selectedPriority.value = null
    }
}
