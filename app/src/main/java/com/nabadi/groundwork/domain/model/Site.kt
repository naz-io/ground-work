package com.nabadi.groundwork.domain.model

data class Site(
    val id: SiteId,
    val name: String,
    val description: String,
    val location: String,
    val priority: SitePriority,
    val status: SiteStatus,
    val createdAt: Long,
    val updatedAt: Long,
)
