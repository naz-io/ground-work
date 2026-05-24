package com.nabadi.groundwork.feature.fieldnotes

import com.nabadi.groundwork.domain.model.FieldNote

data class FieldNotesUiState(
    val isLoading: Boolean = true,
    val fieldNotes: List<FieldNote> = emptyList(),
    val errorMessage: String? = null
)