package com.nabadi.groundwork.feature.fieldnotes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FieldNotesListRoute(
    modifier: Modifier = Modifier,
    onAddFieldNoteClick: () -> Unit,
    viewModel: FieldNotesListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FieldNotesListScreen(
        uiState = uiState,
        onAddFieldNoteClick = onAddFieldNoteClick,
        modifier = modifier
    )
}