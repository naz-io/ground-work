package com.nabadi.groundwork.feature.fieldnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FieldNoteEditorViewModel @Inject constructor(
    private val fieldNoteRepository: FieldNoteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FieldNoteEditorUiState())
    val uiState: StateFlow<FieldNoteEditorUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.update { currentState ->
            currentState.copy(title = title)
        }
    }

    fun onBodyChange(body: String) {
        _uiState.update { currentState ->
            currentState.copy(body = body)
        }
    }

    fun saveFieldNote(onSaved: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                val now = System.currentTimeMillis()

                fieldNoteRepository.saveFieldNote(
                    FieldNote(
                        id = FieldNoteId(UUID.randomUUID().toString()),
                        title = currentState.title.ifBlank { "(untitled field note)" },
                        body = currentState.body,
                        status = FieldNoteStatus.ACTIVE,
                        createdAt = now,
                        updatedAt = now,
                    )
                )
            }.onSuccess {
                _uiState.update { FieldNoteEditorUiState() }
                onSaved()
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Unable to save field note.",
                    )
                }
            }
        }
    }
}