package com.nabadi.groundwork.feature.fieldnotes.list

import android.content.res.Configuration
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.ui.components.GroundWorkFilterChip
import com.nabadi.groundwork.feature.fieldnotes.PREVIEW_API_LEVEL
import com.nabadi.groundwork.feature.fieldnotes.labelResId
import com.nabadi.groundwork.feature.fieldnotes.previewFieldNotes
import com.nabadi.groundwork.ui.components.GroundWorkLoadingState
import com.nabadi.groundwork.ui.components.GroundWorkPreviewSurface
import com.nabadi.groundwork.ui.components.GroundWorkShapes

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
        topBar = { FieldNotesTopBar() },
        floatingActionButton = {
            if (shouldShowAddButton) {
                FloatingActionButton(
                    onClick = onAddFieldNoteClick,
                    shape = GroundWorkShapes.Control,
                ) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    uiState.isLoading -> GroundWorkLoadingState()
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
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_form_section)),
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
        placeholder = {
            Text(
                text = stringResource(R.string.field_notes_list_search_placeholder),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
                        contentDescription = stringResource(R.string.field_notes_list_search_clear_content_description),
                    )
                }
            }
        },
        singleLine = true,
        shape = GroundWorkShapes.Control,
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
            GroundWorkFilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusFilterChange(null) },
                enabled = true,
                label = stringResource(R.string.field_notes_list_filter_all),
            )
        }
        items(
            items = FieldNoteStatus.entries,
            key = { it },
            contentType = { "FilterOption" },
        ) { statusFilter ->
            GroundWorkFilterChip(
                selected = selectedStatus == statusFilter,
                onClick = { onStatusFilterChange(statusFilter) },
                enabled = true,
                label = stringResource(statusFilter.labelResId),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldNotesTopBar(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.field_notes_list_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
            },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
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

@Preview(
    name = "Content",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNotesListScreenPreview_Content() {
    GroundWorkPreviewSurface {
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
    GroundWorkPreviewSurface {
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
    GroundWorkPreviewSurface {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                isLoading = true
            ),
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
    GroundWorkPreviewSurface {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                isLoading = true
            ),
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
    GroundWorkPreviewSurface {
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
    GroundWorkPreviewSurface {
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
    GroundWorkPreviewSurface {
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
    GroundWorkPreviewSurface {
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
    GroundWorkPreviewSurface {
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
    GroundWorkPreviewSurface {
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
    name = "Error State",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun ErrorStatePreview() {
    GroundWorkPreviewSurface {
        ErrorState(
            errorMessage = "Could not connect to the server.",
        )
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
    GroundWorkPreviewSurface {
        ErrorState(
            errorMessage = "Could not connect to the server.",
        )
    }
}

@Preview(
    name = "Empty Filtered/Searched State",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun NoMatchingFieldNotesStatePreview() {
    GroundWorkPreviewSurface {
        NoMatchingFieldNotesState(
            onClearCriteriaClick = {},
        )
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
    GroundWorkPreviewSurface {
        NoMatchingFieldNotesState(
            onClearCriteriaClick = {},
        )
    }
}