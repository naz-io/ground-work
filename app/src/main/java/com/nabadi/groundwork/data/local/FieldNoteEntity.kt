package com.nabadi.groundwork.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "field_notes")
data class FieldNoteEntity(
    @PrimaryKey val id: String,
    val siteId: String?,
    val title: String,
    val body: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
)
