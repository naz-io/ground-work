package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.FieldNoteDao
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstFieldNoteRepository @Inject constructor(
    private val fieldNoteDao: FieldNoteDao
) : FieldNoteRepository {

    override fun observeFieldNotes(): Flow<List<FieldNote>> =
        fieldNoteDao.observeFieldNotes()
            .map { entities ->
                entities.map { it.toDomain() }
            }

    override suspend fun getFieldNote(id: FieldNoteId): FieldNote? =
        fieldNoteDao.getFieldNote(id.value)?.toDomain()


    override suspend fun saveFieldNote(fieldNote: FieldNote) =
        fieldNoteDao.upsertFieldNote(fieldNote.toEntity())

    override suspend fun deleteFieldNote(id: FieldNoteId) =
        fieldNoteDao.deleteFieldNote(id.value)
}