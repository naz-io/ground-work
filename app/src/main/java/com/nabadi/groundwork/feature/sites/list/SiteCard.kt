package com.nabadi.groundwork.feature.sites.list

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.feature.sites.labelResId
import com.nabadi.groundwork.feature.sites.previewSites
import com.nabadi.groundwork.ui.components.GroundWorkPreviewSurface
import com.nabadi.groundwork.ui.components.GroundWorkShapes
import com.nabadi.groundwork.ui.components.PREVIEW_API_LEVEL
import com.nabadi.groundwork.ui.components.TechnicalLabel
import com.nabadi.groundwork.ui.format.relativeTimeLabel

@Composable
fun SiteCard(
    site: Site,
    noteCount: Int,
    onOpenSiteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onOpenSiteClick,
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
                priority = site.priority,
            )

            SiteCardBody(site = site)

            SiteCardMetadata(
                site = site,
                noteCount = noteCount,
            )
        }
    }
}

@Composable
private fun SiteCardHeader(
    siteId: String,
    priority: SitePriority,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.padding_card_content),
                top = dimensionResource(R.dimen.padding_card_content),
                end = dimensionResource(R.dimen.padding_card_content),
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TechnicalLabel(
            text = stringResource(
                R.string.sites_list_site_id_label,
                siteId.uppercase(),
            ),
        )
        SitePriorityBadge(priority = priority)
    }
}

@Composable
private fun SiteCardBody(
    site: Site,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.padding_card_content),
                top = dimensionResource(R.dimen.spacing_list_item),
                end = dimensionResource(R.dimen.padding_card_content),
                bottom = dimensionResource(R.dimen.padding_card_content),
            ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_card_content)),
    ) {
        Text(
            text = site.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
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
}

@Composable
private fun SitePriorityBadge(
    priority: SitePriority,
    modifier: Modifier = Modifier,
) {
    val containerColor = when (priority) {
        SitePriority.URGENT -> MaterialTheme.colorScheme.errorContainer
        SitePriority.HIGH -> MaterialTheme.colorScheme.primaryContainer
        SitePriority.NORMAL,
        SitePriority.LOW,
        -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (priority) {
        SitePriority.URGENT -> MaterialTheme.colorScheme.onErrorContainer
        SitePriority.HIGH -> MaterialTheme.colorScheme.onPrimaryContainer
        SitePriority.NORMAL,
        SitePriority.LOW,
        -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = GroundWorkShapes.Control,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.padding_site_card_priority_horizontal),
                vertical = dimensionResource(R.dimen.padding_site_card_priority_vertical),
            ),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_icon_text)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (priority == SitePriority.URGENT) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.size_icon_small)),
                )
            }
            Text(
                text = stringResource(priority.labelResId),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
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
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_card_content),
                vertical = dimensionResource(R.dimen.padding_card_metadata_vertical),
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SiteCardMetadataLabel(
            text = stringResource(site.status.labelResId),
            emphasized = site.status == SiteStatus.ACTIVE,
        )
        SiteCardMetadataLabel(
            icon = Icons.Filled.NoteAlt,
            text = stringResource(R.string.sites_list_site_notes_count, noteCount),
        )
        SiteCardMetadataLabel(
            icon = Icons.Filled.Schedule,
            text = stringResource(
                R.string.sites_list_site_updated_label,
                site.updatedAt.relativeTimeLabel(),
            ),
        )
    }
}

@Composable
private fun SiteCardMetadataLabel(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    emphasized: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_icon_text)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.size_icon_small)),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (emphasized) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(
    name = "Site Card",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteCardPreview() {
    GroundWorkPreviewSurface {
        SiteCard(
            site = previewSites.first(),
            noteCount = 12,
            onOpenSiteClick = {},
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
private fun SiteCardPreview_Dark() {
    GroundWorkPreviewSurface {
        SiteCard(
            site = previewSites.first(),
            noteCount = 12,
            onOpenSiteClick = {},
        )
    }
}
