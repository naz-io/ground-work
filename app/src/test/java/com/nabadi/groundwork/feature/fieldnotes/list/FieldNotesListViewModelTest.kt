package com.nabadi.groundwork.feature.fieldnotes.list

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.nabadi.groundwork.MainDispatcherRule
import com.nabadi.groundwork.feature.fieldnotes.TestFieldNote.fieldNote
import com.nabadi.groundwork.data.repository.FakeFieldNoteRepository
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FieldNotesListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeFieldNoteRepository()
    private lateinit var viewModel: FieldNotesListViewModel

    @Before
    fun setup() {
        viewModel = FieldNotesListViewModel(repository)
    }

    @Test
    fun `uiState initially emits data from repository`() = runTest {
        val fieldNotes = listOf(
            fieldNote(
                id = "1",
                title = "Title 1",
                body = "Body 1",
                status = FieldNoteStatus.ACTIVE,
            ),
            fieldNote(
                id = "2",
                title = "Title 2",
                body = "Body 2",
                status = FieldNoteStatus.ARCHIVED,
            ),
        )
        repository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            val state = skipItemsUntilLoaded()
            assertFalse(state.isLoading)
            assertEquals(fieldNotes, state.fieldNotes)
        }
    }

    @Test
    fun `onSearchQueryChange updates searchQuery and filters fieldNotes`() = runTest {
        val fieldNotes = listOf(
            fieldNote(
                id = "1",
                title = "Apple",
                body = "Body 1",
                status = FieldNoteStatus.ACTIVE,
            ),
            fieldNote(
                id = "2",
                title = "Banana",
                body = "Body 2",
                status = FieldNoteStatus.ACTIVE,
            ),
        )
        repository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "App")

            val filteredState = awaitItem()
            assertEquals("App", filteredState.searchQuery)
            assertEquals(1, filteredState.fieldNotes.size)
            assertEquals("Apple", filteredState.fieldNotes[0].title)
        }
    }

    @Test
    fun `onStatusFilterChange updates selectedStatus and filters fieldNotes`() = runTest {
        val fieldNotes = listOf(
            fieldNote(
                id = "1",
                title = "Title 1",
                body = "Body 1",
                status = FieldNoteStatus.ACTIVE,
            ),
            fieldNote(
                id = "2",
                title = "Title 2",
                body = "Body 2",
                status = FieldNoteStatus.DRAFT,
            ),
        )
        repository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onStatusFilterChange(status = FieldNoteStatus.DRAFT)

            val filteredState = awaitItem()
            assertEquals(FieldNoteStatus.DRAFT, filteredState.selectedStatus)
            assertEquals(1, filteredState.fieldNotes.size)
            assertEquals(FieldNoteStatus.DRAFT, filteredState.fieldNotes[0].status)
        }
    }

    @Test
    fun `combined search and status filter works correctly`() = runTest {
        val notes = listOf(
            fieldNote(
                id = "1",
                title = "Apple",
                body = "Body 1",
                status = FieldNoteStatus.ACTIVE,
            ),
            fieldNote(
                id = "2",
                title = "Apple Pie",
                body = "Body 2",
                status = FieldNoteStatus.DRAFT,
            ),
            fieldNote(
                id = "3",
                title = "Banana",
                body = "Body 3",
                status = FieldNoteStatus.ACTIVE,
            ),
        )
        repository.setFieldNotes(notes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "Apple")
            awaitItem()

            viewModel.onStatusFilterChange(status = FieldNoteStatus.DRAFT)
            val finalState = awaitItem()

            assertEquals(1, finalState.fieldNotes.size)
            assertEquals("Apple", finalState.searchQuery)
            assertEquals(FieldNoteStatus.DRAFT, finalState.selectedStatus)
            assertEquals("Apple Pie", finalState.fieldNotes[0].title)
            assertEquals(FieldNoteStatus.DRAFT, finalState.fieldNotes[0].status)
        }
    }

    @Test
    fun `search query filters notes by body`() = runTest {
        val fieldNotes = listOf(
            fieldNote(
                id = "field-note-001",
                title = "North gate safety check",
                body = "Loose temporary fencing reported near the north access point.",
                status = FieldNoteStatus.ACTIVE,
            ),
            fieldNote(
                id = "field-note-002",
                title = "Pump room inspection",
                body = "Pressure gauge needs follow-up.",
                status = FieldNoteStatus.ACTIVE,
            ),
        )
        repository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "fencing")

            val finalState = awaitItem()

            assertEquals(1, finalState.fieldNotes.size)
            assertEquals("field-note-001", finalState.fieldNotes.first().id.value)
            assertEquals("North gate safety check", finalState.fieldNotes.first().title)
            assertEquals("fencing", finalState.searchQuery)
        }
    }

    @Test
    fun `search query is case insensitive`() = runTest {
        val fieldNotes = listOf(
            fieldNote(
                id = "1",
                title = "North Gate Safety Check",
                body = "Body 1",
                status = FieldNoteStatus.ACTIVE,
            ),
            fieldNote(
                id = "2",
                title = "Pump room inspection",
                body = "Body 2",
                status = FieldNoteStatus.ACTIVE,
            ),
        )
        repository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "gate")

            val filteredState = awaitItem()
            assertEquals(1, filteredState.fieldNotes.size)
            assertEquals("North Gate Safety Check", filteredState.fieldNotes.first().title)
        }
    }

    @Test
    fun `blank search query does not filter fieldNotes`() = runTest {
        val fieldNotes = listOf(
            fieldNote(
                id = "1",
                title = "Apple",
                body = "Body 1",
                status = FieldNoteStatus.ACTIVE,
            ),
            fieldNote(
                id = "2",
                title = "Banana",
                body = "Body 2",
                status = FieldNoteStatus.ACTIVE,
            ),
        )
        repository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "   ")

            val state = awaitItem()
            assertEquals("   ", state.searchQuery)
            assertEquals(fieldNotes, state.fieldNotes)
        }
    }

    @Test
    fun `clearing status filter shows all fieldNotes`() = runTest {
        val fieldNotes = listOf(
            fieldNote(
                id = "1",
                title = "Active note",
                body = "Body 1",
                status = FieldNoteStatus.ACTIVE,
            ),
            fieldNote(
                id = "2",
                title = "Draft note",
                body = "Body 2",
                status = FieldNoteStatus.DRAFT,
            ),
        )
        repository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onStatusFilterChange(status = FieldNoteStatus.DRAFT)
            awaitItem()

            viewModel.onStatusFilterChange(status = null)

            val clearedState = awaitItem()
            assertNull(clearedState.selectedStatus)
            assertEquals(fieldNotes, clearedState.fieldNotes)
        }
    }

    @Test
    fun `uiState emits error message when repository fails`() = runTest {
        repository.setShouldThrowError(true)

        val errorViewModel = FieldNotesListViewModel(repository)

        errorViewModel.uiState.test {
            val state = awaitItem()
            if (state.errorMessage == null) {
                val nextState = awaitItem()
                assertNotNull(nextState.errorMessage)
                assertEquals("Unable to load field notes.", nextState.errorMessage)
            } else {
                assertNotNull(state.errorMessage)
                assertEquals("Unable to load field notes.", state.errorMessage)
            }
        }
    }

    private suspend fun ReceiveTurbine<FieldNotesListUiState>.skipItemsUntilLoaded(): FieldNotesListUiState {
        var item = awaitItem()
        while (item.isLoading && !item.isError) {
            item = awaitItem()
        }
        return item
    }
}