package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.SiteEntity
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class SiteMapperTest {

    @Test
    fun `toDomain maps SiteEntity to domain model`() {
        val entity = SiteEntity(
            id = "1",
            name = "Test Site",
            description = "This is a test site.",
            location = "Test Location",
            priority = "URGENT",
            status = "ACTIVE",
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )
        val site = entity.toDomain()

        assertEquals("1", site.id.value)
        assertEquals("Test Site", site.name)
        assertEquals("This is a test site.", site.description)
        assertEquals("Test Location", site.location)
        assertEquals(SitePriority.URGENT, site.priority)
        assertEquals(SiteStatus.ACTIVE, site.status)
        assertEquals(1_734_220_800_000L, site.createdAt)
        assertEquals(1_734_224_400_000L, site.updatedAt)
    }

    @Test
    fun `toEntity maps domain model to SiteEntity`() {
        val site = Site(
            id = SiteId("1"),
            name = "Test Site",
            description = "This is a test site.",
            location = "Test Location",
            priority = SitePriority.URGENT,
            status = SiteStatus.ACTIVE,
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )
        val entity = site.toEntity()

        assertEquals("1", entity.id)
        assertEquals("Test Site", entity.name)
        assertEquals("This is a test site.", entity.description)
        assertEquals("Test Location", entity.location)
        assertEquals("URGENT", entity.priority)
        assertEquals("ACTIVE", entity.status)
        assertEquals(1_734_220_800_000L, entity.createdAt)
        assertEquals(1_734_224_400_000L, entity.updatedAt)
    }

    @Test
    fun `toEntity maps completed status to entity status value`() {
        val site = Site(
            id = SiteId("2"),
            name = "Completed Site",
            description = "Completed remediation site.",
            location = "West Yard",
            priority = SitePriority.NORMAL,
            status = SiteStatus.COMPLETED,
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )

        val entity = site.toEntity()

        assertEquals("COMPLETED", entity.status)
    }

    @Test
    fun `toEntity maps low priority to entity priority value`() {
        val site = Site(
            id = SiteId("3"),
            name = "Low Priority Site",
            description = "Routine inspection site.",
            location = "South Yard",
            priority = SitePriority.LOW,
            status = SiteStatus.ACTIVE,
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )

        val entity = site.toEntity()

        assertEquals("LOW", entity.priority)
    }
}