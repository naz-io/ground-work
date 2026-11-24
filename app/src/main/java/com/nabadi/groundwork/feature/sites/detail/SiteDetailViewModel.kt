package com.nabadi.groundwork.feature.sites.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import com.nabadi.groundwork.domain.repository.SiteRepository
import com.nabadi.groundwork.feature.fieldnotes.list.FieldNoteListItemUiState
import com.nabadi.groundwork.navigation.GroundWorkRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = SiteDetailViewModel.Factory::class)
class SiteDetailViewModel @AssistedInject constructor(
    @Assisted route: GroundWorkRoute.SiteDetail,
    siteRepository: SiteRepository,
    fieldNoteRepository: FieldNoteRepository,
) : ViewModel() {

    private val siteId: SiteId = SiteId(route.siteId)

    private val site: Flow<Site?> = siteRepository.getSitesStream()
        .map { sites -> sites.firstOrNull { it.id == siteId } }
        .distinctUntilChanged()

    // Caught here rather than downstream so a notes failure keeps the site details on screen.
    private val fieldNotes: Flow<Result<List<FieldNote>>> =
        fieldNoteRepository.getFieldNotesForSiteStream(siteId)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }

    val uiState: StateFlow<SiteDetailUiState> =
        combine(site, fieldNotes, ::Pair)
            .scan(SiteDetailUiState()) { previous, (site, fieldNotes) ->
                previous.next(site, fieldNotes)
            }
            .catch {
                emit(
                    SiteDetailUiState(
                        isLoading = false,
                        error = SiteDetailError.LOAD_FAILED,
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = SiteDetailUiState(),
            )

    private fun SiteDetailUiState.next(
        site: Site?,
        fieldNotes: Result<List<FieldNote>>,
    ): SiteDetailUiState {
        if (site == null) {
            return SiteDetailUiState(
                isLoading = false,
                error = SiteDetailError.NOT_FOUND,
            )
        }

        return SiteDetailUiState(
            siteId = site.id,
            name = site.name,
            description = site.description,
            location = site.location,
            priority = site.priority,
            status = site.status,
            fieldNoteItems = fieldNotes.fold(
                onSuccess = { notes -> fieldNoteItems.reuseOrBuild(notes, site.name) },
                onFailure = { fieldNoteItems },
            ),
            notesLoadFailed = fieldNotes.isFailure,
            isLoading = false,
        )
    }

    // A site-only emission passes the same note instances again, so keep the existing
    // rows unless a note or the site name they show changed. That way other site edits
    // keep the same row instances and the note cards skip by identity.
    private fun List<FieldNoteListItemUiState>.reuseOrBuild(
        notes: List<FieldNote>,
        siteName: String,
    ): List<FieldNoteListItemUiState> {
        val unchanged = size == notes.size &&
                indices.all { index ->
                    this[index].note === notes[index] && this[index].siteName == siteName
                }
        return if (unchanged) {
            this
        } else {
            notes.map { note -> FieldNoteListItemUiState(note = note, siteName = siteName) }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: GroundWorkRoute.SiteDetail): SiteDetailViewModel
    }
}
