package com.nabadi.groundwork.feature.fieldnotes.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FieldNoteEditorRoute(
    onEditorFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FieldNoteEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FieldNoteEditorScreen(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onBodyChange = viewModel::onBodyChange,
        onSaveClick = { viewModel.saveFieldNote(onSaved = onEditorFinished) },
        onDestructiveActionClick = {
            if (uiState.isEditing) {
                viewModel.deleteFieldNote(onDeleted = onEditorFinished)
            } else {
                viewModel.discardDraft(onDiscarded = onEditorFinished)
            }
        },
        onBackClick = onEditorFinished,
        modifier = modifier,
    )
}