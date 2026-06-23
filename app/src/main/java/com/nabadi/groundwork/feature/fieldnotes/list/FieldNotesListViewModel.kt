package com.nabadi.groundwork.feature.fieldnotes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
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
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow<FieldNoteStatus?>(null)

    val uiState: StateFlow<FieldNotesListUiState> =
        combine(
            fieldNoteRepository.observeFieldNotes(),
            searchQuery,
            selectedStatus,
        ) { fieldNotes, searchQuery, selectedStatus ->
            val normalizedSearchQuery = searchQuery.trim()
            val filteredFieldNotes = fieldNotes.filter { fieldNote ->
                val matchesStatus = selectedStatus == null || fieldNote.status == selectedStatus
                val matchesSearchQuery = normalizedSearchQuery.isBlank() ||
                        fieldNote.title.contains(normalizedSearchQuery, ignoreCase = true) ||
                        fieldNote.body.contains(normalizedSearchQuery, ignoreCase = true)

                matchesStatus && matchesSearchQuery
            }

            FieldNotesListUiState(
                isLoading = false,
                searchQuery = searchQuery,
                selectedStatus = selectedStatus,
                fieldNotes = filteredFieldNotes,
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