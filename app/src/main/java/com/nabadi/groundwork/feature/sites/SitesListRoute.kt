package com.nabadi.groundwork.feature.sites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nabadi.groundwork.domain.model.SiteId

@Composable
fun SitesListRoute(
    onOpenSiteClick: (SiteId) -> Unit,
    onAddSiteClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SitesListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SitesListScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onStatusFilterChange = viewModel::onStatusFilterChange,
        onPriorityFilterChange = viewModel::onPriorityFilterChange,
        onClearCriteriaClick = viewModel::onClearCriteriaClick,
        onOpenSiteClick = onOpenSiteClick,
        onEditSiteClick = {},
        onAddSiteClick = onAddSiteClick,
        modifier = modifier,
    )
}