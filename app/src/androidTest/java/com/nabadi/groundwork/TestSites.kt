package com.nabadi.groundwork

import com.nabadi.groundwork.data.local.SiteEntity
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus

object TestSites {

    fun site(
        id: String = "site-1",
        name: String = "Test Site",
        description: String = "This is a test site.",
        location: String = "Test Location",
        priority: SitePriority = SitePriority.NORMAL,
        status: SiteStatus = SiteStatus.ACTIVE,
        createdAt: Long = 1_000L,
        updatedAt: Long = 2_000L,
    ): Site = Site(
        id = SiteId(id),
        name = name,
        description = description,
        location = location,
        priority = priority,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    fun siteEntity(
        id: String = "site-1",
        name: String = "Test Site",
        description: String = "This is a test site.",
        location: String = "Test Location",
        priority: String = "NORMAL",
        status: String = "ACTIVE",
        createdAt: Long = 1_000L,
        updatedAt: Long = 2_000L,
    ): SiteEntity = SiteEntity(
        id = id,
        name = name,
        description = description,
        location = location,
        priority = priority,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}