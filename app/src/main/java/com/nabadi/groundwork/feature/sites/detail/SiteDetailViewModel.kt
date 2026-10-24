package com.nabadi.groundwork.feature.sites.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import com.nabadi.groundwork.domain.repository.SiteRepository
import com.nabadi.groundwork.navigation.GroundWorkRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = SiteDetailViewModel.Factory::class)
class SiteDetailViewModel @AssistedInject constructor(
    @Assisted route: GroundWorkRoute.SiteDetail,
    private val siteRepository: SiteRepository,
    private val fieldNoteRepository: FieldNoteRepository,
) : ViewModel() {

    private val siteId: SiteId = SiteId(route.siteId)

    private val _uiState: MutableStateFlow<SiteDetailUiState> =
        MutableStateFlow(SiteDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeSite(siteId)
        observeFieldNotes(siteId)
    }

    private fun observeSite(id: SiteId) {
        viewModelScope.launch {
            updateLoadingState(isLoading = true)

            siteRepository.observeSites()
                .catch {
                    updateLoadingState(
                        isLoading = false,
                        error = SiteDetailError.LOAD_FAILED,
                    )
                }
                .collect { sites ->
                    val site = sites.firstOrNull { it.id == id }
                    if (site == null) {
                        _uiState.update {
                            it.copy(
                                siteId = null,
                                isLoading = false,
                                error = SiteDetailError.NOT_FOUND,
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                siteId = site.id,
                                name = site.name,
                                description = site.description,
                                location = site.location,
                                priority = site.priority,
                                status = site.status,
                                isLoading = false,
                                error = null,
                            )
                        }
                    }
                }
        }
    }

    private fun observeFieldNotes(id: SiteId) {
        viewModelScope.launch {
            fieldNoteRepository.observeFieldNotesForSite(id)
                .catch {
                    _uiState.update { state ->
                        state.copy(notesLoadFailed = true)
                    }
                }
                .collect { fieldNotes ->
                    _uiState.update { state ->
                        state.copy(
                            fieldNotes = fieldNotes,
                            notesLoadFailed = false,
                        )
                    }
                }
        }
    }

    private fun updateLoadingState(
        isLoading: Boolean,
        error: SiteDetailError? = null,
    ) {
        _uiState.update {
            it.copy(
                isLoading = isLoading,
                error = error,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: GroundWorkRoute.SiteDetail): SiteDetailViewModel
    }
}
