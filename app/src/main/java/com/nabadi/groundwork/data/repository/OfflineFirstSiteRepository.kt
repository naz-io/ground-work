package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.SiteDao
import com.nabadi.groundwork.data.local.SiteEntity
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SyncState
import com.nabadi.groundwork.domain.repository.SiteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstSiteRepository @Inject constructor(
    private val siteDao: SiteDao,
) : SiteRepository {
    override fun observeSites(): Flow<List<Site>> =
        siteDao.observeSites().toDomainFlow()

    override suspend fun getSite(id: SiteId): Site? =
        siteDao.getSite(id.value)?.toDomain()

    override suspend fun saveSite(site: Site) {
        val existingSite = siteDao.getSiteForSync(site.id.value)?.toDomain()
        val syncMetadata = existingSite?.syncMetadata?.markPendingUpdate()
            ?: site.syncMetadata.markPendingCreate()

        siteDao.upsertSite(site.copy(syncMetadata = syncMetadata).toEntity())
    }

    override suspend fun deleteSite(id: SiteId) {
        val existingSite = siteDao.getSiteForSync(id.value)?.toDomain() ?: return

        when {
            existingSite.syncMetadata.state == SyncState.PENDING_CREATE ||
                existingSite.syncMetadata.failedCreate -> siteDao.deleteSite(id.value)
            existingSite.syncMetadata.state == SyncState.PENDING_DELETE -> Unit
            else -> siteDao.upsertSite(
                existingSite.copy(
                    syncMetadata = existingSite.syncMetadata.markPendingDelete(),
                ).toEntity()
            )
        }
    }

    private fun Flow<List<SiteEntity>>.toDomainFlow(): Flow<List<Site>> =
        map { entities -> entities.map { it.toDomain() } }
}
