package com.nabadi.groundwork.domain.repository

import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import kotlinx.coroutines.flow.Flow

interface SiteRepository {
    fun observeSites(): Flow<List<Site>>

    suspend fun getSite(id: SiteId): Site?

    suspend fun saveSite(site: Site)

    suspend fun deleteSite(id: SiteId)
}