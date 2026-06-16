package com.nabadi.groundwork.domain.repository

import com.nabadi.groundwork.domain.model.JobSite
import com.nabadi.groundwork.domain.model.JobSiteId
import com.nabadi.groundwork.domain.model.JobSitePriority
import com.nabadi.groundwork.domain.model.JobSiteStatus
import kotlinx.coroutines.flow.Flow

interface JobSiteRepository {
    fun observeJobSites(): Flow<List<JobSite>>

    fun observeJobSitesByStatus(status: JobSiteStatus): Flow<List<JobSite>>

    fun observeJobSitesByPriority(priority: JobSitePriority): Flow<List<JobSite>>

    suspend fun getJobSite(id: JobSiteId): JobSite?

    suspend fun saveJobSite(jobSite: JobSite)

    suspend fun deleteJobSite(id: JobSiteId)
}