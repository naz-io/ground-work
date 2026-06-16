package com.nabadi.groundwork.di

import com.nabadi.groundwork.data.repository.OfflineFirstFieldNoteRepository
import com.nabadi.groundwork.data.repository.OfflineFirstJobSiteRepository
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import com.nabadi.groundwork.domain.repository.JobSiteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFieldNoteRepository(
        repository: OfflineFirstFieldNoteRepository,
    ): FieldNoteRepository

    @Binds
    @Singleton
    abstract fun bindJobSiteRepository(
        repository: OfflineFirstJobSiteRepository,
    ): JobSiteRepository
}