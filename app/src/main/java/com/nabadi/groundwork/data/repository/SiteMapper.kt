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
        priority = priority.toPersistedEnumOrNull() ?: SitePriority.NORMAL,
        status = status.toPersistedEnumOrNull() ?: SiteStatus.ARCHIVED,
        createdAt = createdAt,
        updatedAt = updatedAt,
        syncMetadata = syncMetadata.toDomain(),
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
        syncMetadata = syncMetadata.toEntity(),
    )
