package com.nabadi.groundwork.feature.fieldnotes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.FieldNoteStatus
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
class FieldNotesListViewModel @Inject constructor(
    fieldNoteRepository: FieldNoteRepository,
    siteRepository: SiteRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow<FieldNoteStatus?>(null)

    val uiState: StateFlow<FieldNotesListUiState> = combine(
        fieldNoteRepository.observeFieldNotes(),
        siteRepository.observeSites(),
        searchQuery,
        selectedStatus,
    ) { fieldNotes, sites, searchQuery, selectedStatus ->
        val normalizedSearchQuery = searchQuery.trim()
        val siteNamesBySiteId = sites.associate { site ->
            site.id to site.name
        }
        val fieldNoteItems = fieldNotes.map { fieldNote ->
            FieldNoteListItemUiState(
                note = fieldNote,
                siteName = fieldNote.siteId?.let(siteNamesBySiteId::get),
            )
        }

        val filteredFieldNoteItems = fieldNoteItems.filter { item ->
            val matchesStatus = selectedStatus == null || item.note.status == selectedStatus
            val matchesSearchQuery = item.matchesSearchQuery(normalizedSearchQuery)

            matchesStatus && matchesSearchQuery
        }

        FieldNotesListUiState(
            isLoading = false,
            searchQuery = searchQuery,
            selectedStatus = selectedStatus,
            fieldNoteItems = filteredFieldNoteItems,
        )
    }.catch {
        emit(
            FieldNotesListUiState(
                isLoading = false,
                searchQuery = searchQuery.value,
                selectedStatus = selectedStatus.value,
                errorMessage = "Unable to load field notes.",
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = FieldNotesListUiState(),
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onStatusFilterChange(status: FieldNoteStatus?) {
        selectedStatus.value = status
    }
}

private fun FieldNoteListItemUiState.matchesSearchQuery(query: String): Boolean {
    return query.isBlank() ||
            note.title.contains(query, ignoreCase = true) ||
            note.body.contains(query, ignoreCase = true) ||
            siteName.orEmpty().contains(query, ignoreCase = true)
}