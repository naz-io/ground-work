package com.nabadi.groundwork.domain.repository

import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.SiteId
import kotlinx.coroutines.flow.Flow

interface FieldNoteRepository {
    fun observeFieldNotes(): Flow<List<FieldNote>>

    fun observeFieldNotesForSite(siteId: SiteId): Flow<List<FieldNote>>

    fun observeUnassignedFieldNotes(): Flow<List<FieldNote>>

    suspend fun getFieldNote(id: FieldNoteId): FieldNote?

    suspend fun saveFieldNote(fieldNote: FieldNote)

    suspend fun deleteFieldNote(id: FieldNoteId)
}