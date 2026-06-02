package com.nabadi.groundwork.feature.fieldnotes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import com.nabadi.groundwork.navigation.GroundWorkRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FieldNoteEditorViewModel @Inject constructor(
    private val fieldNoteRepository: FieldNoteRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val fieldNoteId: FieldNoteId? =
        savedStateHandle.get<String>(GroundWorkRoute.FIELD_NOTE_ID_ARG)?.let(::FieldNoteId)
    private var existingFieldNote: FieldNote? = null

    private val _uiState = MutableStateFlow(FieldNoteEditorUiState())
    val uiState: StateFlow<FieldNoteEditorUiState> = _uiState.asStateFlow()

    init {
        if (fieldNoteId != null) {
            _uiState.update { it.copy(isEditing = true) }
            loadFieldNote(fieldNoteId)
        }
    }

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
        if (currentState.isSaving || currentState.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                val existingNote = existingFieldNote
                val now = System.currentTimeMillis()

                fieldNoteRepository.saveFieldNote(
                    FieldNote(
                        id = existingNote?.id ?: FieldNoteId(UUID.randomUUID().toString()),
                        title = currentState.title.ifBlank { "(untitled field note)" },
                        body = currentState.body,
                        status = existingNote?.status ?: FieldNoteStatus.ACTIVE,
                        createdAt = existingNote?.createdAt ?: now,
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

    private fun loadFieldNote(id: FieldNoteId) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                fieldNoteRepository.getFieldNote(id)
            }.onSuccess { fieldNote ->
                if (fieldNote == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Field note not found.",
                            isEditing = false,
                        )
                    }
                } else {
                    existingFieldNote = fieldNote
                    _uiState.update {
                        it.copy(
                            title = fieldNote.title,
                            body = fieldNote.body,
                            isEditing = true,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unable to load field note.",
                    )
                }

            }
        }
    }
}