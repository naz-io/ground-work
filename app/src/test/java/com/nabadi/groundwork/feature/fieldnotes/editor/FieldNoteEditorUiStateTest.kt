package com.nabadi.groundwork.feature.fieldnotes.editor

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FieldNoteEditorUiStateTest {

    @Test
    fun `isBusy is false by default`() {
        val state = FieldNoteEditorUiState()

        assertFalse(state.isBusy)
    }

    @Test
    fun `isBusy is true when loading`() {
        val state = FieldNoteEditorUiState(isLoading = true)

        assertTrue(state.isBusy)
    }

    @Test
    fun `isBusy is true when saving`() {
        val state = FieldNoteEditorUiState(isSaving = true)

        assertTrue(state.isBusy)
    }

    @Test
    fun `isBusy is true when deleting`() {
        val state = FieldNoteEditorUiState(isDeleting = true)

        assertTrue(state.isBusy)
    }

    @Test
    fun `canSave is false by default`() {
        val state = FieldNoteEditorUiState()

        assertFalse(state.canSave)
    }

    @Test
    fun `canSave is false when title and body contain only whitespace`() {
        val state = FieldNoteEditorUiState(
            title = "   ",
            body = "   ",
        )

        assertFalse(state.canSave)
    }

    @Test
    fun `canSave is true when title is not blank and body is blank`() {
        val state = FieldNoteEditorUiState(
            title = "Safety issue",
            body = "",
        )

        assertTrue(state.canSave)
    }

    @Test
    fun `canSave is true when body is not blank and title is blank`() {
        val state = FieldNoteEditorUiState(
            title = "",
            body = "Check the west entrance.",
        )

        assertTrue(state.canSave)
    }

    @Test
    fun `canSave is true when title and body are not blank`() {
        val state = FieldNoteEditorUiState(
            title = "Safety issue",
            body = "Check the west entrance.",
        )

        assertTrue(state.canSave)
    }

    @Test
    fun `site options error does not block saving an unassigned field note`() {
        val state = FieldNoteEditorUiState(
            title = "Safety issue",
            siteOptionsErrorMessage = "Unable to load sites.",
        )

        assertTrue(state.canSave)
        assertFalse(state.isBusy)
    }

    @Test
    fun `canSave is false while loading`() {
        val state = FieldNoteEditorUiState(
            title = "Safety issue",
            isLoading = true,
        )

        assertFalse(state.canSave)
    }

    @Test
    fun `canSave is false while saving`() {
        val state = FieldNoteEditorUiState(
            title = "Safety issue",
            isSaving = true,
        )

        assertFalse(state.canSave)
    }

    @Test
    fun `canSave is false while deleting`() {
        val state = FieldNoteEditorUiState(
            title = "Safety issue",
            isDeleting = true,
        )

        assertFalse(state.canSave)
    }
}
