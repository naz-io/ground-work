package com.nabadi.groundwork.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FieldNoteEntity::class,
        SiteEntity::class
    ],
    version = 2,
    exportSchema = true,
)
abstract class GroundWorkDatabase : RoomDatabase() {
    abstract fun fieldNoteDao(): FieldNoteDao

    abstract fun siteDao(): SiteDao
}
