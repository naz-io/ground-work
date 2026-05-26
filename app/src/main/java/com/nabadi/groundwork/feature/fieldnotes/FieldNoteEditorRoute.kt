package com.nabadi.groundwork.feature.fieldnotes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FieldNoteEditorRoute(
    modifier: Modifier = Modifier,
    onFieldNoteSaved: () -> Unit,
    viewModel: FieldNoteEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FieldNoteEditorScreen(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onBodyChange = viewModel::onBodyChange,
        onSaveClick = { viewModel.saveFieldNote(onSaved = onFieldNoteSaved) },
        modifier = modifier,
    )
}