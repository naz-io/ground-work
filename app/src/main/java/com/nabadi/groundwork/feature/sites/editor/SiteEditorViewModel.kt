package com.nabadi.groundwork.feature.sites.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.domain.repository.SiteRepository
import com.nabadi.groundwork.navigation.GroundWorkRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SiteEditorViewModel @Inject constructor(
    private val siteRepository: SiteRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val siteId: SiteId? =
        savedStateHandle.get<String>(GroundWorkRoute.SITE_ID_ARG)?.let(::SiteId)
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
                errorMessage = null,
            )
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { currentState ->
            currentState.copy(
                description = description,
                errorMessage = null,
            )
        }
    }

    fun onLocationChange(location: String) {
        _uiState.update { currentState ->
            currentState.copy(
                location = location,
                errorMessage = null,
            )
        }
    }

    fun onStatusChange(status: SiteStatus) {
        _uiState.update { currentState ->
            currentState.copy(
                status = status,
                errorMessage = null,
            )
        }
    }

    fun onPriorityChange(priority: SitePriority) {
        _uiState.update { currentState ->
            currentState.copy(
                priority = priority,
                errorMessage = null,
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
                val now = System.currentTimeMillis()

                siteRepository.saveSite(
                    Site(
                        id = existingSite?.id ?: SiteId(UUID.randomUUID().toString()),
                        name = currentState.name,
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
                    errorMessage = "Unable to save site.",
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
                    errorMessage = "Unable to delete site.",
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
                            errorMessage = "Site not found.",
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
                            errorMessage = null,
                        )
                    }
                }
            }.onFailure {
                updateLoadingState(
                    isLoading = false,
                    errorMessage = "Unable to load site.",
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