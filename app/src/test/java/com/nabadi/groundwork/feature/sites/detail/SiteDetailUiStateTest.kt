package com.nabadi.groundwork.feature.sites.detail

import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.feature.fieldnotes.TestFieldNote
import com.nabadi.groundwork.feature.fieldnotes.list.FieldNoteListItemUiState
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
        val state = SiteDetailUiState(error = SiteDetailError.LOAD_FAILED)

        assertTrue(state.isError)
    }

    @Test
    fun `field note error does not make the whole site state an error`() {
        val state = SiteDetailUiState(notesLoadFailed = true)

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
            error = SiteDetailError.LOAD_FAILED,
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
            error = SiteDetailError.LOAD_FAILED,
        )

        assertFalse(state.canAddFieldNote)
    }

    @Test
    fun `hasNotes is false when there are no field note rows`() {
        val state = SiteDetailUiState(fieldNoteItems = emptyList())

        assertFalse(state.hasNotes)
    }

    @Test
    fun `hasNotes is true when field note rows exist`() {
        val state = SiteDetailUiState(
            fieldNoteItems = listOf(fieldNoteItem()),
        )

        assertTrue(state.hasNotes)
    }

    @Test
    fun `noteCount matches the number of field note rows`() {
        val state = SiteDetailUiState(
            fieldNoteItems = listOf(
                fieldNoteItem(id = "note-001"),
                fieldNoteItem(id = "note-002"),
            ),
        )

        assertEquals(2, state.noteCount)
    }

    private fun fieldNoteItem(id: String = "note-001") = FieldNoteListItemUiState(
        note = TestFieldNote.fieldNote(id = id),
        siteName = "North Warehouse",
    )
}
