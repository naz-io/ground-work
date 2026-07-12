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
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.ui.components.GroundWorkFilterChip
import com.nabadi.groundwork.feature.fieldnotes.labelResId
import com.nabadi.groundwork.feature.fieldnotes.previewFieldNoteItems
import com.nabadi.groundwork.ui.components.GroundWorkFloatingActionButton
import com.nabadi.groundwork.ui.components.GroundWorkLoadingState
import com.nabadi.groundwork.ui.components.GroundWorkPreviewSurface
import com.nabadi.groundwork.ui.components.GroundWorkShapes
import com.nabadi.groundwork.ui.components.GroundWorkPrimaryButton
import com.nabadi.groundwork.ui.components.PREVIEW_API_LEVEL
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
        topBar = { FieldNotesTopBar() },
        floatingActionButton = {
            if (shouldShowAddButton) {
                GroundWorkFloatingActionButton(
                    icon = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.field_notes_list_add_content_description),
                    onClick = onAddFieldNoteClick,
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensionResource(id = R.dimen.spacing_screen)),
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.isLoading -> GroundWorkLoadingState()
                uiState.isError -> uiState.errorMessage?.let {
                    ErrorState(
                        errorMessage = it,
                    )
                }

                uiState.shouldShowEmptyState -> EmptyFieldNotesState()
                else -> FieldNoteItemsContent(
                    selectedStatus = uiState.selectedStatus,
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    onStatusFilterChange = onStatusFilterChange,
                    fieldNoteItems = uiState.fieldNoteItems,
                    onFieldNoteClick = onFieldNoteClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

}

@Composable
private fun FieldNoteItemsContent(
    selectedStatus: FieldNoteStatus?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (FieldNoteStatus?) -> Unit,
    fieldNoteItems: List<FieldNoteListItemUiState>,
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

        if (fieldNoteItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(dimensionResource(R.dimen.padding_card_content)),
                    contentAlignment = Alignment.Center,
                ) {
                    NoMatchingFieldNoteItemsState(
                        onClearCriteriaClick = {
                            onSearchQueryChange("")
                            onStatusFilterChange(null)
                        },
                    )
                }
            }
        } else {
            items(
                items = fieldNoteItems,
                key = { it.note.id.value },
                contentType = { "FieldNote" },
            ) { fieldNoteItems ->
                FieldNoteItemCard(
                    fieldNoteItem = fieldNoteItems,
                    onClick = { onFieldNoteClick(fieldNoteItems.note.id) },
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
private fun NoMatchingFieldNoteItemsState(
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
        GroundWorkPrimaryButton(
            text = stringResource(R.string.field_notes_list_no_matches_action),
            onClick = onClearCriteriaClick,
            leadingIcon = Icons.Filled.FilterAltOff,
        )
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
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                fieldNoteItems = previewFieldNoteItems,
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
                fieldNoteItems = previewFieldNoteItems,
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
    GroundWorkTheme {
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
    GroundWorkTheme {
        FieldNotesListScreen(
            uiState = FieldNotesListUiState(
                isLoading = false,
                fieldNoteItems = emptyList(),
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
                fieldNoteItems = emptyList(),
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
                fieldNoteItems = emptyList(),
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
                fieldNoteItems = emptyList(),
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
    name = "Error State",
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
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun NoMatchingFieldNoteItemsStatePreview() {
    GroundWorkPreviewSurface {
        NoMatchingFieldNoteItemsState(
            onClearCriteriaClick = {},
        )
    }
}

@Preview(
    name = "Empty Filtered/Search State - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun NoMatchingFieldNoteItemsStatePreview_Dark() {
    GroundWorkPreviewSurface {
        NoMatchingFieldNoteItemsState(
            onClearCriteriaClick = {},
        )
    }
}