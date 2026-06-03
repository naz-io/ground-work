package com.nabadi.groundwork.feature.fieldnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class FieldNotesListViewModel @Inject constructor(
    fieldNoteRepository: FieldNoteRepository,
) : ViewModel() {

    private val selectedStatus = MutableStateFlow<FieldNoteStatus?>(null)

    val uiState: StateFlow<FieldNotesListUiState> =
        combine(
            fieldNoteRepository.observeFieldNotes(),
            selectedStatus,
        ) { fieldNotes, selectedStatus ->
            val filteredFieldNotes = selectedStatus?.let { status ->
                fieldNotes.filter { fieldNote -> fieldNote.status == status }
            } ?: fieldNotes

            FieldNotesListUiState(
                isLoading = false,
                selectedStatus = selectedStatus,
                fieldNotes = filteredFieldNotes,
            )
        }.catch {
            emit(
                FieldNotesListUiState(
                    isLoading = false,
                    selectedStatus = selectedStatus.value,
                    errorMessage = "Unable to load field notes.",
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FieldNotesListUiState(),
        )

    fun onStatusFilterChange(status: FieldNoteStatus?) {
        selectedStatus.value = status
    }
}