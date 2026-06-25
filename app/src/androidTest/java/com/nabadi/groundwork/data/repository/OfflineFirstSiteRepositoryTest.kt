package com.nabadi.groundwork.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.TestSites.site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.data.local.GroundWorkDatabase
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.repository.SiteRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineFirstSiteRepositoryTest {
    private lateinit var repository: SiteRepository
    private lateinit var database: GroundWorkDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            GroundWorkDatabase::class.java,
        ).build()
        repository = OfflineFirstSiteRepository(
            siteDao = database.siteDao(),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `saveSite persists site`() = runTest {
        val site = site(id = "1")
        repository.saveSite(site)

        val savedSite = repository.getSite(site.id)

        assertEquals(site, savedSite)
    }

    @Test
    fun `saveSite updates existing site with same id`() = runTest {
        val originalSite = site(
            id = "1",
            name = "Original site",
            description = "Original description",
            location = "Original location",
            updatedAt = 1L,
        )
        val updatedSite = site(
            id = "1",
            name = "Updated site",
            description = "Updated description",
            location = "Updated location",
            updatedAt = 2L,
        )

        repository.saveSite(originalSite)
        repository.saveSite(updatedSite)

        val savedSite = repository.getSite(updatedSite.id)
        val sites = repository.observeSites().first()

        assertEquals(updatedSite, savedSite)
        assertEquals(listOf(updatedSite), sites)
    }

    @Test
    fun `observeSites emits saved sites ordered by updatedAt descending`() = runTest {
        val olderSite = site(
            id = "1",
            name = "Older site",
            updatedAt = 1L,
        )
        val newerSite = site(
            id = "2",
            name = "Newer site",
            updatedAt = 2L,
        )

        repository.saveSite(olderSite)
        repository.saveSite(newerSite)

        val sites = repository.observeSites().first()

        assertEquals(listOf(newerSite, olderSite), sites)
    }

    @Test
    fun `getSite returns saved site`() = runTest {
        val site = site(id = "1")

        repository.saveSite(site)

        val savedSite = repository.getSite(site.id)

        assertEquals(site, savedSite)
    }

    @Test
    fun `getSite returns null for missing id`() = runTest {
        val savedSite = repository.getSite(SiteId("missing-id"))

        assertNull(savedSite)
    }

    @Test
    fun `deleteSite removes saved site`() = runTest {
        val site = site(id = "1")
        repository.saveSite(site)

        repository.deleteSite(site.id)

        val savedSite = repository.getSite(site.id)

        assertNull(savedSite)
    }

    @Test
    fun `deleteSite with missing id does not remove other sites`() = runTest {
        val site = site(id = "1")
        repository.saveSite(site)

        repository.deleteSite(SiteId("missing-id"))

        val sites = repository.observeSites().first()

        assertEquals(listOf(site), sites)
    }

    @Test
    fun `observeSites emits empty list after saved site is deleted`() = runTest {
        val site = site(id = "1")
        repository.saveSite(site)

        repository.deleteSite(site.id)

        val sites = repository.observeSites().first()

        assertEquals(emptyList<Site>(), sites)
    }
}