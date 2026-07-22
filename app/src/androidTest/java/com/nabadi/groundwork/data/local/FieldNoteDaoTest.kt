package com.nabadi.groundwork.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.TestFieldNotes.fieldNoteEntity
import com.nabadi.groundwork.TestSites.siteEntity
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
    private lateinit var siteDao: SiteDao
    private lateinit var db: GroundWorkDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GroundWorkDatabase::class.java,
        ).build()
        dao = db.fieldNoteDao()
        siteDao = db.siteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `observeFieldNotes emits saved notes ordered by updatedAt descending`() = runTest {
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
    fun `observeFieldNotesForSite emits notes assigned to site ordered by updatedAt descending`() = runTest {
        siteDao.upsertSite(siteEntity(id = "site-1"))
        siteDao.upsertSite(siteEntity(id = "site-2"))

        val olderAssignedNote = fieldNoteEntity(
            id = "1",
            siteId = "site-1",
            title = "Older assigned note",
            updatedAt = 1L,
        )
        val newerAssignedNote = fieldNoteEntity(
            id = "2",
            siteId = "site-1",
            title = "Newer assigned note",
            updatedAt = 3L,
        )
        val otherSiteNote = fieldNoteEntity(
            id = "3",
            siteId = "site-2",
            title = "Other site note",
            updatedAt = 4L,
        )
        val unassignedNote = fieldNoteEntity(
            id = "4",
            siteId = null,
            title = "Unassigned note",
            updatedAt = 5L,
        )
        dao.upsertFieldNote(olderAssignedNote)
        dao.upsertFieldNote(newerAssignedNote)
        dao.upsertFieldNote(otherSiteNote)
        dao.upsertFieldNote(unassignedNote)

        val fieldNotes = dao.observeFieldNotesForSite("site-1").first()

        assertEquals(listOf(newerAssignedNote, olderAssignedNote), fieldNotes)
    }

    @Test
    fun `observeFieldNotesForSite emits empty list for site with no notes`() = runTest {
        val entity = fieldNoteEntity(
            id = "1",
            siteId = null,
        )
        dao.upsertFieldNote(entity)

        val fieldNotes = dao.observeFieldNotesForSite("missing-site").first()

        assertEquals(emptyList<FieldNoteEntity>(), fieldNotes)
    }

    @Test
    fun `observeUnassignedFieldNotes emits unassigned notes ordered by updatedAt descending`() = runTest {
        siteDao.upsertSite(siteEntity(id = "site-1"))

        val olderUnassignedNote = fieldNoteEntity(
            id = "1",
            siteId = null,
            title = "Older unassigned note",
            updatedAt = 1L,
        )
        val newerUnassignedNote = fieldNoteEntity(
            id = "2",
            siteId = null,
            title = "Newer unassigned note",
            updatedAt = 3L,
        )
        val assignedNote = fieldNoteEntity(
            id = "3",
            siteId = "site-1",
            title = "Assigned note",
            updatedAt = 4L,
        )
        dao.upsertFieldNote(olderUnassignedNote)
        dao.upsertFieldNote(newerUnassignedNote)
        dao.upsertFieldNote(assignedNote)

        val fieldNotes = dao.observeUnassignedFieldNotes().first()

        assertEquals(listOf(newerUnassignedNote, olderUnassignedNote), fieldNotes)
    }

    @Test
    fun `getFieldNote returns saved field note`() = runTest {
        siteDao.upsertSite(siteEntity(id = "site-1"))

        val entity = fieldNoteEntity(
            id = "1",
            siteId = "site-1",
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
        assertEquals(entity.siteId, retrievedFieldNote?.siteId)
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
        siteDao.upsertSite(siteEntity(id = "site-1"))

        val originalEntity = fieldNoteEntity(
            id = "1",
            siteId = null,
            title = "Original title",
            body = "Original body",
            status = "DRAFT",
            createdAt = 1L,
            updatedAt = 2L,
        )
        val updatedEntity = fieldNoteEntity(
            id = "1",
            siteId = "site-1",
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
        assertEquals(updatedEntity.siteId, retrievedFieldNote?.siteId)
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
    fun `deleteFieldNote with missing id does not remove other notes`() = runTest {
        val entity = fieldNoteEntity(id = "1")
        dao.upsertFieldNote(entity)

        dao.deleteFieldNote("missing-id")

        val fieldNotes = dao.observeFieldNotes().first()
        assertEquals(listOf(entity), fieldNotes)
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
