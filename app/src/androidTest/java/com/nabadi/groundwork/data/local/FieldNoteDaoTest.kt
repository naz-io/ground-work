package com.nabadi.groundwork.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.TestFieldNotes.fieldNoteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FieldNoteDaoTest {
    private lateinit var dao: FieldNoteDao
    private lateinit var db: GroundWorkDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GroundWorkDatabase::class.java,
        ).build()
        dao = db.fieldNoteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `observeFieldNotes emits saved notes`() = runTest {
        val olderEntity = fieldNoteEntity(
            id = "1",
            title = "Older field note",
            updatedAt = 1L,
        )
        val newerEntity = fieldNoteEntity(
            id = "2",
            title = "Newer field note",
            updatedAt = 2L,
        )

        dao.upsertFieldNote(olderEntity)
        dao.upsertFieldNote(newerEntity)

        val fieldNotes = dao.observeFieldNotes().first()

        assertEquals(listOf(newerEntity, olderEntity), fieldNotes)
    }

    @Test
    fun `getFieldNote returns saved field note`() = runTest {
        val entity = fieldNoteEntity(
            id = "1",
            title = "Test Field Note",
            body = "This is a test field note.",
            status = "DRAFT",
            createdAt = 1L,
            updatedAt = 2L,
        )

        dao.upsertFieldNote(entity)

        val retrievedFieldNote = dao.getFieldNote(entity.id)

        assertNotNull(retrievedFieldNote)
        assertEquals(entity.id, retrievedFieldNote?.id)
        assertEquals(entity.title, retrievedFieldNote?.title)
        assertEquals(entity.body, retrievedFieldNote?.body)
        assertEquals(entity.status, retrievedFieldNote?.status)
        assertEquals(entity.createdAt, retrievedFieldNote?.createdAt)
        assertEquals(entity.updatedAt, retrievedFieldNote?.updatedAt)
    }

    @Test
    fun `getFieldNote returns null for missing id`() = runTest {
        val retrievedFieldNote = dao.getFieldNote("missing-id")

        assertNull(retrievedFieldNote)
    }

    @Test
    fun `upsertFieldNote replaces existing note with same id`() = runTest {
        val originalEntity = fieldNoteEntity(
            id = "1",
            title = "Original title",
            body = "Original body",
            status = "DRAFT",
            createdAt = 1L,
            updatedAt = 2L,
        )
        val updatedEntity = fieldNoteEntity(
            id = "1",
            title = "Updated title",
            body = "Updated body",
            status = "ACTIVE",
            createdAt = 1L,
            updatedAt = 3L,
        )

        dao.upsertFieldNote(originalEntity)
        dao.upsertFieldNote(updatedEntity)

        val retrievedFieldNote = dao.getFieldNote("1")

        assertNotNull(retrievedFieldNote)
        assertEquals(updatedEntity.id, retrievedFieldNote?.id)
        assertEquals(updatedEntity.title, retrievedFieldNote?.title)
        assertEquals(updatedEntity.body, retrievedFieldNote?.body)
        assertEquals(updatedEntity.status, retrievedFieldNote?.status)
        assertEquals(updatedEntity.createdAt, retrievedFieldNote?.createdAt)
        assertEquals(updatedEntity.updatedAt, retrievedFieldNote?.updatedAt)
    }

    @Test
    fun `deleteFieldNote removes saved note`() = runTest {
        val entity = fieldNoteEntity(id = "1")
        dao.upsertFieldNote(entity)

        dao.deleteFieldNote(entity.id)

        val retrievedFieldNote = dao.getFieldNote(entity.id)

        assertNull(retrievedFieldNote)
    }

    @Test
    fun `observeFieldNotes emits empty list after saved note is deleted`() = runTest {
        val entity = fieldNoteEntity(id = "1")
        dao.upsertFieldNote(entity)

        dao.deleteFieldNote(entity.id)

        val fieldNotes = dao.observeFieldNotes().first()

        assertEquals(emptyList<FieldNoteEntity>(), fieldNotes)
    }
}