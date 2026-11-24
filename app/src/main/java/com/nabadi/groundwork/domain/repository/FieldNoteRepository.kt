package com.nabadi.groundwork.domain.repository

import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.SiteId
import kotlinx.coroutines.flow.Flow

interface FieldNoteRepository {
    fun getFieldNotesStream(): Flow<List<FieldNote>>

    fun getFieldNotesForSiteStream(siteId: SiteId): Flow<List<FieldNote>>

    fun getUnassignedFieldNotesStream(): Flow<List<FieldNote>>

    suspend fun getFieldNote(id: FieldNoteId): FieldNote?

    suspend fun saveFieldNote(fieldNote: FieldNote)

    suspend fun deleteFieldNote(id: FieldNoteId)
}