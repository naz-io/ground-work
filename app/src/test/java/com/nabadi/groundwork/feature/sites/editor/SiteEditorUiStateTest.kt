package com.nabadi.groundwork.feature.sites.editor

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SiteEditorUiStateTest {

    @Test
    fun `isBusy is false by default`() {
        val state = SiteEditorUiState()

        assertFalse(state.isBusy)
    }

    @Test
    fun `isBusy is true when loading`() {
        val state = SiteEditorUiState(isLoading = true)

        assertTrue(state.isBusy)
    }

    @Test
    fun `isBusy is true when saving`() {
        val state = SiteEditorUiState(isSaving = true)

        assertTrue(state.isBusy)
    }

    @Test
    fun `isBusy is true when deleting`() {
        val state = SiteEditorUiState(isDeleting = true)

        assertTrue(state.isBusy)
    }

    @Test
    fun `canSave is false when name is blank`() {
        val state = SiteEditorUiState(name = "")

        assertFalse(state.canSave)
    }

    @Test
    fun `canSave is false when name contains only whitespace`() {
        val state = SiteEditorUiState(name = "   ")

        assertFalse(state.canSave)
    }

    @Test
    fun `canSave is true when name is not blank and state is not busy`() {
        val state = SiteEditorUiState(name = "Client site")

        assertTrue(state.canSave)
    }

    @Test
    fun `canSave is false while loading`() {
        val state = SiteEditorUiState(
            name = "Client site",
            isLoading = true,
        )

        assertFalse(state.canSave)
    }

    @Test
    fun `canSave is false while saving`() {
        val state = SiteEditorUiState(
            name = "Client site",
            isSaving = true,
        )

        assertFalse(state.canSave)
    }

    @Test
    fun `canSave is false while deleting`() {
        val state = SiteEditorUiState(
            name = "Client site",
            isDeleting = true,
        )

        assertFalse(state.canSave)
    }
}