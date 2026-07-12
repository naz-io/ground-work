package com.nabadi.groundwork.feature.fieldnotes.list

import com.nabadi.groundwork.domain.model.FieldNoteStatus

data class FieldNotesListUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedStatus: FieldNoteStatus? = null,
    val fieldNoteItems: List<FieldNoteListItemUiState> = emptyList(),
    val errorMessage: String? = null,
) {
    val isSearching: Boolean
        get() = searchQuery.isNotBlank()

    val isFiltering: Boolean
        get() = selectedStatus != null

    val hasActiveCriteria: Boolean
        get() = isSearching || isFiltering

    val shouldShowEmptyState: Boolean
        get() = !isLoading && !isError && !hasActiveCriteria && fieldNoteItems.isEmpty()

    val isError: Boolean
        get() = errorMessage != null
}