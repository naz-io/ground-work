package com.nabadi.groundwork.feature.sites.detail

import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.feature.fieldnotes.TestFieldNote
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SiteDetailUiStateTest {

    @Test
    fun `isBusy is false by default`() {
        val state = SiteDetailUiState()

        assertFalse(state.isBusy)
    }

    @Test
    fun `isBusy is true while loading`() {
        val state = SiteDetailUiState(isLoading = true)

        assertTrue(state.isBusy)
    }

    @Test
    fun `isError is false by default`() {
        val state = SiteDetailUiState()

        assertFalse(state.isError)
    }

    @Test
    fun `isError is true when error message exists`() {
        val state = SiteDetailUiState(errorMessage = "Unable to load site.")

        assertTrue(state.isError)
    }

    @Test
    fun `field note error does not make the whole site state an error`() {
        val state = SiteDetailUiState(notesErrorMessage = "Unable to load site notes.")

        assertFalse(state.isError)
    }

    @Test
    fun `canEdit is true when site is loaded without a fatal error`() {
        val state = SiteDetailUiState(siteId = SiteId("site-001"))

        assertTrue(state.canEdit)
    }

    @Test
    fun `canEdit is false when site is not loaded`() {
        val state = SiteDetailUiState(siteId = null)

        assertFalse(state.canEdit)
    }

    @Test
    fun `canEdit is false while loading`() {
        val state = SiteDetailUiState(
            siteId = SiteId("site-001"),
            isLoading = true,
        )

        assertFalse(state.canEdit)
    }

    @Test
    fun `canEdit is false when site has a fatal error`() {
        val state = SiteDetailUiState(
            siteId = SiteId("site-001"),
            errorMessage = "Unable to load site.",
        )

        assertFalse(state.canEdit)
    }

    @Test
    fun `canAddFieldNote is true when site is loaded without a fatal error`() {
        val state = SiteDetailUiState(siteId = SiteId("site-001"))

        assertTrue(state.canAddFieldNote)
    }

    @Test
    fun `canAddFieldNote is false when site is not loaded`() {
        val state = SiteDetailUiState(siteId = null)

        assertFalse(state.canAddFieldNote)
    }

    @Test
    fun `canAddFieldNote is false while loading`() {
        val state = SiteDetailUiState(
            siteId = SiteId("site-001"),
            isLoading = true,
        )

        assertFalse(state.canAddFieldNote)
    }

    @Test
    fun `canAddFieldNote is false when site has a fatal error`() {
        val state = SiteDetailUiState(
            siteId = SiteId("site-001"),
            errorMessage = "Unable to load site.",
        )

        assertFalse(state.canAddFieldNote)
    }

    @Test
    fun `hasNotes is false when field note ids are empty`() {
        val state = SiteDetailUiState(fieldNotes = emptyList())

        assertFalse(state.hasNotes)
    }

    @Test
    fun `hasNotes is true when field note ids exist`() {
        val state = SiteDetailUiState(
            fieldNotes = listOf(TestFieldNote.fieldNote()),
        )

        assertTrue(state.hasNotes)
    }

    @Test
    fun `noteCount matches the number of field notes`() {
        val state = SiteDetailUiState(
            fieldNotes = listOf(
                TestFieldNote.fieldNote(id = "note-001"),
                TestFieldNote.fieldNote(id = "note-002"),
            ),
        )

        assertEquals(2, state.noteCount)
    }
}
