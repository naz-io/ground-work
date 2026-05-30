package com.nabadi.groundwork.feature.fieldnotes

import android.content.res.Configuration
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            FieldNoteEditorTopBar(
                onBackClick = onBackClick,
            )
        },
        bottomBar = {
            FieldNoteEditorBottomBar(
                isSaving = uiState.isSaving,
                canSave = uiState.canSave,
                onSaveClick = onSaveClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.spacing_screen_horizontal)),
        ) {
            DraftStatusCard(
                isLocalDraft = uiState.isLocalDraft,
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_form_section)))

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
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.field_note_editor_title),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_description_back),
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun FieldNoteEditorBottomBar(
    isSaving: Boolean,
    canSave: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        containerColor = Color.Transparent,
    ) {
        Button(
            onClick = onSaveClick,
            enabled = canSave && !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(text = stringResource(R.string.field_note_editor_save))
            }
        }
    }
}

@Composable
private fun DraftStatusCard(
    isLocalDraft: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isLocalDraft) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
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

@Preview(name = "Empty Draft", showBackground = true)
@Composable
private fun FieldNoteEditorScreenPreview_EmptyDraft() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = emptyDraftPreviewState,
            onTitleChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
        )
    }
}

@Preview(name = "Dark Mode - Empty", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FieldNoteEditorScreenPreview_DarkMode_Empty() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = emptyDraftPreviewState,
            onTitleChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
        )
    }
}

@Preview(name = "Filled Draft", showBackground = true)
@Composable
private fun FieldNoteEditorScreenPreview_FilledDraft() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = filledDraftPreviewState,
            onTitleChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
        )
    }
}

@Preview(name = "Dark Mode - Filled", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FieldNoteEditorScreenPreview_DarkMode_Filled() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = filledDraftPreviewState,
            onTitleChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
        )
    }
}

@Preview(name = "Saving", showBackground = true)
@Composable
private fun FieldNoteEditorScreenPreview_Saving() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = savingPreviewState,
            onTitleChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
        )
    }
}

@Preview(name = "Dark Mode - Saving", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FieldNoteEditorScreenPreview_DarkMode_Saving() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = savingPreviewState,
            onTitleChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
        )
    }
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun FieldNoteEditorScreenPreview_Error() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = errorPreviewState,
            onTitleChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
        )
    }
}

@Preview(name = "Dark Mode - Error", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FieldNoteEditorScreenPreview_DarkMode_Error() {
    GroundWorkTheme {
        FieldNoteEditorScreen(
            uiState = errorPreviewState,
            onTitleChange = {},
            onBodyChange = {},
            onBackClick = {},
            onSaveClick = {},
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
