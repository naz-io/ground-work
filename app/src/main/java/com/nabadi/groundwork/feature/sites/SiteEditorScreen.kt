package com.nabadi.groundwork.feature.sites

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.feature.fieldnotes.BackButton
import com.nabadi.groundwork.ui.theme.GroundWorkTheme

@Composable
fun SiteEditorScreen(
    uiState: SiteEditorUiState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onStatusChange: (SiteStatus) -> Unit,
    onPriorityChange: (SitePriority) -> Unit,
    onSaveClick: () -> Unit,
    onDestructiveActionClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            SiteEditorTopBar(
                isEditing = uiState.isEditing,
                onBackClick = onBackClick,
            )
        },
        bottomBar = {
            SiteEditorBottomBar(
                isEditing = uiState.isEditing,
                isSaving = uiState.isSaving,
                canSave = uiState.canSave,
                onSaveClick = onSaveClick,
                isDeleting = uiState.isDeleting,
                onDestructiveActionClick = onDestructiveActionClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.spacing_screen_horizontal)),
        ) {
            val areFieldsEnabled = !uiState.isSaving && !uiState.isDeleting
            SiteNameField(
                name = uiState.name,
                onValueChange = onNameChange,
                enabled = areFieldsEnabled,
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))

            SiteLocationField(
                location = uiState.location,
                onValueChange = onLocationChange,
                enabled = areFieldsEnabled,
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))

            SitePrioritySelector(
                selectedPriority = uiState.priority,
                onPriorityChange = onPriorityChange,
                enabled = areFieldsEnabled,
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))

            SiteStatusSelector(
                selectedStatus = uiState.status,
                onStatusChange = onStatusChange,
                enabled = areFieldsEnabled,
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))

            SiteDescriptionField(
                description = uiState.description,
                onValueChange = onDescriptionChange,
                enabled = areFieldsEnabled,
                modifier = Modifier.weight(1f),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.height_error_message_area)),
                contentAlignment = Alignment.Center,
            ) {
                SiteEditorErrorMessage(errorMessage = uiState.errorMessage)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SiteEditorTopBar(
    isEditing: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    if (isEditing) {
                        R.string.site_editor_edit_title
                    } else {
                        R.string.site_editor_new_title
                    }
                ),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        navigationIcon = {
            BackButton(onBackClick = onBackClick)
        },
        modifier = modifier,
    )
}

@Composable
private fun SiteEditorBottomBar(
    isEditing: Boolean,
    isSaving: Boolean,
    canSave: Boolean,
    onSaveClick: () -> Unit,
    isDeleting: Boolean,
    onDestructiveActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
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
                onClick = onSaveClick,
                enabled = canSave && !isSaving && !isDeleting,
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
                        text =
                            if (isEditing)
                                stringResource(R.string.site_editor_save_changes)
                            else
                                stringResource(R.string.site_editor_save)
                    )
                }
            }

            OutlinedButton(
                onClick = onDestructiveActionClick,
                enabled = !isSaving && !isDeleting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.height_primary_action_button)),
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimensionResource(R.dimen.size_button_progress_indicator)),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(
                        text = stringResource(
                            if (isEditing) {
                                R.string.site_editor_delete
                            } else {
                                R.string.site_editor_discard
                            }
                        ),
                    )
                }
            }
        }
    }
}


@Composable
private fun SiteNameField(
    name: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
    ) {
        Text(
            text = stringResource(R.string.site_editor_name_label),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedTextField(
            value = name,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(R.string.site_editor_name_hint))
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
        )
    }
}

@Composable
private fun SiteLocationField(
    location: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
    ) {
        Text(
            text = stringResource(R.string.site_editor_location_label),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedTextField(
            value = location,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(R.string.site_editor_location_hint))
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
        )
    }
}

@Composable
private fun SiteDescriptionField(
    description: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
    ) {
        Text(
            text = stringResource(R.string.site_editor_description_label),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedTextField(
            value = description,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text(text = stringResource(R.string.site_editor_description_hint))
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            minLines = 3,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SitePrioritySelector(
    selectedPriority: SitePriority,
    onPriorityChange: (SitePriority) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    SiteChoiceSection(
        title = stringResource(R.string.site_editor_priority_label),
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_chip)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
        ) {
            SitePriority.entries.forEach { priority ->
                FilterChip(
                    selected = priority == selectedPriority,
                    onClick = { onPriorityChange(priority) },
                    enabled = enabled,
                    label = {
                        Text(text = stringResource(priority.labelResId))
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SiteStatusSelector(
    selectedStatus: SiteStatus,
    onStatusChange: (SiteStatus) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    SiteChoiceSection(
        title = stringResource(R.string.site_editor_status_label),
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_chip)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
        ) {
            SiteStatus.entries.forEach { status ->
                FilterChip(
                    selected = status == selectedStatus,
                    onClick = { onStatusChange(status) },
                    enabled = enabled,
                    label = {
                        Text(text = stringResource(status.labelResId))
                    },
                )
            }
        }
    }
}

@Composable
private fun SiteChoiceSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        content()
    }
}

@Composable
private fun SiteEditorErrorMessage(
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!errorMessage.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.size_icon_small)),
                tint = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.spacing_icon_text)))
        }

        Text(
            text = errorMessage.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            minLines = 1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val SiteStatus.labelResId: Int
    get() = when (this) {
        SiteStatus.ACTIVE -> R.string.site_status_active
        SiteStatus.COMPLETED -> R.string.site_status_completed
        SiteStatus.ARCHIVED -> R.string.site_status_archived
    }

private val SitePriority.labelResId: Int
    get() = when (this) {
        SitePriority.LOW -> R.string.site_priority_low
        SitePriority.NORMAL -> R.string.site_priority_normal
        SitePriority.HIGH -> R.string.site_priority_high
        SitePriority.URGENT -> R.string.site_priority_urgent
    }


private const val PREVIEW_API_LEVEL = 35

@Preview(
    name = "Empty Draft",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_EmptyDraft() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = emptyDraftPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
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
private fun SiteEditorScreenPreview_DarkMode_Empty() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = emptyDraftPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
        )
    }
}

@Preview(
    name = "Filled Draft",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_FilledDraft() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = filledDraftPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
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
private fun SiteEditorScreenPreview_DarkMode_FilledDraft() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = filledDraftPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
        )
    }
}

@Preview(
    name = "Saving",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_Saving() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = savingPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
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
private fun SiteEditorScreenPreview_DarkMode_Saving() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = savingPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
        )
    }
}

@Preview(
    name = "Error",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_Error() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = errorPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
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
private fun SiteEditorScreenPreview_DarkMode_Error() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = errorPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
        )
    }
}

@Preview(
    name = "Editing",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_Editing() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = editingPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
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
private fun SiteEditorScreenPreview_DarkMode_Editing() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = editingPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
        )
    }
}

@Preview(
    name = "Long Content",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_LongContent() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = longContentPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
        )
    }
}

@Preview(
    name = "Dark Mode - Long Content",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_DarkMode_LongContent() {
    GroundWorkTheme {
        SiteEditorScreen(
            uiState = longContentPreviewState,
            onNameChange = {},
            onDescriptionChange = {},
            onLocationChange = {},
            onStatusChange = {},
            onPriorityChange = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
            onBackClick = {},
        )
    }
}

private val emptyDraftPreviewState = SiteEditorUiState(
    name = "",
    location = "",
    description = "",
    isSaving = false,
    isEditing = false,
)

private val filledDraftPreviewState = SiteEditorUiState(
    name = "North Warehouse",
    location = "Sector A · 125 Industrial Road",
    description = "Main storage facility with recurring loading-bay access issues and damaged pallet racks.",
    isSaving = false,
    isEditing = false,
)

private val savingPreviewState = SiteEditorUiState(
    name = "River Pump Station",
    location = "Riverbend Service Road",
    description = "Pump station with standing water reports, generator checks, and access-road erosion.",
    isSaving = true,
    isEditing = false,
)

private val errorPreviewState = SiteEditorUiState(
    name = "East Service Tunnel",
    location = "Transit Yard · East Access",
    description = "Tunnel inspection route with lighting issues and confined-space safety notes.",
    isSaving = false,
    isEditing = false,
    errorMessage = "Unable to save site.",
)

private val editingPreviewState = SiteEditorUiState(
    name = "Pine Creek Bridge",
    location = "Pine Creek Road · Mile 18",
    description = "Bridge maintenance zone with temporary fencing, drainage concerns, and weekly inspection notes.",
    isSaving = false,
    isEditing = true,
)

private val longContentPreviewState = SiteEditorUiState(
    name = "North Warehouse Emergency Overflow Staging Area With Restricted Vehicle Access",
    location = "Sector A · 125 Industrial Road · Rear service entrance beside loading docks 7 through 12",
    description = "This site has recurring access issues during morning freight windows, damaged pallet racks near the west wall, temporary fencing around the exterior loading bay, and repeated safety notes from crews about poor lighting, blocked emergency exits, and unclear staging instructions during shift changes.",
    priority = SitePriority.URGENT,
    status = SiteStatus.ACTIVE,
    isSaving = false,
    isEditing = true,
)