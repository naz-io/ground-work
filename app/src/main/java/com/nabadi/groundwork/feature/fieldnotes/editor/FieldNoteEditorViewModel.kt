package com.nabadi.groundwork.feature.fieldnotes.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.IdGenerator
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
import java.time.Clock
import javax.inject.Inject

@HiltViewModel
class FieldNoteEditorViewModel @Inject constructor(
    private val fieldNoteRepository: FieldNoteRepository,
    private val siteRepository: SiteRepository,
    private val clock: Clock,
    private val idGenerator: IdGenerator,
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
                error = null,
            )
        }
    }

    fun onAssociatedSiteChange(siteId: SiteId?) {
        _uiState.update { currentState ->
            currentState.copy(
                siteId = siteId,
                error = null,
            )
        }
    }

    fun onBodyChange(body: String) {
        _uiState.update { currentState ->
            currentState.copy(
                body = body,
                error = null,
            )
        }
    }

    fun onStatusChange(status: FieldNoteStatus) {
        _uiState.update {
            it.copy(
                status = status,
                error = null,
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
                val now = clock.millis()

                fieldNoteRepository.saveFieldNote(
                    FieldNote(
                        id = existingNote?.id ?: FieldNoteId(idGenerator.newId()),
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
                    error = FieldNoteEditorError.SAVE_FAILED,
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
                    error = FieldNoteEditorError.DELETE_FAILED,
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
                            siteOptionsLoadFailed = true,
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
                            siteOptionsLoadFailed = false,
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
                            error = FieldNoteEditorError.NOT_FOUND,
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
                            error = null,
                        )
                    }
                }
            }.onFailure {
                updateLoadingState(
                    isLoading = false,
                    error = FieldNoteEditorError.LOAD_FAILED,
                )
            }
        }
    }

    private fun updateLoadingState(
        isLoading: Boolean,
        error: FieldNoteEditorError? = null,
    ) {
        _uiState.update {
            it.copy(
                isLoading = isLoading,
                error = error,
            )
        }
    }

    private fun updateSavingState(
        isSaving: Boolean,
        error: FieldNoteEditorError? = null,
    ) {
        _uiState.update {
            it.copy(
                isSaving = isSaving,
                error = error,
            )
        }
    }

    private fun updateDeletingState(
        isDeleting: Boolean,
        error: FieldNoteEditorError? = null,
    ) {
        _uiState.update {
            it.copy(
                isDeleting = isDeleting,
                error = error,
            )
        }
    }
}
