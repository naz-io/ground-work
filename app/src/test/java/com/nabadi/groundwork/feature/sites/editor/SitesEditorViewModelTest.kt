package com.nabadi.groundwork.feature.sites.editor

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.nabadi.groundwork.MainDispatcherRule
import com.nabadi.groundwork.feature.sites.TestSite
import com.nabadi.groundwork.data.repository.FakeSitesRepository
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.navigation.GroundWorkRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SitesEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init loads existing site when site id is provided`() = runTest {
        val existingSite = TestSite.site(
            id = "site-001",
            name = "Existing site",
            description = "Existing description",
            location = "Existing location",
            priority = SitePriority.HIGH,
            status = SiteStatus.ACTIVE,
        )
        val repository = FakeSitesRepository().apply {
            setSites(listOf(existingSite))
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(GroundWorkRoute.SITE_ID_ARG to existingSite.id.value),
            ),
        )

        viewModel.uiState.test {
            val loadedState = skipItemsUntil { state ->
                !state.isLoading &&
                        state.name == existingSite.name &&
                        state.description == existingSite.description &&
                        state.location == existingSite.location &&
                        state.priority == existingSite.priority &&
                        state.status == existingSite.status
            }

            assertEquals(existingSite.name, loadedState.name)
            assertEquals(existingSite.description, loadedState.description)
            assertEquals(existingSite.location, loadedState.location)
            assertEquals(existingSite.priority, loadedState.priority)
            assertEquals(existingSite.status, loadedState.status)
            assertTrue(loadedState.isEditing)
            assertFalse(loadedState.isLoading)
            assertNull(loadedState.errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init shows error when existing site is not found`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSitesRepository(),
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(GroundWorkRoute.SITE_ID_ARG to "missing-id"),
            ),
        )

        viewModel.uiState.test {
            val errorState = skipItemsUntil { state ->
                !state.isLoading && state.errorMessage != null
            }

            assertEquals("Site not found.", errorState.errorMessage)
            assertFalse(errorState.isEditing)
            assertFalse(errorState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init shows error when repository load fails`() = runTest {
        val repository = FakeSitesRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(GroundWorkRoute.SITE_ID_ARG to "site-001"),
            ),
        )

        viewModel.uiState.test {
            val errorState = skipItemsUntil { state ->
                !state.isLoading && state.errorMessage != null
            }

            assertEquals("Unable to load site.", errorState.errorMessage)
            assertFalse(errorState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNameChange updates name`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSitesRepository(),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onNameChange("North Warehouse")

        assertEquals("North Warehouse", viewModel.uiState.value.name)
    }

    @Test
    fun `onLocationChange updates location`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSitesRepository(),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onLocationChange("Sector A")

        assertEquals("Sector A", viewModel.uiState.value.location)
    }

    @Test
    fun `onDescriptionChange updates description`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSitesRepository(),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onDescriptionChange("Recurring loading-bay access issue.")

        assertEquals(
            "Recurring loading-bay access issue.",
            viewModel.uiState.value.description
        )
    }

    @Test
    fun `onPriorityChange updates priority`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSitesRepository(),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onPriorityChange(SitePriority.URGENT)

        assertEquals(SitePriority.URGENT, viewModel.uiState.value.priority)
    }

    @Test
    fun `onStatusChange updates status`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSitesRepository(),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onStatusChange(SiteStatus.ARCHIVED)

        assertEquals(SiteStatus.ARCHIVED, viewModel.uiState.value.status)
    }

    @Test
    fun `saveSite saves new site`() = runTest {
        val repository = FakeSitesRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )
        var onSavedCalled = false

        viewModel.onNameChange("North Warehouse")
        viewModel.onLocationChange("Sector A")
        viewModel.onDescriptionChange("Main storage facility.")
        viewModel.onPriorityChange(SitePriority.HIGH)
        viewModel.onStatusChange(SiteStatus.ACTIVE)
        viewModel.saveSite(onSaved = { onSavedCalled = true })

        val savedSite = repository.observeSites().first().single()
        assertEquals("North Warehouse", savedSite.name)
        assertEquals("Sector A", savedSite.location)
        assertEquals("Main storage facility.", savedSite.description)
        assertEquals(SitePriority.HIGH, savedSite.priority)
        assertEquals(SiteStatus.ACTIVE, savedSite.status)
        assertTrue(savedSite.id.value.isNotBlank())
        assertTrue(savedSite.createdAt > 0L)
        assertTrue(savedSite.updatedAt > 0L)
        assertTrue(onSavedCalled)
        assertEquals(SiteEditorUiState(), viewModel.uiState.value)
    }

    @Test
    fun `saveSite does not save when name is blank`() = runTest {
        val repository = FakeSitesRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )
        var onSavedCalled = false

        viewModel.onLocationChange("Sector A")
        viewModel.onDescriptionChange("Description without a site name.")
        viewModel.saveSite(onSaved = { onSavedCalled = true })

        assertTrue(repository.observeSites().first().isEmpty())
        assertFalse(onSavedCalled)
    }

    @Test
    fun `saveSite updates existing site and preserves metadata`() = runTest {
        val existingSite = TestSite.site(
            id = "site-001",
            name = "Original site",
            description = "Original description",
            location = "Original location",
            priority = SitePriority.NORMAL,
            status = SiteStatus.ACTIVE,
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )
        val repository = FakeSitesRepository().apply {
            setSites(listOf(existingSite))
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(GroundWorkRoute.SITE_ID_ARG to existingSite.id.value),
            ),
        )
        var onSavedCalled = false

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onNameChange("Updated site")
            viewModel.onLocationChange("Updated location")
            viewModel.onDescriptionChange("Updated description")
            viewModel.onPriorityChange(SitePriority.URGENT)
            viewModel.onStatusChange(SiteStatus.COMPLETED)
            viewModel.saveSite(onSaved = { onSavedCalled = true })

            val savedSite = repository.observeSites().first().single()
            assertEquals(existingSite.id, savedSite.id)
            assertEquals("Updated site", savedSite.name)
            assertEquals("Updated location", savedSite.location)
            assertEquals("Updated description", savedSite.description)
            assertEquals(SitePriority.URGENT, savedSite.priority)
            assertEquals(SiteStatus.COMPLETED, savedSite.status)
            assertEquals(existingSite.createdAt, savedSite.createdAt)
            assertTrue(savedSite.updatedAt >= existingSite.updatedAt)
            assertTrue(onSavedCalled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteSite deletes existing site`() = runTest {
        val existingSite = TestSite.site(id = "site-001")
        val repository = FakeSitesRepository().apply {
            setSites(listOf(existingSite))
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(GroundWorkRoute.SITE_ID_ARG to existingSite.id.value),
            ),
        )
        var onDeletedCalled = false

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.deleteSite(onDeleted = { onDeletedCalled = true })

            assertTrue(repository.observeSites().first().isEmpty())
            assertTrue(onDeletedCalled)
            assertEquals(SiteEditorUiState(), viewModel.uiState.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteSite does nothing when site is not editing existing site`() = runTest {
        val repository = FakeSitesRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )
        var onDeletedCalled = false

        viewModel.deleteSite(onDeleted = { onDeletedCalled = true })

        assertFalse(onDeletedCalled)
        assertTrue(repository.observeSites().first().isEmpty())
        assertEquals(SiteEditorUiState(), viewModel.uiState.value)
    }

    @Test
    fun `discardDraft clears state and does not save site`() = runTest {
        val repository = FakeSitesRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )
        var onDiscardedCalled = false

        viewModel.onNameChange("Draft site")
        viewModel.onLocationChange("Draft location")
        viewModel.onDescriptionChange("Draft description")
        viewModel.discardSite(onDiscarded = { onDiscardedCalled = true })

        assertEquals(SiteEditorUiState(), viewModel.uiState.value)
        assertTrue(onDiscardedCalled)
        assertTrue(repository.observeSites().first().isEmpty())
    }

    @Test
    fun `saveSite shows error when repository save fails`() = runTest {
        val repository = FakeSitesRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )
        var onSavedCalled = false

        viewModel.onNameChange("North Warehouse")
        viewModel.saveSite(onSaved = { onSavedCalled = true })

        val state = viewModel.uiState.value
        assertFalse(state.isSaving)
        assertEquals("Unable to save site.", state.errorMessage)
        assertFalse(onSavedCalled)
    }

    @Test
    fun `deleteSite shows error when repository delete fails`() = runTest {
        val existingSite = TestSite.site(id = "site-001")
        val repository = FakeSitesRepository().apply {
            setSites(listOf(existingSite))
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(GroundWorkRoute.SITE_ID_ARG to existingSite.id.value),
            ),
        )
        var onDeletedCalled = false

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            repository.setShouldThrowError(true)
            viewModel.deleteSite(onDeleted = { onDeletedCalled = true })

            val state = viewModel.uiState.value
            assertFalse(state.isDeleting)
            assertEquals("Unable to delete site.", state.errorMessage)
            assertFalse(onDeletedCalled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNameChange clears error message`() = runTest {
        val repository = FakeSitesRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onNameChange("North Warehouse")
        viewModel.saveSite(onSaved = {})
        viewModel.onNameChange("Updated site")

        assertEquals("Updated site", viewModel.uiState.value.name)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    private suspend fun ReceiveTurbine<SiteEditorUiState>.skipItemsUntilLoaded(): SiteEditorUiState =
        skipItemsUntil { state -> !state.isLoading }

    private suspend fun ReceiveTurbine<SiteEditorUiState>.skipItemsUntil(
        predicate: (SiteEditorUiState) -> Boolean,
    ): SiteEditorUiState {
        var item = awaitItem()
        while (!predicate(item)) {
            item = awaitItem()
        }
        return item
    }
}