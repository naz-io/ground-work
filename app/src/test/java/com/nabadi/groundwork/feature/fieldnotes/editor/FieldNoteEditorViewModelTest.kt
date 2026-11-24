package com.nabadi.groundwork.feature.fieldnotes.editor

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.nabadi.groundwork.MainDispatcherRule
import com.nabadi.groundwork.domain.IdGenerator
import com.nabadi.groundwork.feature.fieldnotes.TestFieldNote.fieldNote
import com.nabadi.groundwork.data.repository.FakeFieldNoteRepository
import com.nabadi.groundwork.data.repository.FakeSiteRepository
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.feature.sites.TestSite
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

class FieldNoteEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = Clock.fixed(Instant.ofEpochMilli(NOW), ZoneOffset.UTC)
    private val idGenerator = IdGenerator { GENERATED_ID }

    @Test
    fun `init preselects site when site id is provided for a new field note`() = runTest {
        val siteId = SiteId("site-001")
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(siteId = siteId.value),
        )

        val state = viewModel.uiState.value

        assertEquals(siteId, state.siteId)
        assertFalse(state.isEditing)
        assertFalse(state.isLoading)
    }

    @Test
    fun `site detail creation route preselects site and saves assigned field note`() = runTest {
        val siteId = SiteId("site-001")
        val repository = FakeFieldNoteRepository()
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(siteId = siteId.value),
        )

        assertEquals(siteId, viewModel.uiState.value.siteId)

        viewModel.updateTitle("North gate issue")
        viewModel.saveFieldNote()

        assertEquals(siteId, repository.getFieldNotesStream().first().single().siteId)
    }

    @Test
    fun `site options load only while ui state is collected`() = runTest {
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            siteRepository = FakeSiteRepository().apply {
                setSites(listOf(TestSite.site(id = "site-001", name = "North Warehouse")))
            },
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        assertTrue(viewModel.uiState.value.availableSites.isEmpty())

        viewModel.uiState.test {
            val state = skipItemsUntil { it.availableSites.isNotEmpty() }

            assertEquals(listOf("North Warehouse"), state.availableSites.map { it.name })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `available sites are exposed as editor options`() = runTest {
        val sites = listOf(
            TestSite.site(id = "site-001", name = "North Warehouse"),
            TestSite.site(id = "site-002", name = "South Terminal"),
        )
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            siteRepository = FakeSiteRepository().apply { setSites(sites) },
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.uiState.test {
            val state = skipItemsUntil { it.availableSites.size == sites.size }

            assertEquals(sites.map { it.id }.toSet(), state.availableSites.map { it.id }.toSet())
            assertEquals(sites.map { it.name }.toSet(), state.availableSites.map { it.name }.toSet())
            assertFalse(state.siteOptionsLoadFailed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `site repository failure keeps unassigned capture available and exposes error`() = runTest {
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            siteRepository = FakeSiteRepository().apply { setShouldThrowError(true) },
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.uiState.test {
            val state = skipItemsUntil { it.siteOptionsLoadFailed }

            assertTrue(state.siteOptionsLoadFailed)
            assertTrue(state.availableSites.isEmpty())
            assertNull(state.siteId)
            assertFalse(state.isBusy)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init loads existing field note when field note id is provided`() = runTest {
        val existingFieldNote = fieldNote(
            id = "field-note-001",
            title = "Existing title",
            body = "Existing body",
            status = FieldNoteStatus.DRAFT,
        )
        val fieldNoteRepository = FakeFieldNoteRepository().apply {
            setFieldNotes(listOf(existingFieldNote))
        }
        val siteRepository = FakeSiteRepository()
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = fieldNoteRepository,
            siteRepository = siteRepository,
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(fieldNoteId = existingFieldNote.id.value),
        )

        viewModel.uiState.test {
            val loadedState = skipItemsUntil { state ->
                !state.isLoading && state.title == existingFieldNote.title
            }

            assertEquals("Existing title", loadedState.title)
            assertEquals("Existing body", loadedState.body)
            assertEquals(existingFieldNote.siteId, loadedState.siteId)
            assertEquals(existingFieldNote.status, loadedState.status)
            assertEquals(existingFieldNote.updatedAt, loadedState.updatedAt)
            assertTrue(loadedState.isEditing)
            assertFalse(loadedState.isLoading)
            assertNull(loadedState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init shows error when existing field note is not found`() = runTest {
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(fieldNoteId = "missing-id"),
        )

        viewModel.uiState.test {
            val errorState = skipItemsUntil { state ->
                !state.isLoading && state.error != null
            }

            assertEquals(FieldNoteEditorError.NOT_FOUND, errorState.error)
            assertFalse(errorState.isEditing)
            assertFalse(errorState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateTitle updates title`() = runTest {
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.uiState.test {
            viewModel.updateTitle("North gate issue")

            assertEquals("North gate issue", viewModel.uiState.value.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateBody updates body`() = runTest {
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.uiState.test {
            viewModel.updateBody("Loose fencing near the north access point.")

            assertEquals(
                "Loose fencing near the north access point.",
                viewModel.uiState.value.body
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateAssociatedSite assigns and clears site`() = runTest {
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )
        val siteId = SiteId("site-001")

        viewModel.uiState.test {
            viewModel.updateAssociatedSite(siteId)
            assertEquals(siteId, viewModel.uiState.value.siteId)

            viewModel.updateAssociatedSite(null)
            assertNull(viewModel.uiState.value.siteId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveFieldNote saves new active field note`() = runTest {
        val repository = FakeFieldNoteRepository()
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.uiState.test {
            viewModel.updateTitle("North gate issue")
            viewModel.updateBody("Loose fencing near the north access point.")
            viewModel.updateStatus(FieldNoteStatus.ACTIVE)
            viewModel.saveFieldNote()

            val savedFieldNote = repository.getFieldNotesStream().first().single()
            assertEquals("North gate issue", savedFieldNote.title)
            assertEquals("Loose fencing near the north access point.", savedFieldNote.body)
            assertEquals(FieldNoteStatus.ACTIVE, savedFieldNote.status)
            assertNull(savedFieldNote.siteId)
            assertEquals(FieldNoteId(GENERATED_ID), savedFieldNote.id)
            assertEquals(NOW, savedFieldNote.createdAt)
            assertEquals(NOW, savedFieldNote.updatedAt)
            assertEquals(FieldNoteEditorCompletion.SAVED, viewModel.uiState.value.completion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveFieldNote saves blank title when title is blank`() = runTest {
        val repository = FakeFieldNoteRepository()
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.updateBody("Observation without title.")
        viewModel.saveFieldNote()

        val savedFieldNote = repository.getFieldNotesStream().first().single()
        assertEquals("", savedFieldNote.title)
        assertEquals("Observation without title.", savedFieldNote.body)
    }

    @Test
    fun `saveFieldNote trims the title and body`() = runTest {
        val repository = FakeFieldNoteRepository()
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.updateTitle("  North gate issue ")
        viewModel.updateBody("\nLoose fencing near the north access point.  \n")
        viewModel.saveFieldNote()

        val savedFieldNote = repository.getFieldNotesStream().first().single()
        assertEquals("North gate issue", savedFieldNote.title)
        assertEquals("Loose fencing near the north access point.", savedFieldNote.body)
    }

    @Test
    fun `saveFieldNote updates existing field note and preserves metadata`() = runTest {
        val existingFieldNote = fieldNote(
            id = "field-note-001",
            title = "Original title",
            body = "Original body",
            status = FieldNoteStatus.DRAFT,
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )
        val repository = FakeFieldNoteRepository().apply {
            setFieldNotes(listOf(existingFieldNote))
        }
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(fieldNoteId = existingFieldNote.id.value),
        )

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.updateTitle("Updated title")
            viewModel.updateBody("Updated body")
            viewModel.saveFieldNote()

            val savedFieldNote = repository.getFieldNotesStream().first().single()
            assertEquals(existingFieldNote.id, savedFieldNote.id)
            assertEquals("Updated title", savedFieldNote.title)
            assertEquals("Updated body", savedFieldNote.body)
            assertEquals(FieldNoteStatus.DRAFT, savedFieldNote.status)
            assertEquals(existingFieldNote.createdAt, savedFieldNote.createdAt)
            assertEquals(NOW, savedFieldNote.updatedAt)
            assertEquals(FieldNoteEditorCompletion.SAVED, viewModel.uiState.value.completion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveFieldNote can reassign an existing field note`() = runTest {
        val existingFieldNote = fieldNote(
            id = "field-note-001",
            siteId = "site-001",
        )
        val repository = FakeFieldNoteRepository().apply {
            setFieldNotes(listOf(existingFieldNote))
        }
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(fieldNoteId = existingFieldNote.id.value),
        )

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.updateAssociatedSite(SiteId("site-002"))
            viewModel.saveFieldNote()

            assertEquals(
                SiteId("site-002"),
                repository.getFieldNotesStream().first().single().siteId,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveFieldNote can make an existing field note unassigned`() = runTest {
        val existingFieldNote = fieldNote(
            id = "field-note-001",
            siteId = "site-001",
        )
        val repository = FakeFieldNoteRepository().apply {
            setFieldNotes(listOf(existingFieldNote))
        }
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(fieldNoteId = existingFieldNote.id.value),
        )

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.updateAssociatedSite(null)
            viewModel.saveFieldNote()

            assertNull(repository.getFieldNotesStream().first().single().siteId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteFieldNote deletes existing field note`() = runTest {
        val existingFieldNote = fieldNote(id = "field-note-001")
        val repository = FakeFieldNoteRepository().apply {
            setFieldNotes(listOf(existingFieldNote))
        }
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(fieldNoteId = existingFieldNote.id.value),
        )

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.deleteFieldNote()

            assertTrue(repository.getFieldNotesStream().first().isEmpty())
            assertEquals(FieldNoteEditorCompletion.DELETED, viewModel.uiState.value.completion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `discardChanges finishes without saving the field note`() = runTest {
        val repository = FakeFieldNoteRepository()
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.uiState.test {
            viewModel.updateTitle("Draft title")
            viewModel.updateBody("Draft body")
            viewModel.discardChanges()

            assertEquals(FieldNoteEditorCompletion.DISCARDED, viewModel.uiState.value.completion)
            assertTrue(repository.getFieldNotesStream().first().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveFieldNote shows error when repository save fails`() = runTest {
        val repository = FakeFieldNoteRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.uiState.test {
            viewModel.updateTitle("Title")
            viewModel.saveFieldNote()

            val state = viewModel.uiState.value
            assertFalse(state.isSaving)
            assertEquals(FieldNoteEditorError.SAVE_FAILED, state.error)
            assertNull(state.completion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a closing editor ignores further actions`() = runTest {
        val repository = FakeFieldNoteRepository()
        // Fresh IDs, so a second save would show up as a second note.
        var nextId = 0
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = IdGenerator { "note-${nextId++}" },
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.uiState.test {
            viewModel.updateBody("Loose fencing near the north access point.")
            viewModel.saveFieldNote()
            viewModel.discardChanges()
            viewModel.saveFieldNote()

            assertEquals(FieldNoteEditorCompletion.SAVED, viewModel.uiState.value.completion)
            assertEquals(1, repository.getFieldNotesStream().first().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateTitle clears error message`() = runTest {
        val repository = FakeFieldNoteRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            siteRepository = FakeSiteRepository(),
            clock = clock,
            idGenerator = idGenerator,
            route = GroundWorkRoute.FieldNoteEditor(),
        )

        viewModel.uiState.test {
            viewModel.updateBody("Body")
            viewModel.saveFieldNote()
            viewModel.updateTitle("Updated title")

            assertEquals("Updated title", viewModel.uiState.value.title)
            assertNull(viewModel.uiState.value.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<FieldNoteEditorUiState>.skipItemsUntilLoaded(): FieldNoteEditorUiState =
        skipItemsUntil { state -> !state.isLoading }

    private suspend fun ReceiveTurbine<FieldNoteEditorUiState>.skipItemsUntil(
        predicate: (FieldNoteEditorUiState) -> Boolean,
    ): FieldNoteEditorUiState {
        var item = awaitItem()
        while (!predicate(item)) {
            item = awaitItem()
        }
        return item
    }
}

private const val NOW = 1_750_000_000_000L
private const val GENERATED_ID = "generated-id"
