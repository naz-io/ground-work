package com.nabadi.groundwork.feature.fieldnotes

import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteStatus

data class FieldNotesListUiState(
    val isLoading: Boolean = true,
    val selectedStatus: FieldNoteStatus? = null,
    val fieldNotes: List<FieldNote> = emptyList(),
    val errorMessage: String? = null
) {
    val isFiltering: Boolean
        get() = selectedStatus != null

    val isError: Boolean
        get() = errorMessage != null
}