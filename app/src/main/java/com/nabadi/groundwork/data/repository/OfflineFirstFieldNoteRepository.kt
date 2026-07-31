package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.FieldNoteDao
import com.nabadi.groundwork.data.local.FieldNoteEntity
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SyncState
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstFieldNoteRepository @Inject constructor(
    private val fieldNoteDao: FieldNoteDao,
) : FieldNoteRepository {

    override fun observeFieldNotes(): Flow<List<FieldNote>> =
        fieldNoteDao.observeFieldNotes()
            .toDomainFlow()

    override fun observeFieldNotesForSite(siteId: SiteId): Flow<List<FieldNote>> =
        fieldNoteDao.observeFieldNotesForSite(siteId.value)
            .toDomainFlow()

    override fun observeUnassignedFieldNotes(): Flow<List<FieldNote>> =
        fieldNoteDao.observeUnassignedFieldNotes()
            .toDomainFlow()

    override suspend fun getFieldNote(id: FieldNoteId): FieldNote? =
        fieldNoteDao.getFieldNote(id.value)?.toDomain()

    override suspend fun saveFieldNote(fieldNote: FieldNote) {
        val existingFieldNote = fieldNoteDao.getFieldNoteForSync(fieldNote.id.value)?.toDomain()
        val syncMetadata = existingFieldNote?.syncMetadata?.markPendingUpdate()
            ?: fieldNote.syncMetadata.markPendingCreate()

        fieldNoteDao.upsertFieldNote(fieldNote.copy(syncMetadata = syncMetadata).toEntity())
    }

    override suspend fun deleteFieldNote(id: FieldNoteId) {
        val existingFieldNote = fieldNoteDao.getFieldNoteForSync(id.value)?.toDomain() ?: return

        when {
            existingFieldNote.syncMetadata.state == SyncState.PENDING_CREATE ||
                existingFieldNote.syncMetadata.failedCreate -> fieldNoteDao.deleteFieldNote(id.value)
            existingFieldNote.syncMetadata.state == SyncState.PENDING_DELETE -> Unit
            else -> fieldNoteDao.upsertFieldNote(
                existingFieldNote.copy(
                    syncMetadata = existingFieldNote.syncMetadata.markPendingDelete(),
                ).toEntity()
            )
        }
    }

    private fun Flow<List<FieldNoteEntity>>.toDomainFlow(): Flow<List<FieldNote>> =
        map { entities -> entities.map { it.toDomain() } }
}
