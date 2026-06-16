package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.JobSiteEntity
import com.nabadi.groundwork.domain.model.JobSite
import com.nabadi.groundwork.domain.model.JobSiteId
import com.nabadi.groundwork.domain.model.JobSitePriority
import com.nabadi.groundwork.domain.model.JobSiteStatus

fun JobSiteEntity.toDomain(): JobSite =
    JobSite(
        id = JobSiteId(id),
        name = name,
        description = description,
        location = location,
        priority = JobSitePriority.valueOf(priority),
        status = JobSiteStatus.valueOf(status),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun JobSite.toEntity(): JobSiteEntity =
    JobSiteEntity(
        id = id.value,
        name = name,
        description = description,
        location = location,
        priority = priority.name,
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

