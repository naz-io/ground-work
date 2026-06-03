package com.nabadi.groundwork.feature.fieldnotes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nabadi.groundwork.domain.model.FieldNoteId

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
        onStatusFilterChange = viewModel::onStatusFilterChange,
        onAddFieldNoteClick = onAddFieldNoteClick,
        onFieldNoteClick = onFieldNoteClick,
        modifier = modifier,
    )
}