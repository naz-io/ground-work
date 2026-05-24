package com.nabadi.groundwork.feature.fieldnotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.ui.theme.GroundWorkTheme

@Composable
fun FieldNotesListScreen(
    uiState: FieldNotesUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.spacing_screen)),
        contentAlignment = Alignment.Center,
    ) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.errorMessage != null -> ErrorState(
                errorMessage = uiState.errorMessage,
            )

            uiState.fieldNotes.isEmpty() -> EmptyState()
            else -> FieldNotesContent(
                fieldNotes = uiState.fieldNotes,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
private fun FieldNotesContent(
    fieldNotes: List<FieldNote>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
    ) {
        item(key = "header") {
            Text(
                text = stringResource(R.string.field_notes_list_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        items(
            items = fieldNotes,
            key = { it.id.value },
            contentType = { "FieldNote" },
        ) { fieldNote ->
            FieldNoteCard(fieldNote = fieldNote)
        }
    }
}

@Composable
private fun FieldNoteCard(
    fieldNote: FieldNote,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_card_content)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_card)),
        ) {
            Text(
                text = fieldNote.title.ifBlank { "(untitled)" },
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = fieldNote.body,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = fieldNote.status.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "No field notes yet.",
        modifier = modifier,
    )
}

@Composable
private fun ErrorState(
    errorMessage: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Error in loading field notes: $errorMessage",
        modifier = modifier,
        color = MaterialTheme.colorScheme.error,
    )
}

@Preview(name = "Loading", showBackground = true)
@Composable
fun FieldNotesListScreenPreview_Loading() {
    GroundWorkTheme {
        FieldNotesListScreen(uiState = FieldNotesUiState(isLoading = true))
    }
}

@Preview(name = "Content", showBackground = true)
@Composable
fun FieldNotesListScreenPreview_Content() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesUiState(
                fieldNotes = previewFieldNotes,
                isLoading = false,
            )
        )
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
fun FieldNotesListScreenPreview_Empty() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesUiState(
                fieldNotes = emptyList(),
                isLoading = false,
            )
        )
    }
}

@Preview(name = "Error", showBackground = true)
@Composable
fun FieldNotesListScreenPreview_Error() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesUiState(
                errorMessage = "oh! Error!",
                isLoading = false,
            )
        )
    }
}


private val previewFieldNotes = listOf(
    FieldNote(
        id = FieldNoteId("field-note-001"),
        title = "North gate safety check",
        body = "Loose temporary fencing reported near the north access point. Needs follow-up before the evening shift.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_734_220_800_000L,
        updatedAt = 1_734_224_400_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-002"),
        title = "Generator maintenance log",
        body = "Fuel level checked. Backup generator started successfully during manual test. Next inspection due tomorrow morning.",
        status = FieldNoteStatus.DRAFT,
        createdAt = 1_734_213_600_000L,
        updatedAt = 1_734_217_200_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-003"),
        title = "",
        body = "Material delivery arrived without the updated packing list. Confirm quantities before marking the delivery as accepted.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_734_127_200_000L,
        updatedAt = 1_734_130_800_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-004"),
        title = "Water damage photo review",
        body = "Initial inspection complete. Evidence photos should be attached once local attachment support is implemented.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_734_040_800_000L,
        updatedAt = 1_734_044_400_000L,
    ),
)
