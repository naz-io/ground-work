package com.nabadi.groundwork.feature.fieldnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FieldNotesViewModel @Inject constructor(
    fieldNoteRepository: FieldNoteRepository
) : ViewModel() {

    val uiState: StateFlow<FieldNotesUiState> =
        fieldNoteRepository.observeFieldNotes()
            .map { fieldNotes ->
                FieldNotesUiState(
                    isLoading = false,
                    fieldNotes = fieldNotes,
                )
            }
            .catch {
                emit(
                    FieldNotesUiState(
                        isLoading = false,
                        errorMessage = "Unable to load field notes."
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FieldNotesUiState()
            )

}