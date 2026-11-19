package com.nabadi.groundwork.feature.sites.detail

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.nabadi.groundwork.MainDispatcherRule
import com.nabadi.groundwork.data.repository.FakeFieldNoteRepository
import com.nabadi.groundwork.data.repository.FakeSiteRepository
import com.nabadi.groundwork.feature.sites.TestSite
import com.nabadi.groundwork.feature.fieldnotes.TestFieldNote
import com.nabadi.groundwork.navigation.GroundWorkRoute
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test

class SiteDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init loads site details when site id is provided`() = runTest {
        val existingSite = TestSite.site(
            id = "site-001",
            name = "North Warehouse",
            description = "Main storage facility.",
            location = "Sector A",
        )
        val viewModel = createViewModel(
            siteRepository = FakeSiteRepository().apply {
                setSites(listOf(existingSite))
            },
            siteId = existingSite.id.value,
        )

        viewModel.uiState.test {
            val loadedState = skipItemsUntil { state ->
                !state.isLoading && state.name == existingSite.name
            }

            assertEquals(existingSite.name, loadedState.name)
            assertEquals(existingSite.description, loadedState.description)
            assertEquals(existingSite.location, loadedState.location)
            assertEquals(existingSite.priority, loadedState.priority)
            assertEquals(existingSite.status, loadedState.status)
            assertFalse(loadedState.isLoading)
            assertNull(loadedState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init observes only field notes assigned to the site`() = runTest {
        val matchingNote = TestFieldNote.fieldNote(id = "matching", siteId = "site-001")
        val otherNote = TestFieldNote.fieldNote(id = "other", siteId = "site-002")
        val fieldNoteRepository = FakeFieldNoteRepository().apply {
            setFieldNotes(listOf(matchingNote, otherNote))
        }
        val viewModel = createViewModel(
            siteRepository = FakeSiteRepository().apply {
                setSites(listOf(TestSite.site(id = "site-001")))
            },
            fieldNoteRepository = fieldNoteRepository,
            siteId = "site-001",
        )

        viewModel.uiState.test {
            val state = skipItemsUntil { it.fieldNoteItems.isNotEmpty() }

            assertEquals(listOf(matchingNote), state.fieldNoteItems.map { it.note })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `field note rows show the site name`() = runTest {
        val viewModel = createViewModel(
            siteRepository = FakeSiteRepository().apply {
                setSites(listOf(TestSite.site(id = "site-001", name = "North Warehouse")))
            },
            fieldNoteRepository = FakeFieldNoteRepository().apply {
                setFieldNotes(listOf(TestFieldNote.fieldNote(id = "note-001", siteId = "site-001")))
            },
            siteId = "site-001",
        )

        viewModel.uiState.test {
            val state = skipItemsUntil { state ->
                state.fieldNoteItems.isNotEmpty() && state.name == "North Warehouse"
            }

            assertEquals(listOf("North Warehouse"), state.fieldNoteItems.map { it.siteName })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `renaming the site updates the site name on its field note rows`() = runTest {
        val originalSite = TestSite.site(id = "site-001", name = "Original site")
        val siteRepository = FakeSiteRepository().apply {
            setSites(listOf(originalSite))
        }
        val viewModel = createViewModel(
            siteRepository = siteRepository,
            fieldNoteRepository = FakeFieldNoteRepository().apply {
                setFieldNotes(listOf(TestFieldNote.fieldNote(id = "note-001", siteId = "site-001")))
            },
            siteId = "site-001",
        )

        viewModel.uiState.test {
            skipItemsUntil { state ->
                state.fieldNoteItems.isNotEmpty() && state.name == originalSite.name
            }

            siteRepository.setSites(listOf(originalSite.copy(name = "Renamed site")))

            val renamedState = skipItemsUntil { it.name == "Renamed site" }
            assertEquals(listOf("Renamed site"), renamedState.fieldNoteItems.map { it.siteName })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `site edits that keep the name reuse the same field note rows`() = runTest {
        val originalSite = TestSite.site(id = "site-001", location = "Original location")
        val siteRepository = FakeSiteRepository().apply {
            setSites(listOf(originalSite))
        }
        val viewModel = createViewModel(
            siteRepository = siteRepository,
            fieldNoteRepository = FakeFieldNoteRepository().apply {
                setFieldNotes(listOf(TestFieldNote.fieldNote(id = "note-001", siteId = "site-001")))
            },
            siteId = "site-001",
        )

        viewModel.uiState.test {
            val loadedState = skipItemsUntil { state ->
                state.fieldNoteItems.isNotEmpty() && state.name == originalSite.name
            }

            siteRepository.setSites(listOf(originalSite.copy(location = "Updated location")))

            val updatedState = skipItemsUntil { it.location == "Updated location" }
            assertSame(loadedState.fieldNoteItems, updatedState.fieldNoteItems)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `site details update when repository site changes`() = runTest {
        val originalSite = TestSite.site(
            id = "site-001",
            name = "Original site",
            location = "Original location",
        )
        val updatedSite = originalSite.copy(
            name = "Updated site",
            location = "Updated location",
        )
        val siteRepository = FakeSiteRepository().apply {
            setSites(listOf(originalSite))
        }
        val viewModel = createViewModel(
            siteRepository = siteRepository,
            siteId = originalSite.id.value,
        )

        viewModel.uiState.test {
            skipItemsUntil { it.name == originalSite.name }

            siteRepository.setSites(listOf(updatedSite))

            val updatedState = skipItemsUntil { it.name == updatedSite.name }
            assertEquals(updatedSite.location, updatedState.location)
            assertNull(updatedState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `site deletion changes detail state to site not found`() = runTest {
        val existingSite = TestSite.site(id = "site-001")
        val siteRepository = FakeSiteRepository().apply {
            setSites(listOf(existingSite))
        }
        val viewModel = createViewModel(
            siteRepository = siteRepository,
            siteId = existingSite.id.value,
        )

        viewModel.uiState.test {
            skipItemsUntil { it.siteId == existingSite.id }

            siteRepository.setSites(emptyList())

            val deletedState = skipItemsUntil { it.error != null }
            assertEquals(SiteDetailError.NOT_FOUND, deletedState.error)
            assertNull(deletedState.siteId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `field note load error does not replace site details`() = runTest {
        val existingSite = TestSite.site(id = "site-001")
        val viewModel = createViewModel(
            siteRepository = FakeSiteRepository().apply {
                setSites(listOf(existingSite))
            },
            fieldNoteRepository = FakeFieldNoteRepository().apply {
                setShouldThrowError(true)
            },
            siteId = existingSite.id.value,
        )

        viewModel.uiState.test {
            val state = skipItemsUntil { it.notesLoadFailed && !it.isLoading }

            assertEquals(existingSite.name, state.name)
            assertNull(state.error)
            assertTrue(state.notesLoadFailed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init shows site not found when repository has no matching site`() = runTest {
        val viewModel = createViewModel(siteId = "missing-site")

        viewModel.uiState.test {
            val errorState = skipItemsUntil { state ->
                !state.isLoading && state.error != null
            }

            assertEquals(SiteDetailError.NOT_FOUND, errorState.error)
            assertFalse(errorState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init shows load error when site repository fails`() = runTest {
        val viewModel = createViewModel(
            siteRepository = FakeSiteRepository().apply {
                setShouldThrowError(true)
            },
            siteId = "site-001",
        )

        viewModel.uiState.test {
            val errorState = skipItemsUntil { state ->
                !state.isLoading && state.error != null
            }

            assertEquals(SiteDetailError.LOAD_FAILED, errorState.error)
            assertFalse(errorState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel(
        siteRepository: FakeSiteRepository = FakeSiteRepository(),
        fieldNoteRepository: FakeFieldNoteRepository = FakeFieldNoteRepository(),
        siteId: String,
    ): SiteDetailViewModel = SiteDetailViewModel(
        siteRepository = siteRepository,
        fieldNoteRepository = fieldNoteRepository,
        route = GroundWorkRoute.SiteDetail(siteId = siteId),
    )

    private suspend fun ReceiveTurbine<SiteDetailUiState>.skipItemsUntil(
        predicate: (SiteDetailUiState) -> Boolean,
    ): SiteDetailUiState {
        while (true) {
            val state = awaitItem()
            if (predicate(state)) return state
        }
    }
}
