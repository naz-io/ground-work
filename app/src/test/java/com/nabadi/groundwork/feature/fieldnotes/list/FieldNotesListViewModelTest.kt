package com.nabadi.groundwork.feature.fieldnotes.list

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.nabadi.groundwork.MainDispatcherRule
import com.nabadi.groundwork.feature.fieldnotes.TestFieldNote.fieldNote
import com.nabadi.groundwork.data.repository.FakeFieldNoteRepository
import com.nabadi.groundwork.data.repository.FakeSiteRepository
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.feature.sites.TestSite.site
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

    private val fieldNoteRepository = FakeFieldNoteRepository()
    private val siteRepository = FakeSiteRepository()
    private lateinit var viewModel: FieldNotesListViewModel

    @Before
    fun setup() {
        viewModel = FieldNotesListViewModel(fieldNoteRepository, siteRepository)
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
        fieldNoteRepository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            val state = skipItemsUntilLoaded()
            assertFalse(state.isLoading)
            assertEquals(fieldNotes, state.fieldNoteItems.map { it.note })
        }
    }

    @Test
    fun `uiState maps associated site names from repository`() = runTest {
        siteRepository.setSites(
            listOf(
                site(
                    id = "site-generator-room",
                    name = "Generator Room",
                ),
                site(
                    id = "site-north-gate",
                    name = "North Gate",
                ),
            ),
        )

        fieldNoteRepository.setFieldNotes(
            listOf(
                fieldNote(
                    id = "field-note-1",
                    title = "Generator note",
                    siteId = "site-generator-room",
                    body = "Body 1",
                    status = FieldNoteStatus.ACTIVE,
                ),
                fieldNote(
                    id = "field-note-2",
                    title = "Unassigned note",
                    siteId = null,
                    body = "Body 2",
                    status = FieldNoteStatus.ACTIVE,
                ),
            )
        )

        viewModel.uiState.test {
            val state = skipItemsUntilLoaded()

            assertEquals(2, state.fieldNoteItems.size)
            assertEquals("Generator Room", state.fieldNoteItems[0].siteName)
            assertNull(state.fieldNoteItems[1].siteName)
        }
    }

    @Test
    fun `onSearchQueryChange updates searchQuery and filters field note items`() = runTest {
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
        fieldNoteRepository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "App")

            val filteredState = awaitItem()
            assertEquals("App", filteredState.searchQuery)
            assertEquals(1, filteredState.fieldNoteItems.size)
            assertEquals("Apple", filteredState.fieldNoteItems[0].note.title)
        }
    }

    @Test
    fun `onStatusFilterChange updates selectedStatus and filters field note items`() = runTest {
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
        fieldNoteRepository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onStatusFilterChange(status = FieldNoteStatus.DRAFT)

            val filteredState = awaitItem()
            assertEquals(FieldNoteStatus.DRAFT, filteredState.selectedStatus)
            assertEquals(1, filteredState.fieldNoteItems.size)
            assertEquals(FieldNoteStatus.DRAFT, filteredState.fieldNoteItems[0].note.status)
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
        fieldNoteRepository.setFieldNotes(notes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "Apple")
            awaitItem()

            viewModel.onStatusFilterChange(status = FieldNoteStatus.DRAFT)
            val finalState = awaitItem()

            assertEquals(1, finalState.fieldNoteItems.size)
            assertEquals("Apple", finalState.searchQuery)
            assertEquals(FieldNoteStatus.DRAFT, finalState.selectedStatus)
            assertEquals("Apple Pie", finalState.fieldNoteItems[0].note.title)
            assertEquals(FieldNoteStatus.DRAFT, finalState.fieldNoteItems[0].note.status)
        }
    }

    @Test
    fun `search query filters notes by body`() = runTest {
        fieldNoteRepository.setFieldNotes(
            listOf(
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
        )

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "fencing")

            val finalState = awaitItem()

            assertEquals(1, finalState.fieldNoteItems.size)
            assertEquals("field-note-001", finalState.fieldNoteItems.first().note.id.value)
            assertEquals("North gate safety check", finalState.fieldNoteItems.first().note.title)
            assertEquals("fencing", finalState.searchQuery)
        }
    }

    @Test
    fun `search query filters notes by associated site name`() = runTest {
        siteRepository.setSites(
            listOf(
                site(
                    id = "site-generator-room",
                    name = "Generator Room",
                ),
                site(
                    id = "site-north-gate",
                    name = "North Gate",
                ),
            ),
        )

        fieldNoteRepository.setFieldNotes(
            listOf(
                fieldNote(
                    id = "field-note-001",
                    title = "Unrelated title",
                    siteId = "site-generator-room",
                    body = "Unrelated body",
                    status = FieldNoteStatus.ACTIVE,
                ),
                fieldNote(
                    id = "field-note-002",
                    title = "Another unrelated title",
                    siteId = "site-north-gate",
                    body = "Another unrelated body",
                    status = FieldNoteStatus.ACTIVE,
                ),
            )
        )

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "generator")

            val filteredState = awaitItem()
            assertEquals("generator", filteredState.searchQuery)
            assertEquals(1, filteredState.fieldNoteItems.size)
            assertEquals("field-note-001", filteredState.fieldNoteItems.first().note.id.value)
            assertEquals("Generator Room", filteredState.fieldNoteItems.first().siteName)
        }
    }

    @Test
    fun `missing associated site keeps siteName null`() = runTest {
        siteRepository.setSites(emptyList())
        fieldNoteRepository.setFieldNotes(
            listOf(
                fieldNote(
                    id = "field-note-001",
                    title = "Unknown site note",
                    siteId = "site-missing",
                    body = "Body 1",
                    status = FieldNoteStatus.ACTIVE,
                ),
            ),
        )

        viewModel.uiState.test {
            val state = skipItemsUntilLoaded()

            assertEquals(1, state.fieldNoteItems.size)
            assertEquals("field-note-001", state.fieldNoteItems.first().note.id.value)
            assertNull(state.fieldNoteItems.first().siteName)
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
        fieldNoteRepository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "gate")

            val filteredState = awaitItem()
            assertEquals(1, filteredState.fieldNoteItems.size)
            assertEquals("North Gate Safety Check", filteredState.fieldNoteItems.first().note.title)
        }
    }

    @Test
    fun `blank search query does not filter field note items`() = runTest {
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
        fieldNoteRepository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onSearchQueryChange(query = "   ")

            val state = awaitItem()
            assertEquals("   ", state.searchQuery)
            assertEquals(fieldNotes, state.fieldNoteItems.map { it.note })
        }
    }

    @Test
    fun `clearing status filter shows all field note items`() = runTest {
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
        fieldNoteRepository.setFieldNotes(fieldNotes)

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onStatusFilterChange(status = FieldNoteStatus.DRAFT)
            awaitItem()

            viewModel.onStatusFilterChange(status = null)

            val clearedState = awaitItem()
            assertNull(clearedState.selectedStatus)
            assertEquals(fieldNotes, clearedState.fieldNoteItems.map { it.note })
        }
    }

    @Test
    fun `uiState emits error message when repository fails`() = runTest {
        fieldNoteRepository.setShouldThrowError(true)

        val errorViewModel = FieldNotesListViewModel(fieldNoteRepository, siteRepository)

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