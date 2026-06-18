package com.nabadi.groundwork.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {
    @Query("SELECT * FROM sites ORDER BY updatedAt DESC")
    fun observeSites(): Flow<List<SiteEntity>>

    @Query("SELECT * FROM sites WHERE id = :id")
    suspend fun getSite(id: String): SiteEntity?

    @Upsert
    suspend fun upsertSite(site: SiteEntity)

    @Query("DELETE FROM sites WHERE id = :id")
    suspend fun deleteSite(id: String)
}