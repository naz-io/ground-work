package com.nabadi.groundwork.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldNoteDao {
    @Query("SELECT * FROM field_notes WHERE $NOT_DELETED ORDER BY updatedAt DESC")
    fun observeFieldNotes(): Flow<List<FieldNoteEntity>>

    @Query(
        "SELECT * FROM field_notes WHERE siteId = :siteId AND $NOT_DELETED " +
            "ORDER BY updatedAt DESC"
    )
    fun observeFieldNotesForSite(siteId: String): Flow<List<FieldNoteEntity>>

    @Query("SELECT * FROM field_notes WHERE siteId = :siteId AND $NOT_DELETED")
    suspend fun getFieldNotesForSite(siteId: String): List<FieldNoteEntity>

    @Query(
        "SELECT * FROM field_notes WHERE siteId IS NULL AND $NOT_DELETED " +
            "ORDER BY updatedAt DESC"
    )
    fun observeUnassignedFieldNotes(): Flow<List<FieldNoteEntity>>

    @Query("SELECT * FROM field_notes WHERE id = :id AND $NOT_DELETED")
    suspend fun getFieldNote(id: String): FieldNoteEntity?

    @Query("SELECT * FROM field_notes WHERE id = :id")
    suspend fun getFieldNoteForSync(id: String): FieldNoteEntity?

    @Upsert
    suspend fun upsertFieldNote(fieldNote: FieldNoteEntity)

    @Query("DELETE FROM field_notes WHERE id = :id")
    suspend fun deleteFieldNote(id: String)
}
