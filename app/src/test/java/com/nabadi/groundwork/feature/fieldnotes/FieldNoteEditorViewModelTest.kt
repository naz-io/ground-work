package com.nabadi.groundwork.feature.fieldnotes

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.nabadi.groundwork.MainDispatcherRule
import com.nabadi.groundwork.TestFieldNote.fieldNote
import com.nabadi.groundwork.data.repository.FakeFieldNoteRepository
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.navigation.GroundWorkRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FieldNoteEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init loads existing field note when field note id is provided`() = runTest {
        val existingFieldNote = fieldNote(
            id = "field-note-001",
            title = "Existing title",
            body = "Existing body",
            status = FieldNoteStatus.DRAFT,
        )
        val repository = FakeFieldNoteRepository().apply {
            setFieldNotes(listOf(existingFieldNote))
        }
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(GroundWorkRoute.FIELD_NOTE_ID_ARG to existingFieldNote.id.value),
            ),
        )

        viewModel.uiState.test {
            val loadedState = skipItemsUntil { state ->
                !state.isLoading && state.title == existingFieldNote.title
            }

            assertEquals("Existing title", loadedState.title)
            assertEquals("Existing body", loadedState.body)
            assertTrue(loadedState.isEditing)
            assertFalse(loadedState.isLoading)
            assertNull(loadedState.errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init shows error when existing field note is not found`() = runTest {
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(GroundWorkRoute.FIELD_NOTE_ID_ARG to "missing-id"),
            ),
        )

        viewModel.uiState.test {
            val errorState = skipItemsUntil { state ->
                !state.isLoading && state.errorMessage != null
            }

            assertEquals("Field note not found.", errorState.errorMessage)
            assertFalse(errorState.isEditing)
            assertFalse(errorState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onTitleChange updates title`() = runTest {
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onTitleChange("North gate issue")

        assertEquals("North gate issue", viewModel.uiState.value.title)
    }

    @Test
    fun `onBodyChange updates body`() = runTest {
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = FakeFieldNoteRepository(),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onBodyChange("Loose fencing near the north access point.")

        assertEquals("Loose fencing near the north access point.", viewModel.uiState.value.body)
    }

    @Test
    fun `saveFieldNote saves new active field note`() = runTest {
        val repository = FakeFieldNoteRepository()
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )
        var onSavedCalled = false

        viewModel.onTitleChange("North gate issue")
        viewModel.onBodyChange("Loose fencing near the north access point.")
        viewModel.saveFieldNote(onSaved = { onSavedCalled = true })

        val savedFieldNote = repository.observeFieldNotes().first().single()
        assertEquals("North gate issue", savedFieldNote.title)
        assertEquals("Loose fencing near the north access point.", savedFieldNote.body)
        assertEquals(FieldNoteStatus.ACTIVE, savedFieldNote.status)
        assertTrue(savedFieldNote.id.value.isNotBlank())
        assertTrue(savedFieldNote.createdAt > 0L)
        assertTrue(savedFieldNote.updatedAt > 0L)
        assertTrue(onSavedCalled)
        assertEquals(FieldNoteEditorUiState(), viewModel.uiState.value)
    }

    @Test
    fun `saveFieldNote uses untitled field note when title is blank`() = runTest {
        val repository = FakeFieldNoteRepository()
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onBodyChange("Observation without title.")
        viewModel.saveFieldNote(onSaved = {})

        val savedFieldNote = repository.observeFieldNotes().first().single()
        assertEquals("Untitled field note", savedFieldNote.title)
        assertEquals("Observation without title.", savedFieldNote.body)
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
            savedStateHandle = SavedStateHandle(
                mapOf(GroundWorkRoute.FIELD_NOTE_ID_ARG to existingFieldNote.id.value),
            ),
        )
        var onSavedCalled = false

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.onTitleChange("Updated title")
            viewModel.onBodyChange("Updated body")
            viewModel.saveFieldNote(onSaved = { onSavedCalled = true })

            val savedFieldNote = repository.observeFieldNotes().first().single()
            assertEquals(existingFieldNote.id, savedFieldNote.id)
            assertEquals("Updated title", savedFieldNote.title)
            assertEquals("Updated body", savedFieldNote.body)
            assertEquals(FieldNoteStatus.DRAFT, savedFieldNote.status)
            assertEquals(existingFieldNote.createdAt, savedFieldNote.createdAt)
            assertTrue(savedFieldNote.updatedAt >= existingFieldNote.updatedAt)
            assertTrue(onSavedCalled)
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
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(GroundWorkRoute.FIELD_NOTE_ID_ARG to existingFieldNote.id.value),
            ),
        )
        var onDeletedCalled = false

        viewModel.uiState.test {
            skipItemsUntilLoaded()

            viewModel.deleteFieldNote(onDeleted = { onDeletedCalled = true })

            assertTrue(repository.observeFieldNotes().first().isEmpty())
            assertTrue(onDeletedCalled)
            assertEquals(FieldNoteEditorUiState(), viewModel.uiState.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `discardDraft clears state and does not save field note`() = runTest {
        val repository = FakeFieldNoteRepository()
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )
        var onDiscardedCalled = false

        viewModel.onTitleChange("Draft title")
        viewModel.onBodyChange("Draft body")
        viewModel.discardDraft(onDiscarded = { onDiscardedCalled = true })

        assertEquals(FieldNoteEditorUiState(), viewModel.uiState.value)
        assertTrue(onDiscardedCalled)
        assertTrue(repository.observeFieldNotes().first().isEmpty())
    }

    @Test
    fun `saveFieldNote shows error when repository save fails`() = runTest {
        val repository = FakeFieldNoteRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )
        var onSavedCalled = false

        viewModel.onTitleChange("Title")
        viewModel.saveFieldNote(onSaved = { onSavedCalled = true })

        val state = viewModel.uiState.value
        assertFalse(state.isSaving)
        assertEquals("Unable to save field note.", state.errorMessage)
        assertFalse(onSavedCalled)
    }

    @Test
    fun `onTitleChange clears error message`() = runTest {
        val repository = FakeFieldNoteRepository().apply {
            setShouldThrowError(true)
        }
        val viewModel = FieldNoteEditorViewModel(
            fieldNoteRepository = repository,
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onBodyChange("Body")
        viewModel.saveFieldNote(onSaved = {})
        viewModel.onTitleChange("Updated title")

        assertEquals("Updated title", viewModel.uiState.value.title)
        assertNull(viewModel.uiState.value.errorMessage)
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