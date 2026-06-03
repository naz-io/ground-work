package com.nabadi.groundwork.feature.fieldnotes

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.ui.theme.GroundWorkTheme

@Composable
fun FieldNotesListScreen(
    uiState: FieldNotesListUiState,
    onStatusFilterChange: (FieldNoteStatus?) -> Unit,
    onAddFieldNoteClick: () -> Unit,
    onFieldNoteClick: (FieldNoteId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shouldShowAddButton = !uiState.isLoading && uiState.errorMessage == null

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (shouldShowAddButton) {
                FloatingActionButton(onClick = onAddFieldNoteClick) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.field_notes_list_add_content_description),
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(dimensionResource(id = R.dimen.spacing_screen)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_screen_section)),
        ) {
            Text(
                text = stringResource(R.string.field_notes_list_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    uiState.isLoading -> LoadingState()
                    uiState.isError -> uiState.errorMessage?.let {
                        ErrorState(
                            errorMessage = it,
                        )
                    }

                    uiState.isFiltering.not() && uiState.fieldNotes.isEmpty() -> EmptyState()
                    else -> FieldNotesContent(
                        selectedStatus = uiState.selectedStatus,
                        onStatusFilterChange = onStatusFilterChange,
                        fieldNotes = uiState.fieldNotes,
                        onFieldNoteClick = onFieldNoteClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
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
    selectedStatus: FieldNoteStatus?,
    onStatusFilterChange: (FieldNoteStatus?) -> Unit,
    fieldNotes: List<FieldNote>,
    onFieldNoteClick: (FieldNoteId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
        contentPadding = PaddingValues(
            bottom = dimensionResource(R.dimen.spacing_fab_clearance),
        ),
    ) {
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
            ) {
                item {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { onStatusFilterChange(null) },
                        label = {
                            Text(text = stringResource(R.string.field_notes_list_filter_all))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }
                items(
                    items = FieldNoteStatus.entries,
                    key = { it },
                    contentType = { "FilterOption" },
                ) { statusFilter ->
                    FilterChip(
                        selected = selectedStatus == statusFilter,
                        onClick = { onStatusFilterChange(statusFilter) },
                        label = {
                            Text(
                                text = stringResource(statusFilter.labelResId),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }
            }
        }
        if (fieldNotes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(dimensionResource(R.dimen.padding_card_content)),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyFilteredState()
                }
            }
        } else {
            items(
                items = fieldNotes,
                key = { it.id.value },
                contentType = { "FieldNote" },
            ) { fieldNote ->
                FieldNoteCard(
                    fieldNote = fieldNote,
                    onClick = { onFieldNoteClick(fieldNote.id) },
                )
            }
        }
    }
}

@Composable
private fun FieldNoteCard(
    fieldNote: FieldNote,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_card_content)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_card_content)),
        ) {
            Text(
                text = fieldNote.title.ifBlank { stringResource(R.string.field_notes_list_untitled) },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = fieldNote.body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(fieldNote.status.labelResId),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@get:StringRes
private val FieldNoteStatus.labelResId: Int
    get() = when (this) {
        FieldNoteStatus.ACTIVE -> R.string.field_note_status_active
        FieldNoteStatus.DRAFT -> R.string.field_note_status_draft
        FieldNoteStatus.ARCHIVED -> R.string.field_note_status_archived
    }

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.field_notes_list_empty),
        modifier = modifier,
    )
}

@Composable
private fun EmptyFilteredState(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.field_notes_list_empty_filtered),
        modifier = modifier,
    )
}

@Composable
private fun ErrorState(
    errorMessage: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.field_notes_list_error, errorMessage),
        modifier = modifier,
        color = MaterialTheme.colorScheme.error,
    )
}

@Preview(
    name = "Content",
    showBackground = true,
)
@Composable
private fun FieldNotesListScreenPreview_Content() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                fieldNotes = previewFieldNotes,
                isLoading = false,
            ),
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Content - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun FieldNotesListScreenPreview_Content_Dark() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                fieldNotes = previewFieldNotes,
                isLoading = false,
            ),
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Loading",
    showBackground = true,
)
@Composable
private fun FieldNotesListScreenPreview_Loading() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(isLoading = true),
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Loading - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun FieldNotesListScreenPreview_Loading_Dark() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(isLoading = true),
            onAddFieldNoteClick = {},
            onStatusFilterChange = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Empty",
    showBackground = true,
)
@Composable
private fun FieldNotesListScreenPreview_Empty() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                isLoading = false,
                fieldNotes = emptyList(),
            ),
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}


@Preview(
    name = "Empty - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun FieldNotesListScreenPreview_Empty_Dark() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                fieldNotes = emptyList(),
                isLoading = false,
            ),
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "No Filter Matches",
    showBackground = true,
)
@Composable
private fun FieldNotesListScreenPreview_NoFilterMatches() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                selectedStatus = FieldNoteStatus.DRAFT,
                fieldNotes = emptyList(),
                isLoading = false,
            ),
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "No Filter Matches - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun FieldNotesListScreenPreview_NoFilterMatches_Dark() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                selectedStatus = FieldNoteStatus.DRAFT,
                fieldNotes = emptyList(),
                isLoading = false,
            ),
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Error",
    showBackground = true,
)
@Composable
private fun FieldNotesListScreenPreview_Error() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                errorMessage = "Could not connect to the server.",
                isLoading = false,
            ),
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Error - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun FieldNotesListScreenPreview_Error_Dark() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                errorMessage = "Could not connect to the server.",
                isLoading = false,
            ),
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Field Note Card",
    showBackground = true,
)
@Composable
private fun FieldNoteCardPreview() {
    GroundWorkTheme {
        PreviewSurface {
            FieldNoteCard(
                fieldNote = previewFieldNotes.first(),
                onClick = {},
            )
        }
    }
}

@Preview(
    name = "Field Note Card - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun FieldNoteCardPreview_Dark() {
    GroundWorkTheme {
        PreviewSurface {
            FieldNoteCard(
                fieldNote = previewFieldNotes.first(),
                onClick = {},
            )
        }
    }
}

@Preview(
    name = "Error State",
    showBackground = true,
)
@Composable
private fun ErrorStatePreview() {
    GroundWorkTheme {
        PreviewSurface {
            ErrorState(
                errorMessage = "Could not connect to the server.",
            )
        }
    }
}

@Preview(
    name = "Error State - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ErrorStatePreview_Dark() {
    GroundWorkTheme {
        PreviewSurface {
            ErrorState(
                errorMessage = "Could not connect to the server.",
            )
        }
    }
}

@Preview(
    name = "Empty Filtered State",
    showBackground = true,
)
@Composable
private fun EmptyFilteredStatePreview() {
    GroundWorkTheme {
        PreviewSurface {
            EmptyFilteredState()
        }
    }
}

@Preview(
    name = "Empty Filtered State - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun EmptyFilteredStatePreview_Dark() {
    GroundWorkTheme {
        PreviewSurface {
            EmptyFilteredState()
        }
    }
}

@Composable
private fun PreviewSurface(
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_screen)),
        ) {
            content()
        }
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
    FieldNote(
        id = FieldNoteId("field-note-005"),
        title = "South stairwell lighting issue",
        body = "Two bulbs are out on the second landing. Visibility is poor after sunset and should be fixed before night operations.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_954_400_000L,
        updatedAt = 1_733_958_000_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-006"),
        title = "Cracked concrete near loading bay",
        body = "Surface crack observed near bay three. No immediate trip hazard, but the area should be monitored after the next freeze-thaw cycle.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_868_000_000L,
        updatedAt = 1_733_871_600_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-007"),
        title = "Incomplete toolbox talk record",
        body = "Morning crew completed the safety briefing, but the attendance sheet is missing two signatures. Follow up with the site supervisor.",
        status = FieldNoteStatus.DRAFT,
        createdAt = 1_733_781_600_000L,
        updatedAt = 1_733_785_200_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-008"),
        title = "Temporary drainage channel",
        body = "Drainage channel is working after yesterday's rainfall. Minor pooling remains beside the west fence line.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_695_200_000L,
        updatedAt = 1_733_698_800_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-009"),
        title = "Noise complaint follow-up",
        body = "Neighbouring property reported early morning equipment noise. Confirmed work began within permitted hours, but crew was reminded to avoid unnecessary idling.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_733_608_800_000L,
        updatedAt = 1_733_612_400_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-010"),
        title = "PPE storage cabinet",
        body = "Gloves and safety glasses are running low in the east trailer. Restock before the next full crew shift.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_522_400_000L,
        updatedAt = 1_733_526_000_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-011"),
        title = "",
        body = "Short note with no title to verify the fallback title renders correctly inside a long scrolling list.",
        status = FieldNoteStatus.DRAFT,
        createdAt = 1_733_436_000_000L,
        updatedAt = 1_733_439_600_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-012"),
        title = "Elevator access delay",
        body = "Service elevator was unavailable for approximately twenty minutes while another contractor moved equipment. No work was blocked, but schedule impact should be tracked.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_349_600_000L,
        updatedAt = 1_733_353_200_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-013"),
        title = "Paint sample approval",
        body = "Client approved the warmer grey sample for the corridor walls. Update finish schedule before procurement.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_733_263_200_000L,
        updatedAt = 1_733_266_800_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-014"),
        title = "Very long observation title for west corridor temporary access, loose floor protection, fallen caution signage, and cable routing review",
        body = "This note intentionally has a longer body to test how the card handles multiple lines of field text. The west corridor had several small issues: protective floor covering was loose near the entrance, one caution sign had fallen behind stored materials, and the temporary lighting cable crossed too close to the pedestrian route. None of these items stopped work, but they should be corrected before the next inspection round. This body should wrap across several lines in the preview so the card can be checked for spacing, readability, vertical rhythm, and whether the status label still feels attached to the note rather than floating too far away from the main content.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_176_800_000L,
        updatedAt = 1_733_180_400_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-015"),
        title = "Missing label on chemical container",
        body = "Unlabelled container found in the storage area. It has been isolated until contents are confirmed and proper labelling is added.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_090_400_000L,
        updatedAt = 1_733_094_000_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-016"),
        title = "Parking lot ice patch",
        body = "Small ice patch observed near the employee entrance. Salt applied at 8:15 AM. Recheck before lunch.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_733_004_000_000L,
        updatedAt = 1_733_007_600_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-017"),
        title = "Draft note for follow-up call with supplier about delayed replacement parts, temporary heating unit availability, and revised delivery timing",
        body = "Need to call supplier about delayed replacement parts for the temporary heating unit. Ask whether the replacement control module is already in transit, whether there is an alternative compatible part in local inventory, and whether the revised delivery window affects weekend work. Also confirm who is responsible for notifying the night crew if temporary heat is still unavailable by Friday afternoon.",
        status = FieldNoteStatus.DRAFT,
        createdAt = 1_732_917_600_000L,
        updatedAt = 1_732_921_200_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-018"),
        title = "Inspection route changed",
        body = "Started inspection from the east entrance instead of the main lobby because of active material movement near reception.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_732_831_200_000L,
        updatedAt = 1_732_834_800_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-019"),
        title = "Old scaffold tag removed",
        body = "Expired scaffold tag removed and replaced after inspection. Crew notified that only the updated tagged access point should be used.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_732_744_800_000L,
        updatedAt = 1_732_748_400_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-020"),
        title = "Final cleanup reminder for mechanical room floor before site handoff",
        body = "Before leaving site, confirm that loose fasteners, packaging, and offcuts are removed from the mechanical room floor. Check under the temporary workbench, behind the stacked duct sections, around the west wall conduit run, and beside the storage cabinet where small metal pieces tend to collect. This is intentionally verbose preview content to test how the last item behaves near the floating action button and whether enough bottom padding exists when the final card has a tall body.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_732_658_400_000L,
        updatedAt = 1_732_662_000_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-021"),
        title = "Extremely long archived note title used only for Compose preview stress testing when a real field note title becomes much longer than expected",
        body = "Archived inspection record kept for visual regression testing. The title is intentionally too long because real users do not respect ideal UI constraints. This preview helps verify that the card still looks acceptable when the title wraps to two or three lines, the body also wraps, and the status label remains readable without awkward clipping or crowded spacing.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_732_572_000_000L,
        updatedAt = 1_732_575_600_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-022"),
        title = "",
        body = "This is a very long body attached to an empty title. It exists to test the untitled fallback together with a large amount of body text. The note should still show the fallback title clearly, and the long paragraph below it should not make the fallback feel like an accidental label. If this looks bad, the card probably needs stronger title styling, a status chip, or a more deliberate content hierarchy.",
        status = FieldNoteStatus.DRAFT,
        createdAt = 1_732_485_600_000L,
        updatedAt = 1_732_489_200_000L,
    ),
)