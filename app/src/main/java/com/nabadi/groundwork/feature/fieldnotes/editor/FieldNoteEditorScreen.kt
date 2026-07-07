package com.nabadi.groundwork.feature.fieldnotes.editor

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.ui.components.BackButton
import com.nabadi.groundwork.ui.components.FormSection
import com.nabadi.groundwork.ui.components.TechnicalLabel
import com.nabadi.groundwork.ui.format.absoluteDateTimeLabel
import com.nabadi.groundwork.ui.theme.GroundWorkTheme

@Composable
fun FieldNoteEditorScreen(
    uiState: FieldNoteEditorUiState,
    onTitleChange: (String) -> Unit,
    onAssociatedSiteChange: (SiteId?) -> Unit,
    onStatusChange: (FieldNoteStatus) -> Unit,
    onBodyChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDiscardClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            FieldNoteEditorTopBar(
                isEditing = uiState.isEditing,
                showDeleteAction = uiState.isEditing,
                onBackClick = onBackClick,
                onDeleteClick = onDeleteClick,
            )
        },
        bottomBar = {
            FieldNoteEditorBottomBar(
                isBusy = uiState.isBusy,
                isEditing = uiState.isEditing,
                isSaving = uiState.isSaving,
                canSave = uiState.canSave,
                onSaveClick = onSaveClick,
                onDiscardClick = onDiscardClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.spacing_screen_horizontal))
                .padding(top = dimensionResource(R.dimen.spacing_list_item)),
        ) {
            if (uiState.isEditing && uiState.updatedAt != null) {
                FieldNoteMetadataRow(
                    lastUpdated = uiState.updatedAt.absoluteDateTimeLabel(),
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_divider_vertical)))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))
            }

            FieldNoteTitleField(
                value = uiState.title,
                onValueChange = onTitleChange,
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))

            AssociatedSiteField(
                selectedSiteId = uiState.siteId,
                availableSites = uiState.availableSites,
                onAssociatedSiteChange = onAssociatedSiteChange,
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))

            FieldNoteStatusChips(
                selectedStatus = uiState.status,
                onStatusChange = onStatusChange,
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))

            FieldNoteBodyField(
                value = uiState.body,
                onValueChange = onBodyChange,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))

            uiState.errorMessage?.let { errorMessage ->
                FieldNoteEditorErrorMessage(errorMessage = errorMessage)
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldNoteEditorTopBar(
    isEditing: Boolean,
    showDeleteAction: Boolean,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    if (isEditing) {
                        R.string.field_note_editor_edit_title
                    } else {
                        R.string.field_note_editor_new_title
                    }
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
        },
        navigationIcon = {
            BackButton(onBackClick = onBackClick)
        },
        actions = {
            if (showDeleteAction) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.field_note_editor_delete),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun FieldNoteEditorBottomBar(
    isBusy: Boolean,
    isEditing: Boolean,
    isSaving: Boolean,
    canSave: Boolean,
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_bottom_action_horizontal),
                    vertical = dimensionResource(R.dimen.spacing_list_item),
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
        ) {
            Button(
                onClick = {
                    if (!isBusy) {
                        onSaveClick()
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.height_primary_action_button)),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimensionResource(R.dimen.size_button_progress_indicator)),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(
                        text = stringResource(
                            if (isEditing) {
                                R.string.field_note_editor_save_changes
                            } else {
                                R.string.field_note_editor_create_note
                            }
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            TextButton(
                onClick = onDiscardClick,
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(
                        if (isEditing) {
                            R.string.field_note_editor_cancel_changes
                        } else {
                            R.string.field_note_editor_discard_draft
                        }
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing,
                )
            }
        }
    }
}

@Composable
private fun FieldNoteMetadataRow(
    lastUpdated: String,
    modifier: Modifier = Modifier,
) {
    TechnicalLabel(
        modifier = modifier,
        text = stringResource(R.string.field_note_editor_last_modified, lastUpdated),
    )
}

@Composable
private fun FieldNoteTitleField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FormSection(
        label = stringResource(R.string.field_note_editor_title_label),
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(R.string.field_note_editor_title_hint)) },
            textStyle = MaterialTheme.typography.titleMedium,
            maxLines = 2,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssociatedSiteField(
    selectedSiteId: SiteId?,
    availableSites: List<SiteOptionUiState>,
    onAssociatedSiteChange: (SiteId?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val unassignedLabel = stringResource(R.string.field_note_editor_site_unassigned)
    val selectedSiteLabel = availableSites
        .find { it.id == selectedSiteId }
        ?.name
        ?: unassignedLabel

    FormSection(
        label = stringResource(R.string.field_note_editor_associated_site_label),
        modifier = modifier,
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = selectedSiteLabel,
                onValueChange = {},
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true,
                    )
                    .fillMaxWidth(),
                readOnly = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(text = unassignedLabel) },
                    onClick = {
                        onAssociatedSiteChange(null)
                        expanded = false
                    },
                )

                availableSites.forEach { site ->
                    DropdownMenuItem(
                        text = { Text(text = site.name) },
                        onClick = {
                            onAssociatedSiteChange(site.id)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldNoteStatusChips(
    selectedStatus: FieldNoteStatus,
    onStatusChange: (FieldNoteStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    FormSection(
        label = stringResource(R.string.field_note_editor_status_label),
        modifier = modifier,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            StatusChip(
                text = stringResource(R.string.field_note_status_draft),
                selected = selectedStatus == FieldNoteStatus.DRAFT,
                onClick = { onStatusChange(FieldNoteStatus.DRAFT) },
                modifier = Modifier.weight(1f),
            )
            StatusChip(
                text = stringResource(R.string.field_note_status_active),
                selected = selectedStatus == FieldNoteStatus.ACTIVE,
                onClick = { onStatusChange(FieldNoteStatus.ACTIVE) },
                modifier = Modifier.weight(1f),
            )
            StatusChip(
                text = stringResource(R.string.field_note_status_archived),
                selected = selectedStatus == FieldNoteStatus.ARCHIVED,
                onClick = { onStatusChange(FieldNoteStatus.ARCHIVED) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(dimensionResource(R.dimen.height_status_chip)),
        color = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
private fun FieldNoteBodyField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FormSection(
        label = stringResource(R.string.field_note_editor_body_label),
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = { Text(text = stringResource(R.string.field_note_editor_body_hint)) },
            textStyle = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun FieldNoteEditorErrorMessage(
    errorMessage: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = errorMessage,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
    )
}

private const val PREVIEW_API_LEVEL = 35

private val previewSiteOptions = listOf(
    SiteOptionUiState(
        id = SiteId("site-generator-room"),
        name = "Generator Room",
    ),
    SiteOptionUiState(
        id = SiteId("site-west-entrance"),
        name = "West Entrance",
    ),
    SiteOptionUiState(
        id = SiteId("site-north-gate"),
        name = "North Gate",
    ),
)

@Preview(
    name = "Empty Draft",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_EmptyDraft() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = emptyDraftPreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onBodyChange = {},
            onStatusChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Empty",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_DarkMode_Empty() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = emptyDraftPreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Filled Draft",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_FilledDraft() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = filledDraftPreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Filled Draft",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_DarkMode_Filled() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = filledDraftPreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Saving",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_Saving() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = savingPreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Saving",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_DarkMode_Saving() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = savingPreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Error",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_Error() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = errorPreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Error",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_DarkMode_Error() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = errorPreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Editing",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_Editing() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = editingPreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Editing",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_DarkMode_Editing() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = editingPreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Editing - Unassigned Active",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_EditingUnassignedActive() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = editingUnassignedActivePreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Editing Unassigned Active",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteEditorScreenPreview_DarkMode_EditingUnassignedActive() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = editingUnassignedActivePreviewState,
            onTitleChange = {},
            onAssociatedSiteChange = {},
            onStatusChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onDiscardClick = {},
        )
    }
}

private val emptyDraftPreviewState = FieldNoteEditorUiState(
    title = "",
    siteId = null,
    body = "",
    status = FieldNoteStatus.DRAFT,
    updatedAt = null,
    isSaving = false,
)

private val filledDraftPreviewState = FieldNoteEditorUiState(
    title = "Generator maintenance log",
    siteId = SiteId("site-generator-room"),
    availableSites = previewSiteOptions,
    body = "Fuel level checked. Backup generator started successfully during manual test. " +
            "Next inspection is due tomorrow morning. Confirm that the night crew has access " +
            "to the maintenance cabinet and that the temporary access code still works.",
    status = FieldNoteStatus.DRAFT,
    updatedAt = null,
    isSaving = false,
)

private val savingPreviewState = FieldNoteEditorUiState(
    title = "Water damage photo review",
    siteId = SiteId("site-west-entrance"),
    availableSites = previewSiteOptions,
    body = "Initial inspection complete. Evidence photos should be attached once local " +
            "attachment support is implemented.",
    status = FieldNoteStatus.ACTIVE,
    updatedAt = 1698791640000L,
    isSaving = true,
)

private val errorPreviewState = FieldNoteEditorUiState(
    title = "North gate safety check",
    siteId = null,
    availableSites = previewSiteOptions,
    body = "Loose temporary fencing reported near the north access point.",
    status = FieldNoteStatus.ACTIVE,
    updatedAt = 1698798840000L,
    isSaving = false,
    errorMessage = "Unable to save this field note.",
)

private val editingPreviewState = FieldNoteEditorUiState(
    title = "North gate safety check",
    siteId = SiteId("site-north-gate"),
    availableSites = previewSiteOptions,
    body = "Loose temporary fencing reported near the north access point.",
    status = FieldNoteStatus.ARCHIVED,
    updatedAt = 1698806040000L,
    isEditing = true,
)

private val editingUnassignedActivePreviewState = FieldNoteEditorUiState(
    title = "Loose cable follow-up",
    siteId = null,
    availableSites = previewSiteOptions,
    body = "Temporary cable cover needs to be checked again after the afternoon shift.",
    status = FieldNoteStatus.ACTIVE,
    updatedAt = 1698813240000L,
    isEditing = true,
)