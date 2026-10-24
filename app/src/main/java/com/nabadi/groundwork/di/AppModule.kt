package com.nabadi.groundwork.di

import com.nabadi.groundwork.domain.IdGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import java.util.UUID

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideClock(): Clock = Clock.systemUTC()

    @Provides
    fun provideIdGenerator(): IdGenerator =
        IdGenerator { UUID.randomUUID().toString() }
}
