package com.nabadi.groundwork.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldNoteDao {
    @Query("SELECT * FROM field_notes ORDER BY updatedAt DESC")
    fun observeFieldNotes(): Flow<List<FieldNoteEntity>>

    @Query("SELECT * FROM field_notes WHERE siteId = :siteId ORDER BY updatedAt DESC")
    fun observeFieldNotesForSite(siteId: String): Flow<List<FieldNoteEntity>>

    @Query("SELECT * FROM field_notes WHERE siteId IS NULL ORDER BY updatedAt DESC")
    fun observeUnassignedFieldNotes(): Flow<List<FieldNoteEntity>>

    @Query("SELECT * FROM field_notes WHERE id = :id")
    suspend fun getFieldNote(id: String): FieldNoteEntity?

    @Upsert
    suspend fun upsertFieldNote(fieldNote: FieldNoteEntity)

    @Query("DELETE FROM field_notes WHERE id = :id")
    suspend fun deleteFieldNote(id: String)
}
