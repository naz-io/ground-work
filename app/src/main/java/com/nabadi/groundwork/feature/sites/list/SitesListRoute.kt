package com.nabadi.groundwork.feature.sites.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.ui.lifecycle.dropUnlessResumed

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
        onOpenSiteClick = onOpenSiteClick.dropUnlessResumed(),
        onAddSiteClick = dropUnlessResumed { onAddSiteClick() },
        modifier = modifier,
    )
}
