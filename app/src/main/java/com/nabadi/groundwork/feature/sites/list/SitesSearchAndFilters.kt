package com.nabadi.groundwork.feature.sites.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.feature.sites.labelResId
import com.nabadi.groundwork.ui.components.GroundWorkPreviewSurface
import com.nabadi.groundwork.ui.components.GroundWorkShapes
import com.nabadi.groundwork.ui.components.PREVIEW_API_LEVEL

@Composable
fun SitesSearchAndFilters(
    selectedStatus: SiteStatus?,
    selectedPriority: SitePriority?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (SiteStatus?) -> Unit,
    onPriorityFilterChange: (SitePriority?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
    ) {
        SitesSearchField(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
        )
        SitesFilters(
            selectedStatus = selectedStatus,
            selectedPriority = selectedPriority,
            onStatusFilterChange = onStatusFilterChange,
            onPriorityFilterChange = onPriorityFilterChange,
        )
    }
}

@Composable
private fun SitesSearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(
                text = stringResource(R.string.sites_list_search_placeholder),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (searchQuery.isNotBlank()) {
                IconButton(
                    onClick = { onSearchQueryChange("") },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.sites_list_search_clear_content_description),
                    )
                }
            }
        },
        singleLine = true,
    )
}

@Composable
private fun SitesFilters(
    selectedStatus: SiteStatus?,
    selectedPriority: SitePriority?,
    onStatusFilterChange: (SiteStatus?) -> Unit,
    onPriorityFilterChange: (SitePriority?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
    ) {
        item {
            SitesFilterChip(
                selected = selectedStatus == null && selectedPriority == null,
                onClick = {
                    onStatusFilterChange(null)
                    onPriorityFilterChange(null)
                },
                label = stringResource(R.string.sites_list_filter_all),
            )
        }

        items(
            items = SiteStatus.entries,
            key = { it },
            contentType = { "SiteStatusFilterOption" },
        ) { statusFilter ->
            SitesFilterChip(
                selected = selectedStatus == statusFilter,
                onClick = {
                    onStatusFilterChange(
                        if (selectedStatus == statusFilter) {
                            null
                        } else {
                            statusFilter
                        },
                    )
                },
                label = stringResource(
                    R.string.sites_list_filter_status_label,
                    stringResource(statusFilter.labelResId),
                ),
            )
        }

        items(
            items = SitePriority.entries,
            key = { it },
            contentType = { "SitePriorityFilterOption" },
        ) { priorityFilter ->
            SitesFilterChip(
                selected = selectedPriority == priorityFilter,
                onClick = {
                    onPriorityFilterChange(
                        if (selectedPriority == priorityFilter) {
                            null
                        } else {
                            priorityFilter
                        },
                    )
                },
                label = stringResource(
                    R.string.sites_list_filter_priority_label,
                    stringResource(priorityFilter.labelResId),
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SitesFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        shape = GroundWorkShapes.Control,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Preview(
    name = "Empty Criteria",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesSearchAndFiltersPreview_EmptyCriteria() {
    GroundWorkPreviewSurface {
        SitesSearchAndFilters(
            selectedStatus = null,
            selectedPriority = null,
            searchQuery = "",
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
        )
    }
}

@Preview(
    name = "Active Criteria",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesSearchAndFiltersPreview_ActiveCriteria() {
    GroundWorkPreviewSurface {
        SitesSearchAndFilters(
            selectedStatus = SiteStatus.ACTIVE,
            selectedPriority = SitePriority.HIGH,
            searchQuery = "warehouse",
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Empty Criteria",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesSearchAndFiltersPreview_DarkMode_EmptyCriteria() {
    GroundWorkPreviewSurface {
        SitesSearchAndFilters(
            selectedStatus = null,
            selectedPriority = null,
            searchQuery = "",
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Active Criteria",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SitesSearchAndFiltersPreview_DarkMode_ActiveCriteria() {
    GroundWorkPreviewSurface {
        SitesSearchAndFilters(
            selectedStatus = SiteStatus.ACTIVE,
            selectedPriority = SitePriority.HIGH,
            searchQuery = "warehouse",
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
        )
    }
}