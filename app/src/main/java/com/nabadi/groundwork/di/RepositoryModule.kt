package com.nabadi.groundwork.di

import com.nabadi.groundwork.data.repository.OfflineFirstFieldNoteRepository
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
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
    abstract fun bindsFieldNoteRepository(
        repository: OfflineFirstFieldNoteRepository
    ): FieldNoteRepository
}