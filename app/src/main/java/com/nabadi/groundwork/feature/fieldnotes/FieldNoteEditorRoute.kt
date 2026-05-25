package com.nabadi.groundwork.feature.fieldnotes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FieldNoteEditorRoute(
    modifier: Modifier = Modifier,
    viewModel: FieldNoteEditorViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FieldNoteEditorScreen(
        uiState = uiState,
        modifier = modifier,
    )
}