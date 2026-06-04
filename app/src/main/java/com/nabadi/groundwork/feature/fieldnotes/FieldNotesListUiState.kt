package com.nabadi.groundwork.feature.fieldnotes

import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteStatus

data class FieldNotesListUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedStatus: FieldNoteStatus? = null,
    val fieldNotes: List<FieldNote> = emptyList(),
    val errorMessage: String? = null
) {
    val isSearching: Boolean
        get() = searchQuery.isNotBlank()

    val isFiltering: Boolean
        get() = selectedStatus != null

    val hasActiveCriteria: Boolean
        get() = isSearching || isFiltering

    val isError: Boolean
        get() = errorMessage != null
}