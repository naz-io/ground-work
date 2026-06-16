package com.nabadi.groundwork.di

import android.content.Context
import androidx.room.Room
import com.nabadi.groundwork.data.local.FieldNoteDao
import com.nabadi.groundwork.data.local.GroundWorkDatabase
import com.nabadi.groundwork.data.local.JobSiteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGroundWorkDatabase(
        @ApplicationContext context: Context,
    ): GroundWorkDatabase =
        Room.databaseBuilder(
            context = context,
            klass = GroundWorkDatabase::class.java,
            name = "groundwork.db"
        )
            .build()

    @Provides
    fun provideFieldNoteDao(
        database: GroundWorkDatabase
    ): FieldNoteDao =
        database.fieldNoteDao()

    @Provides
    fun provideJobSiteDao(
        database: GroundWorkDatabase
    ): JobSiteDao =
        database.jobSiteDao()
}


