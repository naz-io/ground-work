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
class SiteDaoTest {
    private lateinit var dao: SiteDao
    private lateinit var fieldNoteDao: FieldNoteDao
    private lateinit var db: GroundWorkDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GroundWorkDatabase::class.java,
        ).build()
        dao = db.siteDao()
        fieldNoteDao = db.fieldNoteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `observeSites emits saved sites ordered by updatedAt descending`() = runTest {
        val olderEntity = siteEntity(
            id = "1",
            name = "Older site",
            updatedAt = 1L,
        )
        val newerEntity = siteEntity(
            id = "2",
            name = "Newer site",
            updatedAt = 2L,
        )

        dao.upsertSite(olderEntity)
        dao.upsertSite(newerEntity)

        val sites = dao.observeSites().first()
        assertEquals(listOf(newerEntity, olderEntity), sites)
    }

    @Test
    fun `getSite returns saved site`() = runTest {
        val entity = siteEntity(
            id = "1",
            name = "Test Site",
            description = "This is a test site.",
            location = "Test Location",
            priority = "NORMAL",
            status = "ACTIVE",
            createdAt = 1L,
            updatedAt = 2L,
        )

        dao.upsertSite(entity)

        val retrievedSite = dao.getSite(entity.id)
        assertNotNull(retrievedSite)
        assertEquals(entity.id, retrievedSite?.id)
        assertEquals(entity.name, retrievedSite?.name)
        assertEquals(entity.description, retrievedSite?.description)
        assertEquals(entity.location, retrievedSite?.location)
        assertEquals(entity.priority, retrievedSite?.priority)
        assertEquals(entity.status, retrievedSite?.status)
        assertEquals(entity.createdAt, retrievedSite?.createdAt)
        assertEquals(entity.updatedAt, retrievedSite?.updatedAt)
    }

    @Test
    fun `getSite returns null for missing id`() = runTest {
        val retrievedSite = dao.getSite("missing-id")
        assertNull(retrievedSite)
    }

    @Test
    fun `upsertSite replaces existing site with same id`() = runTest {
        val originalEntity = siteEntity(
            id = "1",
            name = "Original name",
            description = "Original description",
            location = "Original location",
            priority = "NORMAL",
            status = "ACTIVE",
            createdAt = 1L,
            updatedAt = 2L,
        )
        val updatedEntity = siteEntity(
            id = "1",
            name = "Updated name",
            description = "Updated description",
            location = "Updated location",
            priority = "HIGH",
            status = "ARCHIVED",
            createdAt = 1L,
            updatedAt = 3L,
        )

        dao.upsertSite(originalEntity)
        dao.upsertSite(updatedEntity)

        val retrievedSite = dao.getSite("1")
        assertNotNull(retrievedSite)
        assertEquals(updatedEntity.id, retrievedSite?.id)
        assertEquals(updatedEntity.name, retrievedSite?.name)
        assertEquals(updatedEntity.description, retrievedSite?.description)
        assertEquals(updatedEntity.location, retrievedSite?.location)
        assertEquals(updatedEntity.priority, retrievedSite?.priority)
        assertEquals(updatedEntity.status, retrievedSite?.status)
        assertEquals(updatedEntity.createdAt, retrievedSite?.createdAt)
        assertEquals(updatedEntity.updatedAt, retrievedSite?.updatedAt)
    }

    @Test
    fun `deleteSite removes saved site`() = runTest {
        val entity = siteEntity(id = "1")
        dao.upsertSite(entity)

        dao.deleteSite(entity.id)

        val retrievedSite = dao.getSite(entity.id)
        assertNull(retrievedSite)
    }

    @Test
    fun `deleteSite with missing id does not remove other sites`() = runTest {
        val entity = siteEntity(id = "1")
        dao.upsertSite(entity)

        dao.deleteSite("missing-id")

        val sites = dao.observeSites().first()
        assertEquals(listOf(entity), sites)
    }

    @Test
    fun `observeSites emits empty list after saved site is deleted`() = runTest {
        val entity = siteEntity(id = "1")
        dao.upsertSite(entity)

        dao.deleteSite(entity.id)

        val sites = dao.observeSites().first()
        assertEquals(emptyList<SiteEntity>(), sites)
    }

    @Test
    fun `deleteSite preserves its field notes as unassigned`() = runTest {
        val site = siteEntity(id = "site-1")
        val fieldNote = fieldNoteEntity(id = "note-1", siteId = site.id)
        dao.upsertSite(site)
        fieldNoteDao.upsertFieldNote(fieldNote)

        dao.deleteSite(site.id)

        val savedFieldNote = fieldNoteDao.getFieldNote(fieldNote.id)
        assertNotNull(savedFieldNote)
        assertNull(savedFieldNote?.siteId)
        assertEquals(
            listOf(fieldNote.copy(siteId = null)),
            fieldNoteDao.observeUnassignedFieldNotes().first(),
        )
    }
}
