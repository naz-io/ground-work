package com.nabadi.groundwork.feature.fieldnotes.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import com.nabadi.groundwork.domain.repository.SiteRepository
import com.nabadi.groundwork.navigation.GroundWorkRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FieldNoteEditorViewModel @Inject constructor(
    private val fieldNoteRepository: FieldNoteRepository,
    private val siteRepository: SiteRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val initialSiteId: SiteId? =
        savedStateHandle.get<String>(GroundWorkRoute.SITE_ID_ARG)?.let(::SiteId)

    private val fieldNoteId: FieldNoteId? =
        savedStateHandle.get<String>(GroundWorkRoute.FIELD_NOTE_ID_ARG)?.let(::FieldNoteId)
    private var existingFieldNote: FieldNote? = null

    private val _uiState = MutableStateFlow(FieldNoteEditorUiState(siteId = initialSiteId))
    val uiState: StateFlow<FieldNoteEditorUiState> = _uiState.asStateFlow()

    init {
        observeAvailableSites()

        if (fieldNoteId != null) {
            _uiState.update { it.copy(isEditing = true) }
            loadFieldNote(fieldNoteId)
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { currentState ->
            currentState.copy(
                title = title,
                errorMessage = null,
            )
        }
    }

    fun onAssociatedSiteChange(siteId: SiteId?) {
        _uiState.update { currentState ->
            currentState.copy(
                siteId = siteId,
                errorMessage = null,
            )
        }
    }

    fun onBodyChange(body: String) {
        _uiState.update { currentState ->
            currentState.copy(
                body = body,
                errorMessage = null,
            )
        }
    }

    fun onStatusChange(status: FieldNoteStatus) {
        _uiState.update {
            it.copy(
                status = status,
                errorMessage = null,
            )
        }
    }

    fun saveFieldNote(onSaved: () -> Unit) {
        if (_uiState.value.isBusy) return
        val currentState = _uiState.value

        viewModelScope.launch {
            updateSavingState(isSaving = true)

            runCatching {
                val existingNote = existingFieldNote
                val now = System.currentTimeMillis()

                fieldNoteRepository.saveFieldNote(
                    FieldNote(
                        id = existingNote?.id ?: FieldNoteId(UUID.randomUUID().toString()),
                        siteId = currentState.siteId,
                        title = currentState.title.trim(),
                        body = currentState.body,
                        status = currentState.status,
                        createdAt = existingNote?.createdAt ?: now,
                        updatedAt = now,
                    )
                )
            }.onSuccess {
                _uiState.update { FieldNoteEditorUiState() }
                onSaved()
            }.onFailure {
                updateSavingState(
                    isSaving = false,
                    errorMessage = "Unable to save field note.",
                )
            }
        }
    }

    fun deleteFieldNote(onDeleted: () -> Unit) {
        if (_uiState.value.isBusy) return

        viewModelScope.launch {
            updateDeletingState(isDeleting = true)

            runCatching {
                val existingNote = existingFieldNote
                    ?: error("Cannot delete a field note that has not been saved.")
                fieldNoteRepository.deleteFieldNote(id = existingNote.id)
            }.onSuccess {
                _uiState.update { FieldNoteEditorUiState() }
                onDeleted()
            }.onFailure {
                updateDeletingState(
                    isDeleting = false,
                    errorMessage = "Unable to delete field note.",
                )
            }
        }
    }

    fun discardChanges(onDiscarded: () -> Unit) {
        if (_uiState.value.isBusy) return

        _uiState.update { FieldNoteEditorUiState() }
        onDiscarded()
    }

    private fun observeAvailableSites() {
        viewModelScope.launch {
            siteRepository.observeSites()
                .catch {
                    _uiState.update { currentState ->
                        currentState.copy(
                            availableSites = emptyList(),
                            siteOptionsErrorMessage = "Unable to load sites.",
                        )
                    }
                }
                .collect { sites ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            availableSites = sites.map { site ->
                                SiteOptionUiState(
                                    id = site.id,
                                    name = site.name,
                                )
                            },
                            siteOptionsErrorMessage = null,
                        )
                    }
                }
        }
    }

    private fun loadFieldNote(id: FieldNoteId) {
        viewModelScope.launch {
            updateLoadingState(isLoading = true)

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
                            siteId = fieldNote.siteId,
                            body = fieldNote.body,
                            status = fieldNote.status,
                            updatedAt = fieldNote.updatedAt,
                            isEditing = true,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
            }.onFailure {
                updateLoadingState(
                    isLoading = false,
                    errorMessage = "Unable to load field note.",
                )
            }
        }
    }

    private fun updateLoadingState(
        isLoading: Boolean,
        errorMessage: String? = null,
    ) {
        _uiState.update {
            it.copy(
                isLoading = isLoading,
                errorMessage = errorMessage,
            )
        }
    }

    private fun updateSavingState(
        isSaving: Boolean,
        errorMessage: String? = null,
    ) {
        _uiState.update {
            it.copy(
                isSaving = isSaving,
                errorMessage = errorMessage,
            )
        }
    }

    private fun updateDeletingState(
        isDeleting: Boolean,
        errorMessage: String? = null,
    ) {
        _uiState.update {
            it.copy(
                isDeleting = isDeleting,
                errorMessage = errorMessage,
            )
        }
    }
}
