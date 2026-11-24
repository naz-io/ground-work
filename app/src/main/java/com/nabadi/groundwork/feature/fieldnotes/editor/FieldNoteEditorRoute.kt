package com.nabadi.groundwork.feature.fieldnotes.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.nabadi.groundwork.navigation.GroundWorkRoute

@Composable
fun FieldNoteEditorRoute(
    route: GroundWorkRoute.FieldNoteEditor,
    onEditorFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FieldNoteEditorViewModel = hiltViewModel<FieldNoteEditorViewModel, FieldNoteEditorViewModel.Factory>(
        creationCallback = { factory -> factory.create(route) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigation reacts to the finished state rather than being called by the
    // ViewModel, so it always uses this composition's callback and only runs
    // while the screen is started.
    val currentOnEditorFinished by rememberUpdatedState(onEditorFinished)
    val completion = uiState.completion
    LaunchedEffect(completion) {
        if (completion != null) currentOnEditorFinished()
    }

    FieldNoteEditorScreen(
        uiState = uiState,
        onTitleChange = viewModel::updateTitle,
        onAssociatedSiteChange = viewModel::updateAssociatedSite,
        onStatusChange = viewModel::updateStatus,
        onBodyChange = viewModel::updateBody,
        onSaveClick = viewModel::saveFieldNote,
        onDeleteClick = viewModel::deleteFieldNote,
        onDiscardClick = viewModel::discardChanges,
        onBackClick = dropUnlessResumed { onEditorFinished() },
        modifier = modifier,
    )
}
