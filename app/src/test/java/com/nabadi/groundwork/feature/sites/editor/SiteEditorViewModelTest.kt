package com.nabadi.groundwork.feature.sites.editor

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.nabadi.groundwork.MainDispatcherRule
import com.nabadi.groundwork.domain.IdGenerator
import com.nabadi.groundwork.feature.sites.TestSite
import com.nabadi.groundwork.data.repository.FakeSiteRepository
import com.nabadi.groundwork.domain.model.SiteId
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
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class SitesEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = Clock.fixed(Instant.ofEpochMilli(NOW), ZoneOffset.UTC)
    private val idGenerator = IdGenerator { GENERATED_ID }

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
        val repository = FakeSiteRepository().apply {
            setSites(listOf(existingSite))
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(siteId = existingSite.id.value),
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
            assertNull(loadedState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init shows error when existing site is not found`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(siteId = "missing-id"),
        )

        viewModel.uiState.test {
            val errorState = skipItemsUntil { state ->
                !state.isLoading && state.error != null
            }

            assertEquals(SiteEditorError.NOT_FOUND, errorState.error)
            assertFalse(errorState.isEditing)
            assertFalse(errorState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init shows error when repository load fails`() = runTest {
        val repository = FakeSiteRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(siteId = "site-001"),
        )

        viewModel.uiState.test {
            val errorState = skipItemsUntil { state ->
                !state.isLoading && state.error != null
            }

            assertEquals(SiteEditorError.LOAD_FAILED, errorState.error)
            assertFalse(errorState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNameChange updates name`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.onNameChange("North Warehouse")

        assertEquals("North Warehouse", viewModel.uiState.value.name)
    }

    @Test
    fun `clearing the name shows the required name error`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )
        assertFalse(viewModel.uiState.value.showNameRequiredError)

        viewModel.onNameChange("North Warehouse")
        assertFalse(viewModel.uiState.value.showNameRequiredError)

        viewModel.onNameChange("")
        assertTrue(viewModel.uiState.value.showNameRequiredError)
    }

    @Test
    fun `saveSite trims the site name`() = runTest {
        val repository = FakeSiteRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.onNameChange("  North Warehouse  ")
        viewModel.saveSite(onSaved = {})

        assertEquals("North Warehouse", repository.observeSites().first().single().name)
    }

    @Test
    fun `onLocationChange updates location`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.onLocationChange("Sector A")

        assertEquals("Sector A", viewModel.uiState.value.location)
    }

    @Test
    fun `onDescriptionChange updates description`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
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
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.onPriorityChange(SitePriority.HIGH)

        assertEquals(SitePriority.HIGH, viewModel.uiState.value.priority)
    }

    @Test
    fun `onStatusChange updates status`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.onStatusChange(SiteStatus.ARCHIVED)

        assertEquals(SiteStatus.ARCHIVED, viewModel.uiState.value.status)
    }

    @Test
    fun `saveSite saves new site`() = runTest {
        val repository = FakeSiteRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
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
        assertEquals(SiteId(GENERATED_ID), savedSite.id)
        assertEquals(NOW, savedSite.createdAt)
        assertEquals(NOW, savedSite.updatedAt)
        assertTrue(onSavedCalled)
        assertEquals(SiteEditorUiState(), viewModel.uiState.value)
    }

    @Test
    fun `saveSite does not save when name is blank`() = runTest {
        val repository = FakeSiteRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
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
        val repository = FakeSiteRepository().apply {
            setSites(listOf(existingSite))
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(siteId = existingSite.id.value),
        )
        var onSavedCalled = false

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onNameChange("Updated site")
            viewModel.onLocationChange("Updated location")
            viewModel.onDescriptionChange("Updated description")
            viewModel.onPriorityChange(SitePriority.HIGH)
            viewModel.onStatusChange(SiteStatus.ARCHIVED)
            viewModel.saveSite(onSaved = { onSavedCalled = true })

            val savedSite = repository.observeSites().first().single()
            assertEquals(existingSite.id, savedSite.id)
            assertEquals("Updated site", savedSite.name)
            assertEquals("Updated location", savedSite.location)
            assertEquals("Updated description", savedSite.description)
            assertEquals(SitePriority.HIGH, savedSite.priority)
            assertEquals(SiteStatus.ARCHIVED, savedSite.status)
            assertEquals(existingSite.createdAt, savedSite.createdAt)
            assertEquals(NOW, savedSite.updatedAt)
            assertTrue(onSavedCalled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteSite deletes existing site`() = runTest {
        val existingSite = TestSite.site(id = "site-001")
        val repository = FakeSiteRepository().apply {
            setSites(listOf(existingSite))
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(siteId = existingSite.id.value),
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
        val repository = FakeSiteRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )
        var onDeletedCalled = false

        viewModel.deleteSite(onDeleted = { onDeletedCalled = true })

        assertFalse(onDeletedCalled)
        assertTrue(repository.observeSites().first().isEmpty())
        assertEquals(SiteEditorUiState(), viewModel.uiState.value)
    }

    @Test
    fun `discardDraft clears state and does not save site`() = runTest {
        val repository = FakeSiteRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
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
        val repository = FakeSiteRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )
        var onSavedCalled = false

        viewModel.onNameChange("North Warehouse")
        viewModel.saveSite(onSaved = { onSavedCalled = true })

        val state = viewModel.uiState.value
        assertFalse(state.isSaving)
        assertEquals(SiteEditorError.SAVE_FAILED, state.error)
        assertFalse(onSavedCalled)
    }

    @Test
    fun `deleteSite shows error when repository delete fails`() = runTest {
        val existingSite = TestSite.site(id = "site-001")
        val repository = FakeSiteRepository().apply {
            setSites(listOf(existingSite))
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(siteId = existingSite.id.value),
        )
        var onDeletedCalled = false

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            repository.setShouldThrowError(true)
            viewModel.deleteSite(onDeleted = { onDeletedCalled = true })

            val state = viewModel.uiState.value
            assertFalse(state.isDeleting)
            assertEquals(SiteEditorError.DELETE_FAILED, state.error)
            assertFalse(onDeletedCalled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNameChange clears error message`() = runTest {
        val repository = FakeSiteRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.onNameChange("North Warehouse")
        viewModel.saveSite(onSaved = {})
        viewModel.onNameChange("Updated site")

        assertEquals("Updated site", viewModel.uiState.value.name)
        assertNull(viewModel.uiState.value.error)
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

private const val NOW = 1_750_000_000_000L
private const val GENERATED_ID = "generated-id"
