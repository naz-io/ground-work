package com.nabadi.groundwork.domain.repository

import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import kotlinx.coroutines.flow.Flow

interface FieldNoteRepository {
    fun observeFieldNotes(): Flow<List<FieldNote>>

    suspend fun getFieldNote(id: FieldNoteId): FieldNote?

    suspend fun saveFieldNote(fieldNote: FieldNote)

    suspend fun deleteFieldNote(id: FieldNoteId)
}