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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.ui.theme.GroundWorkTheme

@Composable
fun FieldNotesListScreen(
    uiState: FieldNotesListUiState,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (FieldNoteStatus?) -> Unit,
    onAddFieldNoteClick: () -> Unit,
    onFieldNoteClick: (FieldNoteId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shouldShowAddButton = !uiState.isLoading && !uiState.isError

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

                    !uiState.hasActiveCriteria && uiState.fieldNotes.isEmpty() -> EmptyFieldNotesState()
                    else -> FieldNotesContent(
                        selectedStatus = uiState.selectedStatus,
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
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
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
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
            FieldNotesSearchField(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
            )
        }

        item {
            FieldNotesStatusFilters(
                selectedStatus = selectedStatus,
                onStatusFilterChange = onStatusFilterChange,
            )
        }

        if (fieldNotes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(dimensionResource(R.dimen.padding_card_content)),
                    contentAlignment = Alignment.Center,
                ) {
                    NoMatchingFieldNotesState(
                        onClearCriteriaClick = {
                            onSearchQueryChange("")
                            onStatusFilterChange(null)
                        }
                    )
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
private fun FieldNotesSearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(text = stringResource(R.string.field_notes_search_label))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (searchQuery.isNotBlank()) {
                IconButton(
                    onClick = { onSearchQueryChange("") },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.field_notes_search_clear_content_description),
                    )
                }
            }
        },
        singleLine = true,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldNotesStatusFilters(
    selectedStatus: FieldNoteStatus?,
    onStatusFilterChange: (FieldNoteStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_list_item)),
    ) {
        item {
            FieldNotesStatusFilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusFilterChange(null) },
                label = stringResource(R.string.field_notes_list_filter_all),
            )
        }
        items(
            items = FieldNoteStatus.entries,
            key = { it },
            contentType = { "FilterOption" },
        ) { statusFilter ->
            FieldNotesStatusFilterChip(
                selected = selectedStatus == statusFilter,
                onClick = { onStatusFilterChange(statusFilter) },
                label = stringResource(statusFilter.labelResId),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldNotesStatusFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = label,
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
                text = fieldNote.title.ifBlank { stringResource(R.string.field_notes_list_untitled_note_title) },
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
private fun EmptyFieldNotesState(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.field_notes_list_empty),
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun NoMatchingFieldNotesState(
    onClearCriteriaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_screen_section)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.field_notes_list_no_matches_title),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.field_notes_list_no_matches_description),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            onClick = onClearCriteriaClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.field_notes_list_no_matches_action),
            )
        }
    }
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

private const val PREVIEW_API_LEVEL = 35

@Preview(
    name = "Content",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_Content() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                fieldNotes = previewFieldNotes,
                isLoading = false,
            ),
            onSearchQueryChange = {},
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
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_Content_Dark() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                fieldNotes = previewFieldNotes,
                isLoading = false,
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Loading",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_Loading() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(isLoading = true),
            onSearchQueryChange = {},
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
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_Loading_Dark() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(isLoading = true),
            onSearchQueryChange = {},
            onAddFieldNoteClick = {},
            onStatusFilterChange = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Empty",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_Empty() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                isLoading = false,
                fieldNotes = emptyList(),
            ),
            onSearchQueryChange = {},
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
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_Empty_Dark() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                fieldNotes = emptyList(),
                isLoading = false,
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "No Filter/Search Matches",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_NoFilterMatches() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                searchQuery = "Testing",
                selectedStatus = FieldNoteStatus.DRAFT,
                fieldNotes = emptyList(),
                isLoading = false,
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "No Filter/Search Matches - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_NoFilterMatches_Dark() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                searchQuery = "Testing",
                selectedStatus = FieldNoteStatus.DRAFT,
                fieldNotes = emptyList(),
                isLoading = false,
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Error",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_Error() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                errorMessage = "Could not connect to the server.",
                isLoading = false,
            ),
            onSearchQueryChange = {},
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
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_Error_Dark() {
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                errorMessage = "Could not connect to the server.",
                isLoading = false,
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onAddFieldNoteClick = {},
            onFieldNoteClick = {},
        )
    }
}

@Preview(
    name = "Field Note Card",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
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
    apiLevel = PREVIEW_API_LEVEL,
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
    apiLevel = PREVIEW_API_LEVEL,
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
    apiLevel = PREVIEW_API_LEVEL,
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
    name = "Empty Filtered/Searched State",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun NoMatchingFieldNotesStatePreview() {
    GroundWorkTheme {
        PreviewSurface {
            NoMatchingFieldNotesState(
                onClearCriteriaClick = {},
            )
        }
    }
}

@Preview(
    name = "Empty Filtered/Search State - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun NoMatchingFieldNotesStatePreview_Dark() {
    GroundWorkTheme {
        PreviewSurface {
            NoMatchingFieldNotesState(
                onClearCriteriaClick = {},
            )
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
        siteId = SiteId("site-north-warehouse"),
        body = "Loose temporary fencing reported near the north access point. Needs follow-up before the evening shift.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_734_220_800_000L,
        updatedAt = 1_734_224_400_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-002"),
        siteId = SiteId("site-east-trailer"),
        title = "Generator maintenance log",
        body = "Fuel level checked. Backup generator started successfully during manual test. Next inspection due tomorrow morning.",
        status = FieldNoteStatus.DRAFT,
        createdAt = 1_734_213_600_000L,
        updatedAt = 1_734_217_200_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-003"),
        siteId = null,
        title = "",
        body = "Material delivery arrived without the updated packing list. Confirm quantities before marking the delivery as accepted.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_734_127_200_000L,
        updatedAt = 1_734_130_800_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-004"),
        siteId = SiteId("site-west-corridor"),
        title = "Water damage photo review",
        body = "Initial inspection complete. Evidence photos should be attached once local attachment support is implemented.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_734_040_800_000L,
        updatedAt = 1_734_044_400_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-005"),
        siteId = SiteId("site-south-stairwell"),
        title = "South stairwell lighting issue",
        body = "Two bulbs are out on the second landing. Visibility is poor after sunset and should be fixed before night operations.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_954_400_000L,
        updatedAt = 1_733_958_000_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-006"),
        siteId = SiteId("site-loading-bay"),
        title = "Cracked concrete near loading bay",
        body = "Surface crack observed near bay three. No immediate trip hazard, but the area should be monitored after the next freeze-thaw cycle.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_868_000_000L,
        updatedAt = 1_733_871_600_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-007"),
        siteId = null,
        title = "Incomplete toolbox talk record",
        body = "Morning crew completed the safety briefing, but the attendance sheet is missing two signatures. Follow up with the site supervisor.",
        status = FieldNoteStatus.DRAFT,
        createdAt = 1_733_781_600_000L,
        updatedAt = 1_733_785_200_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-008"),
        siteId = SiteId("site-west-fence"),
        title = "Temporary drainage channel",
        body = "Drainage channel is working after yesterday's rainfall. Minor pooling remains beside the west fence line.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_695_200_000L,
        updatedAt = 1_733_698_800_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-009"),
        siteId = null,
        title = "Noise complaint follow-up",
        body = "Neighbouring property reported early morning equipment noise. Confirmed work began within permitted hours, but crew was reminded to avoid unnecessary idling.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_733_608_800_000L,
        updatedAt = 1_733_612_400_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-010"),
        siteId = SiteId("site-east-trailer"),
        title = "PPE storage cabinet",
        body = "Gloves and safety glasses are running low in the east trailer. Restock before the next full crew shift.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_522_400_000L,
        updatedAt = 1_733_526_000_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-011"),
        siteId = null,
        title = "",
        body = "Short note with no title to verify the fallback title renders correctly inside a long scrolling list.",
        status = FieldNoteStatus.DRAFT,
        createdAt = 1_733_436_000_000L,
        updatedAt = 1_733_439_600_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-012"),
        siteId = SiteId("site-main-lobby"),
        title = "Elevator access delay",
        body = "Service elevator was unavailable for approximately twenty minutes while another contractor moved equipment. No work was blocked, but schedule impact should be tracked.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_349_600_000L,
        updatedAt = 1_733_353_200_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-013"),
        siteId = null,
        title = "Paint sample approval",
        body = "Client approved the warmer grey sample for the corridor walls. Update finish schedule before procurement.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_733_263_200_000L,
        updatedAt = 1_733_266_800_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-014"),
        siteId = SiteId("site-west-corridor"),
        title = "Very long observation title for west corridor temporary access, loose floor protection, fallen caution signage, and cable routing review",
        body = "This note intentionally has a longer body to test how the card handles multiple lines of field text. The west corridor had several small issues: protective floor covering was loose near the entrance, one caution sign had fallen behind stored materials, and the temporary lighting cable crossed too close to the pedestrian route. None of these items stopped work, but they should be corrected before the next inspection round. This body should wrap across several lines in the preview so the card can be checked for spacing, readability, vertical rhythm, and whether the status label still feels attached to the note rather than floating too far away from the main content.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_176_800_000L,
        updatedAt = 1_733_180_400_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-015"),
        siteId = null,
        title = "Missing label on chemical container",
        body = "Unlabelled container found in the storage area. It has been isolated until contents are confirmed and proper labelling is added.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_733_090_400_000L,
        updatedAt = 1_733_094_000_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-016"),
        siteId = SiteId("site-parking-lot"),
        title = "Parking lot ice patch",
        body = "Small ice patch observed near the employee entrance. Salt applied at 8:15 AM. Recheck before lunch.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_733_004_000_000L,
        updatedAt = 1_733_007_600_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-017"),
        siteId = null,
        title = "Draft note for follow-up call with supplier about delayed replacement parts, temporary heating unit availability, and revised delivery timing",
        body = "Need to call supplier about delayed replacement parts for the temporary heating unit. Ask whether the replacement control module is already in transit, whether there is an alternative compatible part in local inventory, and whether the revised delivery window affects weekend work. Also confirm who is responsible for notifying the night crew if temporary heat is still unavailable by Friday afternoon.",
        status = FieldNoteStatus.DRAFT,
        createdAt = 1_732_917_600_000L,
        updatedAt = 1_732_921_200_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-018"),
        siteId = SiteId("site-east-entrance"),
        title = "Inspection route changed",
        body = "Started inspection from the east entrance instead of the main lobby because of active material movement near reception.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_732_831_200_000L,
        updatedAt = 1_732_834_800_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-019"),
        siteId = null,
        title = "Old scaffold tag removed",
        body = "Expired scaffold tag removed and replaced after inspection. Crew notified that only the updated tagged access point should be used.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_732_744_800_000L,
        updatedAt = 1_732_748_400_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-020"),
        siteId = SiteId("site-mechanical-room"),
        title = "Final cleanup reminder for mechanical room floor before site handoff",
        body = "Before leaving site, confirm that loose fasteners, packaging, and offcuts are removed from the mechanical room floor. Check under the temporary workbench, behind the stacked duct sections, around the west wall conduit run, and beside the storage cabinet where small metal pieces tend to collect. This is intentionally verbose preview content to test how the last item behaves near the floating action button and whether enough bottom padding exists when the final card has a tall body.",
        status = FieldNoteStatus.ACTIVE,
        createdAt = 1_732_658_400_000L,
        updatedAt = 1_732_662_000_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-021"),
        siteId = null,
        title = "Extremely long archived note title used only for Compose preview stress testing when a real field note title becomes much longer than expected",
        body = "Archived inspection record kept for visual regression testing. The title is intentionally too long because real users do not respect ideal UI constraints. This preview helps verify that the card still looks acceptable when the title wraps to two or three lines, the body also wraps, and the status label remains readable without awkward clipping or crowded spacing.",
        status = FieldNoteStatus.ARCHIVED,
        createdAt = 1_732_572_000_000L,
        updatedAt = 1_732_575_600_000L,
    ),
    FieldNote(
        id = FieldNoteId("field-note-022"),
        siteId = null,
        title = "",
        body = "This is a very long body attached to an empty title. It exists to test the untitled fallback together with a large amount of body text. The note should still show the fallback title clearly, and the long paragraph below it should not make the fallback feel like an accidental label. If this looks bad, the card probably needs stronger title styling, a status chip, or a more deliberate content hierarchy.",
        status = FieldNoteStatus.DRAFT,
        createdAt = 1_732_485_600_000L,
        updatedAt = 1_732_489_200_000L,
    ),
)