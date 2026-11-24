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
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.time.Clock

@HiltViewModel(assistedFactory = FieldNoteEditorViewModel.Factory::class)
class FieldNoteEditorViewModel @AssistedInject constructor(
    @Assisted route: GroundWorkRoute.FieldNoteEditor,
    private val savedStateHandle: SavedStateHandle,
    private val fieldNoteRepository: FieldNoteRepository,
    siteRepository: SiteRepository,
    private val clock: Clock,
    private val idGenerator: IdGenerator,
) : ViewModel() {

    private val initialSiteId: SiteId? = route.siteId?.let(::SiteId)
    private val fieldNoteId: FieldNoteId? = route.fieldNoteId?.let(::FieldNoteId)
    private var existingFieldNote: FieldNote? = null

    // The form is state this ViewModel writes itself. The site options are a repository
    // stream, collected only while the screen is subscribed.
    // Set when the process was recreated with unsaved input in progress.
    private val hasSavedDraft: Boolean = savedStateHandle.get<Boolean>(KEY_HAS_DRAFT) == true

    private val formState = MutableStateFlow(
        FieldNoteEditorUiState(siteId = initialSiteId, isEditing = fieldNoteId != null)
            .withSavedDraft(),
    )

    private val siteOptions: Flow<SiteOptions> = siteRepository.getSitesStream()
        .map { sites ->
            SiteOptions(sites = sites.map { site -> SiteOptionUiState(id = site.id, name = site.name) })
        }
        .catch { emit(SiteOptions(loadFailed = true)) }
        // combine waits for every source, so start empty: the form never waits for the sites.
        .onStart { emit(SiteOptions()) }

    val uiState: StateFlow<FieldNoteEditorUiState> =
        combine(formState, siteOptions) { form, options ->
            form.copy(
                availableSites = options.sites,
                siteOptionsLoadFailed = options.loadFailed,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = formState.value,
        )

    init {
        // A one-shot read, not a stream: later database changes must not overwrite the form.
        if (fieldNoteId != null) loadFieldNote(fieldNoteId)
    }

    fun updateTitle(title: String) {
        editForm { it.copy(title = title, error = null) }
    }

    fun updateAssociatedSite(siteId: SiteId?) {
        editForm { it.copy(siteId = siteId, error = null) }
    }

    fun updateBody(body: String) {
        editForm { it.copy(body = body, error = null) }
    }

    fun updateStatus(status: FieldNoteStatus) {
        editForm { it.copy(status = status, error = null) }
    }

    // A ViewModel outlives configuration changes but not process death, so user input is
    // also kept in the SavedStateHandle.
    private fun editForm(transform: (FieldNoteEditorUiState) -> FieldNoteEditorUiState) {
        val form = formState.updateAndGet(transform)
        savedStateHandle[KEY_HAS_DRAFT] = true
        savedStateHandle[KEY_TITLE] = form.title
        savedStateHandle[KEY_BODY] = form.body
        savedStateHandle[KEY_STATUS] = form.status.name
        savedStateHandle[KEY_SITE_ID] = form.siteId?.value
    }

    private fun FieldNoteEditorUiState.withSavedDraft(): FieldNoteEditorUiState {
        if (!hasSavedDraft) return this
        return copy(
            title = savedStateHandle[KEY_TITLE] ?: title,
            body = savedStateHandle[KEY_BODY] ?: body,
            status = savedStateHandle.get<String>(KEY_STATUS)
                ?.let { name -> FieldNoteStatus.entries.firstOrNull { it.name == name } }
                ?: status,
            siteId = savedStateHandle.get<String>(KEY_SITE_ID)?.let(::SiteId),
        )
    }

    fun saveFieldNote() {
        if (formState.value.isBusy) return
        val currentState = formState.value

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
                        body = currentState.body.trim(),
                        status = currentState.status,
                        createdAt = existingNote?.createdAt ?: now,
                        updatedAt = now,
                    )
                )
            }.onSuccess {
                complete(FieldNoteEditorCompletion.SAVED)
            }.onFailure {
                updateSavingState(
                    isSaving = false,
                    error = FieldNoteEditorError.SAVE_FAILED,
                )
            }
        }
    }

    fun deleteFieldNote() {
        if (formState.value.isBusy) return

        viewModelScope.launch {
            updateDeletingState(isDeleting = true)

            runCatching {
                val existingNote = existingFieldNote
                    ?: error("Cannot delete a field note that has not been saved.")
                fieldNoteRepository.deleteFieldNote(id = existingNote.id)
            }.onSuccess {
                complete(FieldNoteEditorCompletion.DELETED)
            }.onFailure {
                updateDeletingState(
                    isDeleting = false,
                    error = FieldNoteEditorError.DELETE_FAILED,
                )
            }
        }
    }

    fun discardChanges() {
        if (formState.value.isBusy) return
        complete(FieldNoteEditorCompletion.DISCARDED)
    }

    // The form keeps its values while the screen leaves, so it doesn't flash empty.
    private fun complete(completion: FieldNoteEditorCompletion) {
        formState.update {
            it.copy(
                isSaving = false,
                isDeleting = false,
                completion = completion,
            )
        }
    }

    private fun loadFieldNote(id: FieldNoteId) {
        viewModelScope.launch {
            updateLoadingState(isLoading = true)

            runCatching {
                fieldNoteRepository.getFieldNote(id)
            }.onSuccess { fieldNote ->
                if (fieldNote == null) {
                    formState.update {
                        it.copy(
                            isLoading = false,
                            error = FieldNoteEditorError.NOT_FOUND,
                            isEditing = false,
                        )
                    }
                } else {
                    existingFieldNote = fieldNote
                    formState.update {
                        // A restored draft holds edits that aren't saved yet, so keep them.
                        val form = if (hasSavedDraft) {
                            it
                        } else {
                            it.copy(
                                title = fieldNote.title,
                                siteId = fieldNote.siteId,
                                body = fieldNote.body,
                                status = fieldNote.status,
                            )
                        }
                        form.copy(
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
        formState.update {
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
        formState.update {
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
        formState.update {
            it.copy(
                isDeleting = isDeleting,
                error = error,
            )
        }
    }

    private companion object {
        const val KEY_HAS_DRAFT = "draft_saved"
        const val KEY_TITLE = "draft_title"
        const val KEY_BODY = "draft_body"
        const val KEY_STATUS = "draft_status"
        const val KEY_SITE_ID = "draft_site_id"
    }

    private data class SiteOptions(
        val sites: List<SiteOptionUiState> = emptyList(),
        val loadFailed: Boolean = false,
    )

    @AssistedFactory
    interface Factory {
        fun create(route: GroundWorkRoute.FieldNoteEditor): FieldNoteEditorViewModel
    }
}
