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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.nabadi.groundwork.ui.components.GroundWorkFloatingActionButton
import com.nabadi.groundwork.ui.components.GroundWorkLoadingState
import com.nabadi.groundwork.ui.components.GroundWorkPreviewSurface
import com.nabadi.groundwork.ui.components.PREVIEW_API_LEVEL

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
                canEdit = uiState.canEdit,
                onBackClick = onBackClick,
                onEditSiteClick = onEditSiteClick,
            )
        },
        floatingActionButton = {
            if (uiState.canAddFieldNote) {
                GroundWorkFloatingActionButton(
                    icon = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.site_detail_add_note),
                    onClick = onAddFieldNoteClick,
                )
            }
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
                onFieldNoteClick = onFieldNoteClick,
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
    canEdit: Boolean,
    onBackClick: () -> Unit,
    onEditSiteClick: () -> Unit,
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
            actions = {
                IconButton(onClick = onEditSiteClick, enabled = canEdit) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.site_detail_edit_site),
                    )
                }
            },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun SiteDetailContent(
    uiState: SiteDetailUiState,
    onFieldNoteClick: (FieldNoteId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = dimensionResource(R.dimen.spacing_screen),
            top = dimensionResource(R.dimen.spacing_screen),
            end = dimensionResource(R.dimen.spacing_screen),
            bottom = dimensionResource(R.dimen.spacing_fab_clearance),
        ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_screen_section)),
    ) {
        item { SiteSummary(uiState = uiState) }
        item {
            Text(
                text = stringResource(R.string.site_detail_notes_title, uiState.fieldNotes.size),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        if (uiState.notesErrorMessage != null) {
            item {
                Text(
                    text = uiState.notesErrorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        } else if (uiState.fieldNotes.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.site_detail_notes_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun SiteSummary(uiState: SiteDetailUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            dimensionResource(R.dimen.border_card_strong),
            MaterialTheme.colorScheme.outlineVariant,
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_card_content)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_card_content)),
        ) {
            Text(
                text = uiState.siteId?.value?.uppercase().orEmpty(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = uiState.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_chip)),
            ) {
                Text(
                    stringResource(uiState.priority.labelResId),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    stringResource(uiState.status.labelResId),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null)
                Text(
                    text = uiState.location,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_icon_text)),
                )
            }
            if (uiState.description.isNotBlank()) {
                Text(
                    text = uiState.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
