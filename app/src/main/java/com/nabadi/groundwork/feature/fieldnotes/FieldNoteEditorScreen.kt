package com.nabadi.groundwork.feature.fieldnotes

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabadi.groundwork.R
import com.nabadi.groundwork.ui.theme.GroundWorkTheme

@Composable
fun FieldNoteEditorScreen(
    uiState: FieldNoteEditorUiState,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDestructiveActionClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            FieldNoteEditorTopBar(
                isEditing = uiState.isEditing,
                onBackClick = onBackClick,
            )
        },
        bottomBar = {
            FieldNoteEditorBottomBar(
                isEditing = uiState.isEditing,
                isSaving = uiState.isSaving,
                canSave = uiState.canSave,
                onSaveClick = onSaveClick,
                isDeleting = uiState.isDeleting,
                onDestructiveActionClick = onDestructiveActionClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.spacing_screen_horizontal)),
        ) {
            if (uiState.isLocalDraft) {
                DraftStatusCard()
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))
            }

            FieldNoteTitleField(
                value = uiState.title,
                onValueChange = onTitleChange,
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))

            FieldNoteBodyField(
                value = uiState.body,
                onValueChange = onBodyChange,
                modifier = Modifier.weight(1f),
            )

            uiState.errorMessage?.let { errorMessage ->
                FieldNoteEditorErrorMessage(errorMessage = errorMessage)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldNoteEditorTopBar(
    isEditing: Boolean,
    onBackClick: () -> Unit,
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
fun BackButton(onBackClick: () -> Unit) {
    IconButton(
        onClick = onBackClick,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.content_description_back),
        )
    }
}

@Composable
private fun FieldNoteEditorBottomBar(
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
                    Text(text = stringResource(R.string.field_note_editor_save))
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
                    if (isEditing) {
                        Text(text = stringResource(R.string.field_note_editor_delete))
                    } else {
                        Text(text = stringResource(R.string.field_note_editor_discard))
                    }
                }
            }
        }
    }
}

@Composable
private fun DraftStatusCard(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_card_content)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.field_note_editor_draft),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }

}

@Composable
private fun FieldNoteTitleField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.field_note_editor_title_label)) },
        placeholder = { Text(text = stringResource(R.string.field_note_editor_title_hint)) },
        textStyle = MaterialTheme.typography.titleLarge,
        maxLines = 2,
    )
}

@Composable
private fun FieldNoteBodyField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.field_note_editor_body_label)) },
        placeholder = { Text(text = stringResource(R.string.field_note_editor_body_hint)) },
        textStyle = MaterialTheme.typography.bodyLarge,
    )
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
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
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
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
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
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
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
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
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
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
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
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
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
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
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
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
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
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
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
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
            onDestructiveActionClick = {},
        )
    }
}

private val emptyDraftPreviewState = FieldNoteEditorUiState(
    title = "",
    body = "",
    isSaving = false,
    isLocalDraft = true,
)

private val filledDraftPreviewState = FieldNoteEditorUiState(
    title = "Generator maintenance log",
    body = "Fuel level checked. Backup generator started successfully during manual test. " +
            "Next inspection is due tomorrow morning. Confirm that the night crew has access " +
            "to the maintenance cabinet and that the temporary access code still works.",
    isSaving = false,
    isLocalDraft = true,
)

private val savingPreviewState = FieldNoteEditorUiState(
    title = "Water damage photo review",
    body = "Initial inspection complete. Evidence photos should be attached once local " +
            "attachment support is implemented.",
    isSaving = true,
    isLocalDraft = true,
)

private val errorPreviewState = FieldNoteEditorUiState(
    title = "North gate safety check",
    body = "Loose temporary fencing reported near the north access point.",
    isSaving = false,
    isLocalDraft = true,
    errorMessage = "Unable to save field note.",
)

private val editingPreviewState = FieldNoteEditorUiState(
    title = "North gate safety check",
    body = "Loose temporary fencing reported near the north access point.",
    isEditing = true,
    isLocalDraft = false,
)
