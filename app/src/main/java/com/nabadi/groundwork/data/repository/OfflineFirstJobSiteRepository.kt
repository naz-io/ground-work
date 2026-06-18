package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.JobSiteDao
import com.nabadi.groundwork.data.local.JobSiteEntity
import com.nabadi.groundwork.domain.model.JobSite
import com.nabadi.groundwork.domain.model.JobSiteId
import com.nabadi.groundwork.domain.model.JobSitePriority
import com.nabadi.groundwork.domain.model.JobSiteStatus
import com.nabadi.groundwork.domain.repository.JobSiteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstJobSiteRepository @Inject constructor(
    private val jobSiteDao: JobSiteDao,
) : JobSiteRepository {
    override fun observeJobSites(): Flow<List<JobSite>> =
        jobSiteDao.observeJobSites().toDomainFlow()

    override suspend fun getJobSite(id: JobSiteId): JobSite? =
        jobSiteDao.getJobSite(id.value)?.toDomain()

    override suspend fun saveJobSite(jobSite: JobSite): Unit =
        jobSiteDao.upsertJobSite(jobSite.toEntity())

    override suspend fun deleteJobSite(id: JobSiteId): Unit =
        jobSiteDao.deleteJobSite(id.value)

    private fun Flow<List<JobSiteEntity>>.toDomainFlow(): Flow<List<JobSite>> =
        map { entities -> entities.map { it.toDomain() } }
}