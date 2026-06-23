package com.nabadi.groundwork.feature.sites.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SiteEditorRoute(
    onEditorFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SiteEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SiteEditorScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onLocationChange = viewModel::onLocationChange,
        onStatusChange = viewModel::onStatusChange,
        onPriorityChange = viewModel::onPriorityChange,
        onSaveClick = { viewModel.saveSite(onSaved = onEditorFinished) },
        onDestructiveActionClick = {
            if (uiState.isEditing) {
                viewModel.deleteSite(onDeleted = onEditorFinished)
            } else {
                viewModel.discardSite(onDiscarded = onEditorFinished)
            }
        },
        onBackClick = onEditorFinished,
        modifier = modifier,
    )
}