package com.nabadi.groundwork.feature.fieldnotes.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.ui.lifecycle.dropUnlessResumed

@Composable
fun FieldNotesListRoute(
    onFieldNoteClick: (FieldNoteId) -> Unit,
    onAddFieldNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FieldNotesListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FieldNotesListScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onStatusFilterChange = viewModel::updateStatusFilter,
        onAddFieldNoteClick = dropUnlessResumed { onAddFieldNoteClick() },
        onFieldNoteClick = onFieldNoteClick.dropUnlessResumed(),
        modifier = modifier,
    )
}