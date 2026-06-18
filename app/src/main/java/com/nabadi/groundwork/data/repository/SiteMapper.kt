package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.SiteEntity
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus

fun SiteEntity.toDomain(): Site =
    Site(
        id = SiteId(id),
        name = name,
        description = description,
        location = location,
        priority = SitePriority.valueOf(priority),
        status = SiteStatus.valueOf(status),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Site.toEntity(): SiteEntity =
    SiteEntity(
        id = id.value,
        name = name,
        description = description,
        location = location,
        priority = priority.name,
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

