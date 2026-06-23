package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.repository.SiteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeSitesRepository : SiteRepository {

    private val sitesFlow = MutableStateFlow<Map<SiteId, Site>>(emptyMap())
    private var shouldThrowError = false

    override fun observeSites(): Flow<List<Site>> =
        sitesFlow.map { sites ->
            if (shouldThrowError) throw IllegalStateException("Test Error")
            sites.values.sortedByDescending { it.updatedAt }
        }

    override suspend fun getSite(id: SiteId): Site? {
        if (shouldThrowError) throw IllegalStateException("Test Error")
        return sitesFlow.value[id]
    }

    override suspend fun saveSite(site: Site) {
        if (shouldThrowError) throw IllegalStateException("Test Error")
        sitesFlow.update { it + (site.id to site) }
    }

    override suspend fun deleteSite(id: SiteId) {
        if (shouldThrowError) throw IllegalStateException("Test Error")
        sitesFlow.update { it - id }
    }

    fun setSites(sites: List<Site>) {
        sitesFlow.value = sites.associateBy { it.id }
    }

    fun setShouldThrowError(value: Boolean) {
        shouldThrowError = value
    }
}