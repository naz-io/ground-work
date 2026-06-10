package com.nabadi.groundwork.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.TestFieldNotes.fieldNote
import com.nabadi.groundwork.data.local.GroundWorkDatabase
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineFirstFieldNoteRepositoryTest {
    private lateinit var database: GroundWorkDatabase
    private lateinit var repository: OfflineFirstFieldNoteRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            GroundWorkDatabase::class.java,
        ).build()

        repository = OfflineFirstFieldNoteRepository(
            fieldNoteDao = database.fieldNoteDao(),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `saveFieldNote persists field note`() = runTest {
        val fieldNote = fieldNote(id = "1")

        repository.saveFieldNote(fieldNote)

        val savedFieldNote = repository.getFieldNote(fieldNote.id)

        assertEquals(fieldNote, savedFieldNote)
    }

    @Test
    fun `observeFieldNotes emits saved field notes`() = runTest {
        val olderFieldNote = fieldNote(
            id = "1",
            title = "Older field note",
            updatedAt = 1L,
        )
        val newerFieldNote = fieldNote(
            id = "2",
            title = "Newer field note",
            updatedAt = 2L,
        )

        repository.saveFieldNote(olderFieldNote)
        repository.saveFieldNote(newerFieldNote)

        val fieldNotes = repository.observeFieldNotes().first()

        assertEquals(listOf(newerFieldNote, olderFieldNote), fieldNotes)
    }

    @Test
    fun `getFieldNote returns saved field note`() = runTest {
        val fieldNote = fieldNote(id = "1")

        repository.saveFieldNote(fieldNote)

        val savedFieldNote = repository.getFieldNote(fieldNote.id)

        assertEquals(fieldNote, savedFieldNote)
    }

    @Test
    fun `getFieldNote returns null for missing id`() = runTest {
        val savedFieldNote = repository.getFieldNote(FieldNoteId("missing-id"))

        assertNull(savedFieldNote)
    }

    @Test
    fun `deleteFieldNote removes saved field note`() = runTest {
        val fieldNote = fieldNote(id = "1")
        repository.saveFieldNote(fieldNote)

        repository.deleteFieldNote(fieldNote.id)

        val savedFieldNote = repository.getFieldNote(fieldNote.id)

        assertNull(savedFieldNote)
    }

    @Test
    fun `observeFieldNotes emits empty list after saved field note is deleted`() = runTest {
        val fieldNote = fieldNote(id = "1")
        repository.saveFieldNote(fieldNote)

        repository.deleteFieldNote(fieldNote.id)

        val fieldNotes = repository.observeFieldNotes().first()

        assertEquals(emptyList<FieldNote>(), fieldNotes)
    }
}