package com.nabadi.groundwork.feature.sites.editor

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
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.feature.sites.deletingSiteEditorPreviewState
import com.nabadi.groundwork.feature.sites.editingSiteEditorPreviewState
import com.nabadi.groundwork.feature.sites.emptySiteEditorPreviewState
import com.nabadi.groundwork.feature.sites.errorSiteEditorPreviewState
import com.nabadi.groundwork.feature.sites.filledSiteEditorPreviewState
import com.nabadi.groundwork.feature.sites.labelResId
import com.nabadi.groundwork.feature.sites.longContentSiteEditorPreviewState
import com.nabadi.groundwork.feature.sites.savingSiteEditorPreviewState
import com.nabadi.groundwork.ui.components.BackButton
import com.nabadi.groundwork.ui.components.FormSection
import com.nabadi.groundwork.ui.components.GroundWorkFilterChip
import com.nabadi.groundwork.ui.components.GroundWorkLoadingState
import com.nabadi.groundwork.ui.components.GroundWorkPreviewSurface
import com.nabadi.groundwork.ui.components.GroundWorkPrimaryButton
import com.nabadi.groundwork.ui.components.PREVIEW_API_LEVEL

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
            if (!uiState.isLoading) {
                SiteEditorBottomBar(
                    isEditing = uiState.isEditing,
                    isSaving = uiState.isSaving,
                    canSave = uiState.canSave,
                    onSaveClick = onSaveClick,
                    isDeleting = uiState.isDeleting,
                    onDestructiveActionClick = onDestructiveActionClick,
                )
            }
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                GroundWorkLoadingState()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(
                        start = dimensionResource(R.dimen.spacing_screen_horizontal),
                        end = dimensionResource(R.dimen.spacing_screen_horizontal),
                        top = dimensionResource(R.dimen.spacing_screen_horizontal),
                    ),
            ) {
                val areFieldsEnabled = !uiState.isBusy
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

                if (!uiState.errorMessage.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimensionResource(R.dimen.spacing_list_item)),
                        contentAlignment = Alignment.Center,
                    ) {
                        SiteEditorErrorMessage(errorMessage = uiState.errorMessage)
                    }
                }
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
    Column(modifier = modifier) {
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
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
            },
            navigationIcon = {
                BackButton(onBackClick = onBackClick)
            },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
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
            GroundWorkPrimaryButton(
                text = stringResource(
                    if (isEditing) {
                        R.string.site_editor_save_changes
                    } else {
                        R.string.site_editor_create_site
                    },
                ),
                onClick = onSaveClick,
                leadingIcon =
                    if (isEditing) {
                        Icons.Filled.Save
                    } else {
                        Icons.Filled.AddLocationAlt
                    },
                enabled = canSave && !isSaving && !isDeleting,
                isLoading = isSaving,
            )

            TextButton(
                onClick = onDestructiveActionClick,
                enabled = !isSaving && !isDeleting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.height_primary_action_button)),
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimensionResource(R.dimen.size_button_progress_indicator)),
                        strokeWidth = dimensionResource(R.dimen.stroke_width_button_progress_indicator),
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Text(
                        text = stringResource(
                            if (isEditing) {
                                R.string.site_editor_delete
                            } else {
                                R.string.site_editor_discard_draft
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
        FormSection(label = stringResource(R.string.site_editor_name_label)) {
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
        FormSection(label = stringResource(R.string.site_editor_location_label)) {
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
        FormSection(label = stringResource(R.string.site_editor_description_label)) {
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
                GroundWorkFilterChip(
                    selected = priority == selectedPriority,
                    enabled = enabled,
                    onClick = { onPriorityChange(priority) },
                    label = stringResource(priority.labelResId),
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
                GroundWorkFilterChip(
                    selected = status == selectedStatus,
                    onClick = { onStatusChange(status) },
                    enabled = enabled,
                    label = stringResource(status.labelResId),
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
        FormSection(label = title) {
            content()
        }
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

@Preview(
    name = "Empty Draft",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_EmptyDraft() {
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = emptySiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = emptySiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = filledSiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = filledSiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = savingSiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = savingSiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = errorSiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = errorSiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = editingSiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = editingSiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = longContentSiteEditorPreviewState,
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
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = longContentSiteEditorPreviewState,
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
    name = "Deleting",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_Deleting() {
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = deletingSiteEditorPreviewState,
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
    name = "Dark Mode - Deleting",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_DarkMode_Deleting() {
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = deletingSiteEditorPreviewState,
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
    name = "Loading",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_Loading() {
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = SiteEditorUiState(
                isLoading = true,
                isEditing = true,
            ),
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
    name = "Dark Mode - Loading",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun SiteEditorScreenPreview_DarkMode_Loading() {
    GroundWorkPreviewSurface {
        SiteEditorScreen(
            uiState = SiteEditorUiState(
                isLoading = true,
                isEditing = true,
            ),
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
