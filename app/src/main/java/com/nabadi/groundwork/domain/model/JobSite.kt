package com.nabadi.groundwork.domain.model

data class JobSite(
    val id: JobSiteId,
    val name: String,
    val description: String,
    val location: String,
    val priority: JobSitePriority,
    val status: JobSiteStatus,
    val createdAt: Long,
    val updatedAt: Long,
)
