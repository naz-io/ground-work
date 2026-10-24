package com.nabadi.groundwork.data.repository

import androidx.room.withTransaction
import com.nabadi.groundwork.data.local.FieldNoteDao
import com.nabadi.groundwork.data.local.GroundWorkDatabase
import com.nabadi.groundwork.data.local.SiteDao
import com.nabadi.groundwork.data.local.SiteEntity
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SyncState
import com.nabadi.groundwork.domain.repository.SiteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import javax.inject.Inject

class OfflineFirstSiteRepository @Inject constructor(
    private val database: GroundWorkDatabase,
    private val siteDao: SiteDao,
    private val fieldNoteDao: FieldNoteDao,
    private val clock: Clock,
) : SiteRepository {
    override fun observeSites(): Flow<List<Site>> =
        siteDao.observeSites().toDomainFlow()

    override suspend fun getSite(id: SiteId): Site? =
        siteDao.getSite(id.value)?.toDomain()

    override suspend fun saveSite(site: Site) {
        database.withTransaction {
            val existingSite = siteDao.getSiteForSync(site.id.value)?.toDomain()
            val syncMetadata = existingSite?.syncMetadata?.markPendingUpdate()
                ?: site.syncMetadata.markPendingCreate()

            siteDao.upsertSite(site.copy(syncMetadata = syncMetadata).toEntity())
        }
    }

    override suspend fun deleteSite(id: SiteId) {
        database.withTransaction {
            val existingSite = siteDao.getSiteForSync(id.value)?.toDomain()
                ?: return@withTransaction

            if (existingSite.syncMetadata.state == SyncState.PENDING_DELETE) return@withTransaction

            // A pending delete keeps the site row, so ON DELETE SET NULL never fires.
            // Unassign the notes explicitly so they stay visible and sync their new site.
            // Unassigning is a real change to each note, so it also moves updatedAt.
            unassignFieldNotes(id)

            if (
                existingSite.syncMetadata.state == SyncState.PENDING_CREATE ||
                existingSite.syncMetadata.failedCreate
            ) {
                siteDao.deleteSite(id.value)
            } else {
                siteDao.upsertSite(
                    existingSite.copy(
                        syncMetadata = existingSite.syncMetadata.markPendingDelete(),
                    ).toEntity()
                )
            }
        }
    }

    private suspend fun unassignFieldNotes(siteId: SiteId) {
        val now = clock.millis()
        fieldNoteDao.getFieldNotesForSite(siteId.value)
            .map { it.toDomain() }
            .forEach { fieldNote ->
                fieldNoteDao.upsertFieldNote(
                    fieldNote.copy(
                        siteId = null,
                        updatedAt = now,
                        syncMetadata = fieldNote.syncMetadata.markPendingUpdate(),
                    ).toEntity()
                )
            }
    }

    private fun Flow<List<SiteEntity>>.toDomainFlow(): Flow<List<Site>> =
        map { entities -> entities.map { it.toDomain() } }
}
