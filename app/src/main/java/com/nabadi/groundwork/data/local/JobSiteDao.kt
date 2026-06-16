package com.nabadi.groundwork.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface JobSiteDao {
    @Query("SELECT * FROM job_sites ORDER BY updatedAt DESC")
    fun observeJobSites(): Flow<List<JobSiteEntity>>

    @Query("SELECT * FROM job_sites WHERE status = :status ORDER BY updatedAt DESC")
    fun observeJobSitesByStatus(status: String): Flow<List<JobSiteEntity>>

    @Query("SELECT * FROM job_sites WHERE priority = :priority ORDER BY updatedAt DESC")
    fun observeJobSitesByPriority(priority: String): Flow<List<JobSiteEntity>>

    @Query("SELECT * FROM job_sites WHERE id = :id")
    suspend fun getJobSite(id: String): JobSiteEntity?

    @Upsert
    suspend fun upsertJobSite(jobSite: JobSiteEntity)

    @Query("DELETE FROM job_sites WHERE id = :id")
    suspend fun deleteJobSite(id: String)
}