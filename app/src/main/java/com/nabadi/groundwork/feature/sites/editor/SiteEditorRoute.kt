package com.nabadi.groundwork.feature.sites.editor

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
fun SiteEditorRoute(
    route: GroundWorkRoute.SiteEditor,
    onEditorFinished: () -> Unit,
    onSiteDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SiteEditorViewModel = hiltViewModel<SiteEditorViewModel, SiteEditorViewModel.Factory>(
        creationCallback = { factory -> factory.create(route) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigation reacts to the finished state rather than being called by the
    // ViewModel, so it always uses this composition's callbacks and only runs
    // while the screen is started.
    val currentOnEditorFinished by rememberUpdatedState(onEditorFinished)
    val currentOnSiteDeleted by rememberUpdatedState(onSiteDeleted)
    val completion = uiState.completion
    LaunchedEffect(completion) {
        when (completion) {
            SiteEditorCompletion.SAVED,
            SiteEditorCompletion.DISCARDED -> currentOnEditorFinished()
            SiteEditorCompletion.DELETED -> currentOnSiteDeleted()
            null -> Unit
        }
    }

    SiteEditorScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onLocationChange = viewModel::onLocationChange,
        onStatusChange = viewModel::onStatusChange,
        onPriorityChange = viewModel::onPriorityChange,
        onSaveClick = viewModel::saveSite,
        onDestructiveActionClick = {
            if (uiState.isEditing) {
                viewModel.deleteSite()
            } else {
                viewModel.discardSite()
            }
        },
        onBackClick = dropUnlessResumed { onEditorFinished() },
        modifier = modifier,
    )
}
