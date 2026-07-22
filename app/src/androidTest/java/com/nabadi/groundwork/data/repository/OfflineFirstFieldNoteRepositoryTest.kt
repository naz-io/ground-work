package com.nabadi.groundwork.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.TestFieldNotes.fieldNote
import com.nabadi.groundwork.TestSites.siteEntity
import com.nabadi.groundwork.data.local.GroundWorkDatabase
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.SiteId
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
    fun `saveFieldNote updates existing field note with same id`() = runTest {
        database.siteDao().upsertSite(siteEntity(id = "site-1"))

        val originalFieldNote = fieldNote(
            id = "1",
            siteId = null,
            title = "Original title",
            body = "Original body",
            updatedAt = 1L,
        )
        val updatedFieldNote = fieldNote(
            id = "1",
            siteId = "site-1",
            title = "Updated title",
            body = "Updated body",
            updatedAt = 2L,
        )

        repository.saveFieldNote(originalFieldNote)
        repository.saveFieldNote(updatedFieldNote)

        val savedFieldNote = repository.getFieldNote(updatedFieldNote.id)
        val fieldNotes = repository.observeFieldNotes().first()

        assertEquals(updatedFieldNote, savedFieldNote)
        assertEquals(listOf(updatedFieldNote), fieldNotes)
    }

    @Test
    fun `observeFieldNotes emits saved field notes ordered by updatedAt descending`() = runTest {
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
    fun `observeFieldNotesForSite emits field notes assigned to site ordered by updatedAt descending`() = runTest {
        database.siteDao().upsertSite(siteEntity(id = "site-1"))
        database.siteDao().upsertSite(siteEntity(id = "site-2"))

        val olderAssignedFieldNote = fieldNote(
            id = "1",
            siteId = "site-1",
            title = "Older assigned field note",
            updatedAt = 1L,
        )
        val newerAssignedFieldNote = fieldNote(
            id = "2",
            siteId = "site-1",
            title = "Newer assigned field note",
            updatedAt = 3L,
        )
        val otherSiteFieldNote = fieldNote(
            id = "3",
            siteId = "site-2",
            title = "Other site field note",
            updatedAt = 4L,
        )
        val unassignedFieldNote = fieldNote(
            id = "4",
            siteId = null,
            title = "Unassigned field note",
            updatedAt = 5L,
        )

        repository.saveFieldNote(olderAssignedFieldNote)
        repository.saveFieldNote(newerAssignedFieldNote)
        repository.saveFieldNote(otherSiteFieldNote)
        repository.saveFieldNote(unassignedFieldNote)

        val fieldNotes = repository.observeFieldNotesForSite(SiteId("site-1")).first()

        assertEquals(listOf(newerAssignedFieldNote, olderAssignedFieldNote), fieldNotes)
    }

    @Test
    fun `observeFieldNotesForSite emits empty list for site with no field notes`() = runTest {
        val fieldNote = fieldNote(
            id = "1",
            siteId = null,
        )
        repository.saveFieldNote(fieldNote)

        val fieldNotes = repository.observeFieldNotesForSite(SiteId("missing-site")).first()

        assertEquals(emptyList<FieldNote>(), fieldNotes)
    }

    @Test
    fun `observeUnassignedFieldNotes emits unassigned field notes ordered by updatedAt descending`() = runTest {
        database.siteDao().upsertSite(siteEntity(id = "site-1"))

        val olderUnassignedFieldNote = fieldNote(
            id = "1",
            siteId = null,
            title = "Older unassigned field note",
            updatedAt = 1L,
        )
        val newerUnassignedFieldNote = fieldNote(
            id = "2",
            siteId = null,
            title = "Newer unassigned field note",
            updatedAt = 3L,
        )
        val assignedFieldNote = fieldNote(
            id = "3",
            siteId = "site-1",
            title = "Assigned field note",
            updatedAt = 4L,
        )

        repository.saveFieldNote(olderUnassignedFieldNote)
        repository.saveFieldNote(newerUnassignedFieldNote)
        repository.saveFieldNote(assignedFieldNote)

        val fieldNotes = repository.observeUnassignedFieldNotes().first()

        assertEquals(listOf(newerUnassignedFieldNote, olderUnassignedFieldNote), fieldNotes)
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
    fun `deleteFieldNote with missing id does not remove other field notes`() = runTest {
        val fieldNote = fieldNote(id = "1")
        repository.saveFieldNote(fieldNote)

        repository.deleteFieldNote(FieldNoteId("missing-id"))

        val fieldNotes = repository.observeFieldNotes().first()

        assertEquals(listOf(fieldNote), fieldNotes)
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
