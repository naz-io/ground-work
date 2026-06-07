package com.nabadi.groundwork.feature.fieldnotes

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.nabadi.groundwork.MainDispatcherRule
import com.nabadi.groundwork.data.repository.FakeFieldNoteRepository
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FieldNotesListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: FieldNotesListViewModel
    private val repository = FakeFieldNoteRepository()

    @Before
    fun setup() {
        viewModel = FieldNotesListViewModel(repository)
    }

    @Test
    fun `uiState initially emits data from repository`() = runTest {
        val fieldNotes = listOf(
            createFieldNote("1", "Title 1", "Body 1", FieldNoteStatus.ACTIVE),
            createFieldNote("2", "Title 2", "Body 2", FieldNoteStatus.ARCHIVED),
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
            createFieldNote("1", "Apple", "Body 1", FieldNoteStatus.ACTIVE),
            createFieldNote("2", "Banana", "Body 2", FieldNoteStatus.ACTIVE),
        )
        repository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange("App")

            val filteredState = awaitItem()
            assertEquals("App", filteredState.searchQuery)
            assertEquals(1, filteredState.fieldNotes.size)
            assertEquals("Apple", filteredState.fieldNotes[0].title)
        }
    }

    @Test
    fun `onStatusFilterChange updates selectedStatus and filters fieldNotes`() = runTest {
        val fieldNotes = listOf(
            createFieldNote("1", "Title 1", "Body 1", FieldNoteStatus.ACTIVE),
            createFieldNote("2", "Title 2", "Body 2", FieldNoteStatus.DRAFT),
        )
        repository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onStatusFilterChange(FieldNoteStatus.DRAFT)

            val filteredState = awaitItem()
            assertEquals(FieldNoteStatus.DRAFT, filteredState.selectedStatus)
            assertEquals(1, filteredState.fieldNotes.size)
            assertEquals(FieldNoteStatus.DRAFT, filteredState.fieldNotes[0].status)
        }
    }

    @Test
    fun `combined search and status filter works correctly`() = runTest {
        val notes = listOf(
            createFieldNote("1", "Apple", "Body 1", FieldNoteStatus.ACTIVE),
            createFieldNote("2", "Apple Pie", "Body 2", FieldNoteStatus.DRAFT),
            createFieldNote("3", "Banana", "Body 3", FieldNoteStatus.ACTIVE),
        )
        repository.setFieldNotes(notes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange("Apple")
            awaitItem()

            viewModel.onStatusFilterChange(FieldNoteStatus.DRAFT)
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
            createFieldNote(
                id = "field-note-001",
                title = "North gate safety check",
                body = "Loose temporary fencing reported near the north access point.",
                status = FieldNoteStatus.ACTIVE,
            ),
            createFieldNote(
                id = "field-note-002",
                title = "Pump room inspection",
                body = "Pressure gauge needs follow-up.",
                status = FieldNoteStatus.ACTIVE,
            ),
        )
        repository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange("fencing")

            val finalState = awaitItem()

            assertEquals(1, finalState.fieldNotes.size)
            assertEquals("field-note-001", finalState.fieldNotes.first().id.value)
            assertEquals("North gate safety check", finalState.fieldNotes.first().title)
            assertEquals("fencing", finalState.searchQuery)
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

    private fun createFieldNote(
        id: String,
        title: String,
        body: String,
        status: FieldNoteStatus,
    ) = FieldNote(
        id = FieldNoteId(id),
        title = title,
        body = body,
        status = status,
        createdAt = 0L,
        updatedAt = 0L,
    )
}
