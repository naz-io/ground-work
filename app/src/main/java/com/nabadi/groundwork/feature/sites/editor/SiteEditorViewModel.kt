package com.nabadi.groundwork.feature.sites.editor

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
import kotlinx.coroutines.launch
import java.time.Clock

@HiltViewModel(assistedFactory = SiteEditorViewModel.Factory::class)
class SiteEditorViewModel @AssistedInject constructor(
    @Assisted route: GroundWorkRoute.SiteEditor,
    private val siteRepository: SiteRepository,
    private val clock: Clock,
    private val idGenerator: IdGenerator,
) : ViewModel() {

    private val siteId: SiteId? = route.siteId?.let(::SiteId)
    private var existingSite: Site? = null

    private val _uiState = MutableStateFlow(SiteEditorUiState())
    val uiState: StateFlow<SiteEditorUiState> = _uiState.asStateFlow()

    init {
        if (siteId != null) {
            _uiState.update { it.copy(isEditing = true) }
            loadSite(siteId)
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                name = name,
                isNameEdited = true,
                error = null,
            )
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { currentState ->
            currentState.copy(
                description = description,
                error = null,
            )
        }
    }

    fun onLocationChange(location: String) {
        _uiState.update { currentState ->
            currentState.copy(
                location = location,
                error = null,
            )
        }
    }

    fun onStatusChange(status: SiteStatus) {
        _uiState.update { currentState ->
            currentState.copy(
                status = status,
                error = null,
            )
        }
    }

    fun onPriorityChange(priority: SitePriority) {
        _uiState.update { currentState ->
            currentState.copy(
                priority = priority,
                error = null,
            )
        }
    }

    fun saveSite(onSaved: () -> Unit) {
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
                        description = currentState.description,
                        location = currentState.location,
                        priority = currentState.priority,
                        status = currentState.status,
                        createdAt = existingSite?.createdAt ?: now,
                        updatedAt = now,
                    )
                )
            }.onSuccess {
                existingSite = null
                _uiState.update { SiteEditorUiState() }
                onSaved()
            }.onFailure {
                updateSavingState(
                    isSaving = false,
                    error = SiteEditorError.SAVE_FAILED,
                )
            }
        }
    }

    fun deleteSite(onDeleted: () -> Unit) {
        val currentState = _uiState.value
        val siteToDelete = existingSite
        if (currentState.isBusy || siteToDelete == null) return

        viewModelScope.launch {
            updateDeletingState(isDeleting = true)
            runCatching {
                siteRepository.deleteSite(id = siteToDelete.id)
            }.onSuccess {
                existingSite = null
                _uiState.update { SiteEditorUiState() }
                onDeleted()
            }.onFailure {
                updateDeletingState(
                    isDeleting = false,
                    error = SiteEditorError.DELETE_FAILED,
                )
            }
        }
    }

    fun discardSite(onDiscarded: () -> Unit) {
        if (_uiState.value.isBusy) return
        existingSite = null
        _uiState.update { SiteEditorUiState() }
        onDiscarded()
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
                        it.copy(
                            name = site.name,
                            description = site.description,
                            location = site.location,
                            priority = site.priority,
                            status = site.status,
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
}
