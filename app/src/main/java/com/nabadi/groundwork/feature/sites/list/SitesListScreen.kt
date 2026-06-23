package com.nabadi.groundwork.feature.sites.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.feature.sites.PREVIEW_API_LEVEL
import com.nabadi.groundwork.feature.sites.previewSites
import com.nabadi.groundwork.ui.theme.GroundWorkTheme

@Composable
fun SitesListScreen(
    uiState: SitesListUiState,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (SiteStatus?) -> Unit,
    onPriorityFilterChange: (SitePriority?) -> Unit,
    onClearCriteriaClick: () -> Unit,
    onOpenSiteClick: (SiteId) -> Unit,
    onEditSiteClick: (SiteId) -> Unit,
    onAddSiteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shouldShowAddButton = !uiState.isLoading && !uiState.isError

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (shouldShowAddButton) {
                FloatingActionButton(onClick = onAddSiteClick) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.sites_list_add_content_description),
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensionResource(id = R.dimen.spacing_screen)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_screen_section)),
        ) {
            Text(
                text = stringResource(R.string.sites_list_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    uiState.isLoading -> LoadingState()
                    uiState.isError -> ErrorState(errorMessage = uiState.errorMessage.orEmpty())
                    uiState.shouldShowEmptyState -> EmptySitesState()
                    uiState.shouldShowNoMatchesState -> SitesNoMatchesContent(
                        selectedStatus = uiState.selectedStatus,
                        selectedPriority = uiState.selectedPriority,
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
                        onStatusFilterChange = onStatusFilterChange,
                        onPriorityFilterChange = onPriorityFilterChange,
                        onClearCriteriaClick = onClearCriteriaClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                    uiState.shouldShowContent -> SitesContent(
                        selectedStatus = uiState.selectedStatus,
                        selectedPriority = uiState.selectedPriority,
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
                        onStatusFilterChange = onStatusFilterChange,
                        onPriorityFilterChange = onPriorityFilterChange,
                        sites = uiState.sites,
                        onOpenSiteClick = onOpenSiteClick,
                        onEditSiteClick = onEditSiteClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
private fun ErrorState(
    errorMessage: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.sites_list_error, errorMessage),
        modifier = modifier,
        color = MaterialTheme.colorScheme.error,
    )
}

@Composable
private fun EmptySitesState(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.sites_list_empty_sites_message),
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun SitesContent(
    selectedStatus: SiteStatus?,
    selectedPriority: SitePriority?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (SiteStatus?) -> Unit,
    onPriorityFilterChange: (SitePriority?) -> Unit,
    sites: List<Site>,
    onOpenSiteClick: (SiteId) -> Unit,
    onEditSiteClick: (SiteId) -> Unit,
    modifier: Modifier = Modifier,
) {
    SitesSearchResultsContent(
        selectedStatus = selectedStatus,
        selectedPriority = selectedPriority,
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        onStatusFilterChange = onStatusFilterChange,
        onPriorityFilterChange = onPriorityFilterChange,
        modifier = modifier,
    ) {
        items(sites, key = { it.id.value }) { site ->
            SiteCard(
                site = site,
                onOpenSiteClick = { onOpenSiteClick(site.id) },
                onEditSiteClick = { onEditSiteClick(site.id) },
            )
        }
    }
}

@Composable
private fun SitesNoMatchesContent(
    selectedStatus: SiteStatus?,
    selectedPriority: SitePriority?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (SiteStatus?) -> Unit,
    onPriorityFilterChange: (SitePriority?) -> Unit,
    onClearCriteriaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SitesSearchResultsContent(
        selectedStatus = selectedStatus,
        selectedPriority = selectedPriority,
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        onStatusFilterChange = onStatusFilterChange,
        onPriorityFilterChange = onPriorityFilterChange,
        modifier = modifier,
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillParentMaxSize()
                    .padding(dimensionResource(R.dimen.padding_card_content)),
                contentAlignment = Alignment.Center,
            ) {
                NoMatchingSitesState(
                    onClearCriteriaClick = onClearCriteriaClick,
                )
            }
        }
    }
}

@Composable
private fun SitesSearchResultsContent(
    selectedStatus: SiteStatus?,
    selectedPriority: SitePriority?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (SiteStatus?) -> Unit,
    onPriorityFilterChange: (SitePriority?) -> Unit,
    modifier: Modifier = Modifier,
    resultsContent: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.spacing_fab_clearance)),
    ) {
        item {
            SitesSearchAndFilters(
                selectedStatus = selectedStatus,
                selectedPriority = selectedPriority,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onStatusFilterChange = onStatusFilterChange,
                onPriorityFilterChange = onPriorityFilterChange,
            )
        }

        resultsContent()
    }
}

@Composable
private fun NoMatchingSitesState(
    onClearCriteriaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_screen_section)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.sites_list_no_matches_title),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.sites_list_no_matches_description),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            onClick = onClearCriteriaClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.sites_list_no_matches_action))
        }
    }
}

@Preview(
    name = "Content",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_Content() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(
                isLoading = false,
                sites = previewSites,
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "Content - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_Content_Dark() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(
                isLoading = false,
                sites = previewSites,
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "Filtered Content",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_FilteredContent() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(
                isLoading = false,
                searchQuery = "warehouse",
                selectedStatus = SiteStatus.ACTIVE,
                selectedPriority = SitePriority.HIGH,
                sites = previewSites.filter {
                    it.status == SiteStatus.ACTIVE && it.priority == SitePriority.HIGH
                },
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "Filtered Content - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_FilteredContent_Dark() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(
                isLoading = false,
                searchQuery = "warehouse",
                selectedStatus = SiteStatus.ACTIVE,
                selectedPriority = SitePriority.HIGH,
                sites = previewSites.filter {
                    it.status == SiteStatus.ACTIVE && it.priority == SitePriority.HIGH
                },
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "No Matches",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_NoMatches() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(
                isLoading = false,
                searchQuery = "missing site",
                selectedStatus = SiteStatus.ARCHIVED,
                selectedPriority = SitePriority.URGENT,
                sites = emptyList(),
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "No Matches - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_NoMatches_Dark() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(
                isLoading = false,
                searchQuery = "missing site",
                selectedStatus = SiteStatus.ARCHIVED,
                selectedPriority = SitePriority.URGENT,
                sites = emptyList(),
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "Loading",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_Loading() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(isLoading = true),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "Loading - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_Loading_Dark() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(isLoading = true),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "Empty",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_Empty() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(
                isLoading = false,
                sites = emptyList(),
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "Empty - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_Empty_Dark() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(
                isLoading = false,
                sites = emptyList(),
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "Error",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_Error() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(
                isLoading = false,
                errorMessage = "Could not load sites.",
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}

@Preview(
    name = "Error - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesListScreenPreview_Error_Dark() {
    GroundWorkTheme {
        SitesListScreen(
            uiState = SitesListUiState(
                isLoading = false,
                errorMessage = "Could not load sites.",
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onClearCriteriaClick = {},
            onOpenSiteClick = {},
            onEditSiteClick = {},
            onAddSiteClick = {},
        )
    }
}