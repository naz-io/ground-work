package com.nabadi.groundwork.feature.sites

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.ui.theme.GroundWorkTheme
import kotlin.math.max

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
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSiteClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.sites_list_add_content_description),
                )
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
                    uiState.isError -> uiState.errorMessage?.let {
                        ErrorState(errorMessage = stringResource(R.string.sites_list_error, it))
                    }

                    uiState.sites.isEmpty() -> EmptySitesState()
                    else -> SitesContent(
                        sites = uiState.sites,
                        onOpenSiteClick = onOpenSiteClick,
                        onEditSiteClick = onEditSiteClick,
                        modifier = Modifier.fillMaxSize(),
                    )
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
private fun ErrorState(errorMessage: String, modifier: Modifier = Modifier) {
    Text(
        text = errorMessage,
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
    sites: List<Site>,
    onOpenSiteClick: (SiteId) -> Unit,
    onEditSiteClick: (SiteId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.spacing_fab_clearance)),
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
fun SiteCard(
    site: Site,
    onOpenSiteClick: () -> Unit,
    onEditSiteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        border = BorderStroke(
            width = dimensionResource(R.dimen.border_card_strong),
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column {
            SiteCardHeader(
                siteId = site.id.value,
                onEditSiteClick = onEditSiteClick,
            )

            SiteCardBody(
                site = site,
                onOpenSiteClick = onOpenSiteClick,
            )

            SiteCardMetadata(
                site = site,
                noteCount = 0
            )
        }
    }
}

@Composable
private fun SiteCardHeader(
    siteId: String,
    onEditSiteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.onSurface)
            .padding(
                start = dimensionResource(R.dimen.padding_card_content),
                end = dimensionResource(R.dimen.padding_trailing_action_end),
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = siteId.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.inverseOnSurface,
        )

        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.size_icon_action_touch_target))
                .clickable(onClick = onEditSiteClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.sites_list_site_actions),
                tint = MaterialTheme.colorScheme.inverseOnSurface,
            )
        }
    }
}

@Composable
private fun SiteCardBody(
    site: Site,
    onOpenSiteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenSiteClick)
            .padding(
                start = dimensionResource(R.dimen.padding_card_content),
                top = dimensionResource(R.dimen.padding_card_content),
                end = dimensionResource(R.dimen.padding_trailing_action_end),
                bottom = dimensionResource(R.dimen.padding_card_content),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_card_content)),
        ) {
            SiteCardLabels(
                priority = site.priority,
                status = site.status,
            )

            Text(
                text = site.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            SiteLocationLabel(location = site.location)

            if (site.description.isNotBlank()) {
                Text(
                    text = site.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Box(
            modifier = Modifier.size(dimensionResource(R.dimen.size_icon_action_touch_target)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun SiteCardLabels(
    priority: SitePriority,
    status: SiteStatus,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_chip)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(priority.labelResId),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(status.labelResId),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun SiteLocationLabel(
    location: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_icon_text)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.size_icon_small)),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = location,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SiteCardMetadata(
    site: Site,
    noteCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_card_content),
                vertical = dimensionResource(R.dimen.padding_card_metadata_vertical),
            ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_metadata_item)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.sites_list_site_notes_count, noteCount),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(
                R.string.sites_list_site_updated_label,
                site.updatedAt.relativeTimeLabel(),
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@get:StringRes
private val SiteStatus.labelResId: Int
    get() = when (this) {
        SiteStatus.ACTIVE -> R.string.site_status_active
        SiteStatus.COMPLETED -> R.string.site_status_completed
        SiteStatus.ARCHIVED -> R.string.site_status_archived
    }

@get:StringRes
private val SitePriority.labelResId: Int
    get() = when (this) {
        SitePriority.LOW -> R.string.site_priority_low
        SitePriority.NORMAL -> R.string.site_priority_normal
        SitePriority.HIGH -> R.string.site_priority_high
        SitePriority.URGENT -> R.string.site_priority_urgent
    }

private fun Long.relativeTimeLabel(nowMillis: Long = System.currentTimeMillis()): String {
    if (this <= 0L) return "unknown"

    val elapsedMillis = max(nowMillis - this, 0L)
    val minuteMillis = 60_000L
    val hourMillis = 60 * minuteMillis
    val dayMillis = 24 * hourMillis
    val weekMillis = 7 * dayMillis
    val monthMillis = 30 * dayMillis
    val yearMillis = 365 * dayMillis

    return when {
        elapsedMillis < minuteMillis -> "just now"
        elapsedMillis < hourMillis -> {
            val minutes = elapsedMillis / minuteMillis
            "$minutes min ago"
        }

        elapsedMillis < dayMillis -> {
            val hours = elapsedMillis / hourMillis
            if (hours == 1L) "1 hour ago" else "$hours hours ago"
        }

        elapsedMillis < weekMillis -> {
            val days = elapsedMillis / dayMillis
            if (days == 1L) "1 day ago" else "$days days ago"
        }

        elapsedMillis < monthMillis -> {
            val weeks = elapsedMillis / weekMillis
            if (weeks == 1L) "1 week ago" else "$weeks weeks ago"
        }

        elapsedMillis < yearMillis -> {
            val months = elapsedMillis / monthMillis
            if (months == 1L) "1 month ago" else "$months months ago"
        }

        else -> {
            val years = elapsedMillis / yearMillis
            if (years == 1L) "1 year ago" else "$years years ago"
        }
    }
}

private const val PREVIEW_API_LEVEL = 35

@Preview(
    name = "Loading",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
fun SitesListScreenPreview_Loading() {
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
fun SitesListScreenPreview_Loading_Dark() {
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
fun SitesListScreenPreview_Empty() {
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
fun SitesListScreenPreview_Empty_Dark() {
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
fun SitesListScreenPreview_Error() {
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
fun SitesListScreenPreview_Error_Dark() {
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
    name = "Content",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
fun SitesListScreenPreview_Content() {
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
fun SitesListScreenPreview_Content_Dark() {
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
    name = "Site Card",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
fun SiteCardPreview() {
    GroundWorkTheme {
        SiteCard(
            site = previewSites.first(),
            onOpenSiteClick = {},
            onEditSiteClick = {},
        )
    }
}

@Preview(
    name = "Site Card - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
fun SiteCardPreview_Dark() {
    GroundWorkTheme {
        SiteCard(
            site = previewSites.first(),
            onOpenSiteClick = {},
            onEditSiteClick = {},
        )
    }
}

private val previewNowMillis = System.currentTimeMillis()

private val previewSites = listOf(
    Site(
        id = SiteId("SITE-001"),
        name = "North Warehouse",
        description = "Main storage facility with recurring loading-bay access issues and damaged pallet racks.",
        location = "Sector A · 125 Industrial Road",
        priority = SitePriority.HIGH,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 2 * 60 * 60 * 1000, // 2 hours ago
    ),
    Site(
        id = SiteId("SITE-002"),
        name = "South Terminal",
        description = "Loading dock inspection area for incoming freight and damaged trailer reports.",
        location = "Sector B · Dock 4",
        priority = SitePriority.NORMAL,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 24 * 60 * 60 * 1000, // 1 day ago
    ),
    Site(
        id = SiteId("SITE-003"),
        name = "Pine Creek Bridge",
        description = "Bridge maintenance zone with temporary fencing, drainage concerns, and weekly inspection notes.",
        location = "Pine Creek Road · Mile 18",
        priority = SitePriority.URGENT,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 3 * 24 * 60 * 60 * 1000, // 3 days ago
    ),
    Site(
        id = SiteId("SITE-004"),
        name = "Main Grid Transformer",
        description = "Electrical utility site with restricted access and required photo documentation after every visit.",
        location = "North Yard · Transformer 7",
        priority = SitePriority.HIGH,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 45 * 60 * 1000, // 45 min ago
    ),
    Site(
        id = SiteId("SITE-005"),
        name = "West Parking Structure",
        description = "Concrete repair and water ingress observations across levels P2 and P3.",
        location = "Civic Centre · West Lot",
        priority = SitePriority.NORMAL,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 6 * 24 * 60 * 60 * 1000, // 6 days ago
    ),
    Site(
        id = SiteId("SITE-006"),
        name = "River Pump Station",
        description = "Pump station with standing water reports, generator checks, and access-road erosion.",
        location = "Riverbend Service Road",
        priority = SitePriority.URGENT,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 12 * 60 * 60 * 1000, // 12 hours ago
    ),
    Site(
        id = SiteId("SITE-007"),
        name = "East Service Tunnel",
        description = "Tunnel inspection route with lighting issues and confined-space safety notes.",
        location = "Transit Yard · East Access",
        priority = SitePriority.HIGH,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 14 * 24 * 60 * 60 * 1000, // 14 days ago
    ),
    Site(
        id = SiteId("SITE-008"),
        name = "Admin Roof Access",
        description = "Roof inspection area with HVAC vibration reports and safety railing follow-up.",
        location = "Admin Building · Roof Level",
        priority = SitePriority.LOW,
        status = SiteStatus.COMPLETED,
        createdAt = 0,
        updatedAt = previewNowMillis - 30 * 24 * 60 * 60 * 1000, // 30 days ago
    ),
    Site(
        id = SiteId("SITE-009"),
        name = "Old Storage Annex",
        description = "Archived site kept for historical notes and completed remediation photos.",
        location = "Annex Road · Unit 12",
        priority = SitePriority.LOW,
        status = SiteStatus.ARCHIVED,
        createdAt = 0,
        updatedAt = previewNowMillis - 180 * 24 * 60 * 60 * 1000, // 180 days ago
    ),
    Site(
        id = SiteId("SITE-010"),
        name = "Fleet Maintenance Bay",
        description = "Vehicle service bay with spill reports, tool inventory notes, and repair photos.",
        location = "Operations Yard · Bay 3",
        priority = SitePriority.NORMAL,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 5 * 60 * 1000, // 5 min ago
    ),
)
