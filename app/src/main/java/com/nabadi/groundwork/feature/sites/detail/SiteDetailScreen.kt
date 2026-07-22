package com.nabadi.groundwork.feature.sites.detail

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.feature.fieldnotes.list.FieldNoteItemCard
import com.nabadi.groundwork.feature.fieldnotes.list.FieldNoteListItemUiState
import com.nabadi.groundwork.feature.fieldnotes.previewFieldNoteItems
import com.nabadi.groundwork.feature.sites.labelResId
import com.nabadi.groundwork.feature.sites.previewLongContentSite
import com.nabadi.groundwork.feature.sites.previewSites
import com.nabadi.groundwork.ui.components.BackButton
import com.nabadi.groundwork.ui.components.GroundWorkLoadingState
import com.nabadi.groundwork.ui.components.GroundWorkPrimaryButton
import com.nabadi.groundwork.ui.components.GroundWorkPreviewSurface
import com.nabadi.groundwork.ui.components.GroundWorkShapes
import com.nabadi.groundwork.ui.components.PREVIEW_API_LEVEL
import com.nabadi.groundwork.ui.components.TechnicalLabel

@Composable
fun SiteDetailScreen(
    uiState: SiteDetailUiState,
    onBackClick: () -> Unit,
    onEditSiteClick: () -> Unit,
    onFieldNoteClick: (FieldNoteId) -> Unit,
    onAddFieldNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SiteDetailTopBar(
                onBackClick = onBackClick,
            )
        },
    ) { paddingValues ->
        when {
            uiState.isBusy -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                GroundWorkLoadingState()
            }

            uiState.isError -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(dimensionResource(R.dimen.spacing_screen)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            else -> SiteDetailContent(
                uiState = uiState,
                onEditSiteClick = onEditSiteClick,
                onFieldNoteClick = onFieldNoteClick,
                onAddFieldNoteClick = onAddFieldNoteClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SiteDetailTopBar(
    onBackClick: () -> Unit,
) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.site_detail_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
            },
            navigationIcon = { BackButton(onBackClick = onBackClick) },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun SiteDetailContent(
    uiState: SiteDetailUiState,
    onEditSiteClick: () -> Unit,
    onFieldNoteClick: (FieldNoteId) -> Unit,
    onAddFieldNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = dimensionResource(R.dimen.spacing_screen),
            top = dimensionResource(R.dimen.spacing_screen),
            end = dimensionResource(R.dimen.spacing_screen),
            bottom = dimensionResource(R.dimen.spacing_screen),
        ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_screen_section)),
    ) {
        item { SiteHeader(uiState = uiState) }
        item {
            SiteNoteMetric(noteCount = uiState.noteCount)
        }
        item {
            SiteLocationPanel(location = uiState.location)
        }
        item {
            SiteActions(
                canAddFieldNote = uiState.canAddFieldNote,
                canEdit = uiState.canEdit,
                onAddFieldNoteClick = onAddFieldNoteClick,
                onEditSiteClick = onEditSiteClick,
            )
        }
        item { SiteInformation(uiState = uiState) }
        item { SiteNotesHeader(noteCount = uiState.noteCount) }
        if (uiState.notesErrorMessage != null) {
            item {
                SiteNotesError(message = uiState.notesErrorMessage)
            }
        } else if (!uiState.hasNotes) {
            item {
                SiteNotesEmptyState(
                    canAddFieldNote = uiState.canAddFieldNote,
                    onAddFieldNoteClick = onAddFieldNoteClick,
                )
            }
        } else {
            items(uiState.fieldNotes, key = { it.id.value }) { note ->
                FieldNoteItemCard(
                    fieldNoteItem = FieldNoteListItemUiState(note, uiState.name),
                    onClick = { onFieldNoteClick(note.id) },
                )
            }
        }
    }
}

@Composable
private fun SiteHeader(
    uiState: SiteDetailUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_card_content)),
    ) {
        TechnicalLabel(text = uiState.siteId?.value?.uppercase().orEmpty())
        Text(
            text = uiState.name,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
        Row(
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
                text = uiState.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_chip)),
        ) {
            SiteBadge(
                text = stringResource(uiState.priority.labelResId),
                emphasized = true,
            )
            SiteBadge(text = stringResource(uiState.status.labelResId))
        }
    }
}

@Composable
private fun SiteBadge(
    text: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    Surface(
        modifier = modifier,
        color = if (emphasized) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = if (emphasized) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        shape = RectangleShape,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.padding_site_detail_badge_horizontal),
                vertical = dimensionResource(R.dimen.padding_site_detail_badge_vertical),
            ),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SiteNoteMetric(
    noteCount: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        border = BorderStroke(
            dimensionResource(R.dimen.border_card_strong),
            MaterialTheme.colorScheme.outlineVariant,
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_site_detail_metric)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TechnicalLabel(text = stringResource(R.string.site_detail_note_count_label))
            Text(
                text = noteCount.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SiteLocationPanel(
    location: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.height_site_detail_location_panel)),
        shape = RectangleShape,
        border = BorderStroke(
            dimensionResource(R.dimen.border_card_strong),
            MaterialTheme.colorScheme.outlineVariant,
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_card_content)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.size_site_detail_location_icon)),
                tint = MaterialTheme.colorScheme.primary,
            )
            TechnicalLabel(text = stringResource(R.string.site_detail_location_label))
            Text(
                text = location,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SiteActions(
    canAddFieldNote: Boolean,
    canEdit: Boolean,
    onAddFieldNoteClick: () -> Unit,
    onEditSiteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
    ) {
        GroundWorkPrimaryButton(
            text = stringResource(R.string.site_detail_add_note_action),
            onClick = onAddFieldNoteClick,
            leadingIcon = Icons.Filled.Add,
            enabled = canAddFieldNote,
        )
        OutlinedButton(
            onClick = onEditSiteClick,
            enabled = canEdit,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.height_site_detail_secondary_action)),
            shape = GroundWorkShapes.Control,
            border = BorderStroke(
                dimensionResource(R.dimen.stroke_width_primary_button_border),
                MaterialTheme.colorScheme.outline,
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.size_icon_small)),
            )
            Text(
                text = stringResource(R.string.site_detail_edit_site_action),
                modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_button_icon_text)),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun SiteInformation(
    uiState: SiteDetailUiState,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        border = BorderStroke(
            dimensionResource(R.dimen.border_field_note_card),
            MaterialTheme.colorScheme.outlineVariant,
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_card_content)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
        ) {
            TechnicalLabel(text = stringResource(R.string.site_detail_information_title))
            SiteInformationRow(
                label = stringResource(R.string.site_detail_site_id_label),
                value = uiState.siteId?.value?.uppercase().orEmpty(),
            )
            if (uiState.description.isNotBlank()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = uiState.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SiteInformationRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SiteNotesHeader(
    noteCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.site_detail_notes_section_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        TechnicalLabel(
            text = stringResource(R.string.site_detail_notes_count, noteCount),
        )
    }
}

@Composable
private fun SiteNotesError(
    message: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        border = BorderStroke(
            dimensionResource(R.dimen.border_field_note_card),
            MaterialTheme.colorScheme.error,
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_card_content)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun SiteNotesEmptyState(
    canAddFieldNote: Boolean,
    onAddFieldNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        border = BorderStroke(
            dimensionResource(R.dimen.border_card_strong),
            MaterialTheme.colorScheme.outlineVariant,
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_site_detail_empty_state)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
        ) {
            Icon(
                imageVector = Icons.Filled.NoteAlt,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.size_site_detail_empty_icon)),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.site_detail_notes_empty_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.site_detail_notes_empty_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            OutlinedButton(
                onClick = onAddFieldNoteClick,
                enabled = canAddFieldNote,
                shape = GroundWorkShapes.Control,
            ) {
                Text(
                    text = stringResource(R.string.site_detail_notes_create_first),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Preview(
    name = "Content",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Preview(
    name = "Content - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteDetailScreenPreview_Content() {
    val site = previewSites.first()
    SiteDetailScreenPreview(
        uiState = site.toDetailPreviewState(
            fieldNotes = previewFieldNotesFor(site = site, count = 2),
        ),
    )
}

@Preview(
    name = "No Field Notes",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Preview(
    name = "No Field Notes - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteDetailScreenPreview_NoFieldNotes() {
    SiteDetailScreenPreview(
        uiState = previewSites.first().toDetailPreviewState(),
    )
}

@Preview(
    name = "Loading",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Preview(
    name = "Loading - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteDetailScreenPreview_Loading() {
    SiteDetailScreenPreview(
        uiState = SiteDetailUiState(isLoading = true),
    )
}

@Preview(
    name = "Site Error",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Preview(
    name = "Site Error - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteDetailScreenPreview_SiteError() {
    SiteDetailScreenPreview(
        uiState = SiteDetailUiState(errorMessage = "Unable to load site."),
    )
}

@Preview(
    name = "Field Notes Error",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Preview(
    name = "Field Notes Error - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteDetailScreenPreview_FieldNotesError() {
    SiteDetailScreenPreview(
        uiState = previewSites.first().toDetailPreviewState(
            notesErrorMessage = "Unable to load site notes.",
        ),
    )
}

@Preview(
    name = "Long Content",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Preview(
    name = "Long Content - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteDetailScreenPreview_LongContent() {
    SiteDetailScreenPreview(
        uiState = previewLongContentSite.toDetailPreviewState(
            fieldNotes = previewFieldNotesFor(site = previewLongContentSite, count = 6),
        ),
    )
}

@Composable
private fun SiteDetailScreenPreview(uiState: SiteDetailUiState) {
    GroundWorkPreviewSurface {
        SiteDetailScreen(
            uiState = uiState,
            onBackClick = {},
            onEditSiteClick = {},
            onFieldNoteClick = {},
            onAddFieldNoteClick = {},
        )
    }
}

private fun Site.toDetailPreviewState(
    fieldNotes: List<FieldNote> = emptyList(),
    notesErrorMessage: String? = null,
): SiteDetailUiState = SiteDetailUiState(
    siteId = id,
    name = name,
    location = location,
    priority = priority,
    status = status,
    description = description,
    fieldNotes = fieldNotes,
    notesErrorMessage = notesErrorMessage,
)

private fun previewFieldNotesFor(
    site: Site,
    count: Int,
): List<FieldNote> = previewFieldNoteItems
    .take(count)
    .map { item -> item.note.copy(siteId = site.id) }
