package com.nabadi.groundwork.feature.sites.list

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.feature.sites.PREVIEW_API_LEVEL
import com.nabadi.groundwork.feature.sites.labelResId
import com.nabadi.groundwork.feature.sites.previewSites
import com.nabadi.groundwork.ui.format.relativeTimeLabel
import com.nabadi.groundwork.ui.theme.GroundWorkTheme

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
                noteCount = 0,
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

@Preview(
    name = "Site Card",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteCardPreview() {
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
private fun SiteCardPreview_Dark() {
    GroundWorkTheme {
        SiteCard(
            site = previewSites.first(),
            onOpenSiteClick = {},
            onEditSiteClick = {},
        )
    }
}
