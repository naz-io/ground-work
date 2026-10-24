package com.nabadi.groundwork.feature.sites.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.navigation.GroundWorkRoute

@Composable
fun SiteDetailRoute(
    route: GroundWorkRoute.SiteDetail,
    onBackClick: () -> Unit,
    onEditSiteClick: (SiteId) -> Unit,
    onFieldNoteClick: (FieldNoteId) -> Unit,
    onAddFieldNoteClick: (SiteId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SiteDetailViewModel = hiltViewModel<SiteDetailViewModel, SiteDetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(route) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SiteDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onEditSiteClick = { uiState.siteId?.let(onEditSiteClick) },
        onFieldNoteClick = onFieldNoteClick,
        onAddFieldNoteClick = { uiState.siteId?.let(onAddFieldNoteClick) },
        modifier = modifier,
    )
}
