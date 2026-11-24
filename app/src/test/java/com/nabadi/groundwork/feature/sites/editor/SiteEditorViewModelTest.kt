package com.nabadi.groundwork.feature.sites.editor

import androidx.lifecycle.SavedStateHandle
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

class SiteEditorViewModelTest {

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
            savedStateHandle = SavedStateHandle(),
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
            savedStateHandle = SavedStateHandle(),
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
            savedStateHandle = SavedStateHandle(),
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
    fun `updateName updates name`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updateName("North Warehouse")

        assertEquals("North Warehouse", viewModel.uiState.value.name)
    }

    @Test
    fun `clearing the name shows the required name error`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )
        assertFalse(viewModel.uiState.value.showNameRequiredError)

        viewModel.updateName("North Warehouse")
        assertFalse(viewModel.uiState.value.showNameRequiredError)

        viewModel.updateName("")
        assertTrue(viewModel.uiState.value.showNameRequiredError)
    }

    @Test
    fun `saveSite trims the site name location and description`() = runTest {
        val repository = FakeSiteRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updateName("  North Warehouse  ")
        viewModel.updateLocation(" Sector A ")
        viewModel.updateDescription("Main storage facility.\n")
        viewModel.saveSite()

        val savedSite = repository.getSitesStream().first().single()
        assertEquals("North Warehouse", savedSite.name)
        assertEquals("Sector A", savedSite.location)
        assertEquals("Main storage facility.", savedSite.description)
    }

    @Test
    fun `updateLocation updates location`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updateLocation("Sector A")

        assertEquals("Sector A", viewModel.uiState.value.location)
    }

    @Test
    fun `updateDescription updates description`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updateDescription("Recurring loading-bay access issue.")

        assertEquals(
            "Recurring loading-bay access issue.",
            viewModel.uiState.value.description
        )
    }

    @Test
    fun `updatePriority updates priority`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updatePriority(SitePriority.HIGH)

        assertEquals(SitePriority.HIGH, viewModel.uiState.value.priority)
    }

    @Test
    fun `updateStatus updates status`() = runTest {
        val viewModel = SiteEditorViewModel(
            siteRepository = FakeSiteRepository(),
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updateStatus(SiteStatus.ARCHIVED)

        assertEquals(SiteStatus.ARCHIVED, viewModel.uiState.value.status)
    }

    @Test
    fun `saveSite saves new site`() = runTest {
        val repository = FakeSiteRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updateName("North Warehouse")
        viewModel.updateLocation("Sector A")
        viewModel.updateDescription("Main storage facility.")
        viewModel.updatePriority(SitePriority.HIGH)
        viewModel.updateStatus(SiteStatus.ACTIVE)
        viewModel.saveSite()

        val savedSite = repository.getSitesStream().first().single()
        assertEquals("North Warehouse", savedSite.name)
        assertEquals("Sector A", savedSite.location)
        assertEquals("Main storage facility.", savedSite.description)
        assertEquals(SitePriority.HIGH, savedSite.priority)
        assertEquals(SiteStatus.ACTIVE, savedSite.status)
        assertEquals(SiteId(GENERATED_ID), savedSite.id)
        assertEquals(NOW, savedSite.createdAt)
        assertEquals(NOW, savedSite.updatedAt)
        assertEquals(SiteEditorCompletion.SAVED, viewModel.uiState.value.completion)
    }

    @Test
    fun `saveSite does not save when name is blank`() = runTest {
        val repository = FakeSiteRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updateLocation("Sector A")
        viewModel.updateDescription("Description without a site name.")
        viewModel.saveSite()

        assertTrue(repository.getSitesStream().first().isEmpty())
        assertNull(viewModel.uiState.value.completion)
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
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(siteId = existingSite.id.value),
        )

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.updateName("Updated site")
            viewModel.updateLocation("Updated location")
            viewModel.updateDescription("Updated description")
            viewModel.updatePriority(SitePriority.HIGH)
            viewModel.updateStatus(SiteStatus.ARCHIVED)
            viewModel.saveSite()

            val savedSite = repository.getSitesStream().first().single()
            assertEquals(existingSite.id, savedSite.id)
            assertEquals("Updated site", savedSite.name)
            assertEquals("Updated location", savedSite.location)
            assertEquals("Updated description", savedSite.description)
            assertEquals(SitePriority.HIGH, savedSite.priority)
            assertEquals(SiteStatus.ARCHIVED, savedSite.status)
            assertEquals(existingSite.createdAt, savedSite.createdAt)
            assertEquals(NOW, savedSite.updatedAt)
            assertEquals(SiteEditorCompletion.SAVED, viewModel.uiState.value.completion)
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
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(siteId = existingSite.id.value),
        )

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.deleteSite()

            assertTrue(repository.getSitesStream().first().isEmpty())
            assertEquals(SiteEditorCompletion.DELETED, viewModel.uiState.value.completion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteSite does nothing when site is not editing existing site`() = runTest {
        val repository = FakeSiteRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.deleteSite()

        assertNull(viewModel.uiState.value.completion)
        assertTrue(repository.getSitesStream().first().isEmpty())
        assertEquals(SiteEditorUiState(), viewModel.uiState.value)
    }

    @Test
    fun `discardSite finishes without saving the site`() = runTest {
        val repository = FakeSiteRepository()
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )
        viewModel.updateName("Draft site")
        viewModel.updateLocation("Draft location")
        viewModel.updateDescription("Draft description")
        viewModel.discardSite()

        assertEquals(SiteEditorCompletion.DISCARDED, viewModel.uiState.value.completion)
        assertTrue(repository.getSitesStream().first().isEmpty())
    }

    @Test
    fun `saveSite shows error when repository save fails`() = runTest {
        val repository = FakeSiteRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updateName("North Warehouse")
        viewModel.saveSite()

        val state = viewModel.uiState.value
        assertFalse(state.isSaving)
        assertEquals(SiteEditorError.SAVE_FAILED, state.error)
        assertNull(viewModel.uiState.value.completion)
    }

    @Test
    fun `deleteSite shows error when repository delete fails`() = runTest {
        val existingSite = TestSite.site(id = "site-001")
        val repository = FakeSiteRepository().apply {
            setSites(listOf(existingSite))
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(siteId = existingSite.id.value),
        )

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            repository.setShouldThrowError(true)
            viewModel.deleteSite()

            val state = viewModel.uiState.value
            assertFalse(state.isDeleting)
            assertEquals(SiteEditorError.DELETE_FAILED, state.error)
            assertNull(viewModel.uiState.value.completion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a closing editor ignores further actions`() = runTest {
        val repository = FakeSiteRepository()
        // Fresh IDs, so a second save would show up as a second site.
        var nextId = 0
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = IdGenerator { "site-${nextId++}" },
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updateName("North Warehouse")
        viewModel.saveSite()
        viewModel.discardSite()
        viewModel.saveSite()

        assertEquals(SiteEditorCompletion.SAVED, viewModel.uiState.value.completion)
        assertEquals(1, repository.getSitesStream().first().size)
    }

    @Test
    fun `unsaved input survives process death`() = runTest {
        val savedStateHandle = SavedStateHandle()
        val route = GroundWorkRoute.SiteEditor()
        val viewModel = createViewModel(route = route, savedStateHandle = savedStateHandle)

        viewModel.updateName("North Warehouse")
        viewModel.updateDescription("Main storage facility.")
        viewModel.updateLocation("Sector A")
        viewModel.updateStatus(SiteStatus.ARCHIVED)
        viewModel.updatePriority(SitePriority.HIGH)

        // A new ViewModel with the same saved state stands in for the one recreated after
        // process death.
        val restored = createViewModel(route = route, savedStateHandle = savedStateHandle)

        val state = restored.uiState.value
        assertEquals("North Warehouse", state.name)
        assertEquals("Main storage facility.", state.description)
        assertEquals("Sector A", state.location)
        assertEquals(SiteStatus.ARCHIVED, state.status)
        assertEquals(SitePriority.HIGH, state.priority)
        assertTrue(state.isNameEdited)
    }

    @Test
    fun `a restored draft is kept over the saved site when editing`() = runTest {
        val existingSite = TestSite.site(
            id = "site-001",
            name = "Original site",
            location = "Original location",
            createdAt = 1_734_220_800_000L,
        )
        val repository = FakeSiteRepository().apply {
            setSites(listOf(existingSite))
        }
        val savedStateHandle = SavedStateHandle()
        val route = GroundWorkRoute.SiteEditor(siteId = existingSite.id.value)
        val viewModel = createViewModel(
            route = route,
            savedStateHandle = savedStateHandle,
            siteRepository = repository,
        )
        viewModel.uiState.test {
            skipItemsUntilLoaded()
            viewModel.updateName("Edited site")
            cancelAndIgnoreRemainingEvents()
        }

        val restored = createViewModel(
            route = route,
            savedStateHandle = savedStateHandle,
            siteRepository = repository,
        )

        restored.uiState.test {
            val loadedState = skipItemsUntilLoaded()
            assertEquals("Edited site", loadedState.name)
            assertEquals("Original location", loadedState.location)
            assertTrue(loadedState.isEditing)

            restored.saveSite()

            val savedSite = repository.getSitesStream().first().single()
            assertEquals(existingSite.id, savedSite.id)
            assertEquals("Edited site", savedSite.name)
            assertEquals(existingSite.createdAt, savedSite.createdAt)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateName clears error message`() = runTest {
        val repository = FakeSiteRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = SiteEditorViewModel(
            siteRepository = repository,
            savedStateHandle = SavedStateHandle(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.SiteEditor(),
        )

        viewModel.updateName("North Warehouse")
        viewModel.saveSite()
        viewModel.updateName("Updated site")

        assertEquals("Updated site", viewModel.uiState.value.name)
        assertNull(viewModel.uiState.value.error)
    }

    private fun createViewModel(
        route: GroundWorkRoute.SiteEditor,
        savedStateHandle: SavedStateHandle,
        siteRepository: FakeSiteRepository = FakeSiteRepository(),
    ) = SiteEditorViewModel(
        route = route,
        savedStateHandle = savedStateHandle,
        siteRepository = siteRepository,
        clock = clock,
        idGenerator = idGenerator,
    )

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
