package com.nabadi.groundwork.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FieldNoteEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class GroundWorkDatabase : RoomDatabase() {
    abstract fun fieldNoteDao(): FieldNoteDao
}