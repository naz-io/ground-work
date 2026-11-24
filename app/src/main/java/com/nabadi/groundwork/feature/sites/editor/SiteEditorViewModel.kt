package com.nabadi.groundwork.feature.sites.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.IdGenerator
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.domain.repository.SiteRepository
import com.nabadi.groundwork.navigation.GroundWorkRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.time.Clock

@HiltViewModel(assistedFactory = SiteEditorViewModel.Factory::class)
class SiteEditorViewModel @AssistedInject constructor(
    @Assisted route: GroundWorkRoute.SiteEditor,
    private val savedStateHandle: SavedStateHandle,
    private val siteRepository: SiteRepository,
    private val clock: Clock,
    private val idGenerator: IdGenerator,
) : ViewModel() {

    private val siteId: SiteId? = route.siteId?.let(::SiteId)
    private var existingSite: Site? = null

    // Set when the process was recreated with unsaved input in progress.
    private val hasSavedDraft: Boolean = savedStateHandle.get<Boolean>(KEY_HAS_DRAFT) == true

    private val _uiState = MutableStateFlow(SiteEditorUiState().withSavedDraft())
    val uiState: StateFlow<SiteEditorUiState> = _uiState.asStateFlow()

    init {
        if (siteId != null) {
            _uiState.update { it.copy(isEditing = true) }
            loadSite(siteId)
        }
    }

    fun updateName(name: String) {
        editForm { it.copy(name = name, isNameEdited = true, error = null) }
    }

    fun updateDescription(description: String) {
        editForm { it.copy(description = description, error = null) }
    }

    fun updateLocation(location: String) {
        editForm { it.copy(location = location, error = null) }
    }

    fun updateStatus(status: SiteStatus) {
        editForm { it.copy(status = status, error = null) }
    }

    fun updatePriority(priority: SitePriority) {
        editForm { it.copy(priority = priority, error = null) }
    }

    // A ViewModel outlives configuration changes but not process death, so user input is
    // also kept in the SavedStateHandle.
    private fun editForm(transform: (SiteEditorUiState) -> SiteEditorUiState) {
        val form = _uiState.updateAndGet(transform)
        savedStateHandle[KEY_HAS_DRAFT] = true
        savedStateHandle[KEY_NAME] = form.name
        savedStateHandle[KEY_DESCRIPTION] = form.description
        savedStateHandle[KEY_LOCATION] = form.location
        savedStateHandle[KEY_STATUS] = form.status.name
        savedStateHandle[KEY_PRIORITY] = form.priority.name
        savedStateHandle[KEY_NAME_EDITED] = form.isNameEdited
    }

    private fun SiteEditorUiState.withSavedDraft(): SiteEditorUiState {
        if (!hasSavedDraft) return this
        return copy(
            name = savedStateHandle[KEY_NAME] ?: name,
            description = savedStateHandle[KEY_DESCRIPTION] ?: description,
            location = savedStateHandle[KEY_LOCATION] ?: location,
            status = savedStateHandle.get<String>(KEY_STATUS)
                ?.let { saved -> SiteStatus.entries.firstOrNull { it.name == saved } }
                ?: status,
            priority = savedStateHandle.get<String>(KEY_PRIORITY)
                ?.let { saved -> SitePriority.entries.firstOrNull { it.name == saved } }
                ?: priority,
            isNameEdited = savedStateHandle[KEY_NAME_EDITED] ?: isNameEdited,
        )
    }

    fun saveSite() {
        val currentState = _uiState.value
        if (currentState.isBusy || currentState.name.isBlank()) return

        viewModelScope.launch {
            updateSavingState(isSaving = true)

            runCatching {
                val existingSite = existingSite
                val now = clock.millis()

                siteRepository.saveSite(
                    Site(
                        id = existingSite?.id ?: SiteId(idGenerator.newId()),
                        name = currentState.name.trim(),
                        description = currentState.description.trim(),
                        location = currentState.location.trim(),
                        priority = currentState.priority,
                        status = currentState.status,
                        createdAt = existingSite?.createdAt ?: now,
                        updatedAt = now,
                    )
                )
            }.onSuccess {
                complete(SiteEditorCompletion.SAVED)
            }.onFailure {
                updateSavingState(
                    isSaving = false,
                    error = SiteEditorError.SAVE_FAILED,
                )
            }
        }
    }

    fun deleteSite() {
        val currentState = _uiState.value
        val siteToDelete = existingSite
        if (currentState.isBusy || siteToDelete == null) return

        viewModelScope.launch {
            updateDeletingState(isDeleting = true)
            runCatching {
                siteRepository.deleteSite(id = siteToDelete.id)
            }.onSuccess {
                complete(SiteEditorCompletion.DELETED)
            }.onFailure {
                updateDeletingState(
                    isDeleting = false,
                    error = SiteEditorError.DELETE_FAILED,
                )
            }
        }
    }

    fun discardSite() {
        if (_uiState.value.isBusy) return
        complete(SiteEditorCompletion.DISCARDED)
    }

    // The form keeps its values while the screen leaves, so it doesn't flash empty.
    private fun complete(completion: SiteEditorCompletion) {
        _uiState.update {
            it.copy(
                isSaving = false,
                isDeleting = false,
                completion = completion,
            )
        }
    }

    private fun loadSite(id: SiteId) {
        viewModelScope.launch {
            updateLoadingState(isLoading = true)

            runCatching {
                siteRepository.getSite(id)
            }.onSuccess { site ->
                if (site == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = SiteEditorError.NOT_FOUND,
                            isEditing = false,
                        )
                    }
                } else {
                    existingSite = site
                    _uiState.update {
                        // A restored draft holds edits that aren't saved yet, so keep them.
                        val form = if (hasSavedDraft) {
                            it
                        } else {
                            it.copy(
                                name = site.name,
                                description = site.description,
                                location = site.location,
                                priority = site.priority,
                                status = site.status,
                            )
                        }
                        form.copy(
                            isEditing = true,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
            }.onFailure {
                updateLoadingState(
                    isLoading = false,
                    error = SiteEditorError.LOAD_FAILED,
                )
            }
        }
    }

    private fun updateLoadingState(
        isLoading: Boolean,
        error: SiteEditorError? = null,
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
        error: SiteEditorError? = null,
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
        error: SiteEditorError? = null,
    ) {
        _uiState.update {
            it.copy(
                isDeleting = isDeleting,
                error = error,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: GroundWorkRoute.SiteEditor): SiteEditorViewModel
    }

    private companion object {
        const val KEY_HAS_DRAFT = "draft_saved"
        const val KEY_NAME = "draft_name"
        const val KEY_DESCRIPTION = "draft_description"
        const val KEY_LOCATION = "draft_location"
        const val KEY_STATUS = "draft_status"
        const val KEY_PRIORITY = "draft_priority"
        const val KEY_NAME_EDITED = "draft_name_edited"
    }
}
