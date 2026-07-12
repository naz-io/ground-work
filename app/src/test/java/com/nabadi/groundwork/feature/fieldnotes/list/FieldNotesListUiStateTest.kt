package com.nabadi.groundwork.feature.fieldnotes.list

import com.nabadi.groundwork.feature.fieldnotes.TestFieldNote.fieldNote
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FieldNotesListUiStateTest {

    @Test
    fun `isSearching is false when search query is blank`() {
        val state = FieldNotesListUiState(searchQuery = "   ")

        assertFalse(state.isSearching)
    }

    @Test
    fun `isSearching is true when search query is not blank`() {
        val state = FieldNotesListUiState(searchQuery = "client")

        assertTrue(state.isSearching)
    }

    @Test
    fun `isFiltering is false by default`() {
        val state = FieldNotesListUiState()

        assertFalse(state.isFiltering)
    }

    @Test
    fun `isFiltering is true when status filter is selected`() {
        val state = FieldNotesListUiState(selectedStatus = FieldNoteStatus.ACTIVE)

        assertTrue(state.isFiltering)
    }

    @Test
    fun `hasActiveCriteria is false by default`() {
        val state = FieldNotesListUiState()

        assertFalse(state.hasActiveCriteria)
    }

    @Test
    fun `hasActiveCriteria is true when searching`() {
        val state = FieldNotesListUiState(searchQuery = "client")

        assertTrue(state.hasActiveCriteria)
    }

    @Test
    fun `hasActiveCriteria is true when filtering by status`() {
        val state = FieldNotesListUiState(selectedStatus = FieldNoteStatus.ACTIVE)

        assertTrue(state.hasActiveCriteria)
    }

    @Test
    fun `isError is false by default`() {
        val state = FieldNotesListUiState()

        assertFalse(state.isError)
    }

    @Test
    fun `isError is true when error message exists`() {
        val state = FieldNotesListUiState(errorMessage = "Unable to load field notes.")

        assertTrue(state.isError)
    }

    @Test
    fun `isEmpty is false while loading`() {
        val state = FieldNotesListUiState(
            isLoading = true,
            fieldNoteItems = emptyList(),
        )

        assertFalse(state.shouldShowEmptyState)
    }

    @Test
    fun `isEmpty is false when error message exists`() {
        val state = FieldNotesListUiState(
            isLoading = false,
            fieldNoteItems = emptyList(),
            errorMessage = "Unable to load field notes.",
        )

        assertFalse(state.shouldShowEmptyState)
    }

    @Test
    fun `isEmpty is false when searching`() {
        val state = FieldNotesListUiState(
            isLoading = false,
            searchQuery = "client",
            fieldNoteItems = emptyList(),
        )

        assertFalse(state.shouldShowEmptyState)
    }

    @Test
    fun `isEmpty is false when filtering by status`() {
        val state = FieldNotesListUiState(
            isLoading = false,
            selectedStatus = FieldNoteStatus.ACTIVE,
            fieldNoteItems = emptyList(),
        )

        assertFalse(state.shouldShowEmptyState)
    }

    @Test
    fun `isEmpty is false when field notes exist`() {
        val state = FieldNotesListUiState(
            isLoading = false,
            fieldNoteItems = listOf(
                FieldNoteListItemUiState(
                    note = fieldNote(id = "1"),
                    siteName = "Site 1"
                )
            ),
        )

        assertFalse(state.shouldShowEmptyState)
    }

    @Test
    fun `isEmpty is true when not loading no error no active criteria and no field notes`() {
        val state = FieldNotesListUiState(
            isLoading = false,
            fieldNoteItems = emptyList(),
        )

        assertTrue(state.shouldShowEmptyState)
    }
}