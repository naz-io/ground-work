package com.nabadi.groundwork.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "field_notes",
    foreignKeys = [
        ForeignKey(
            entity = SiteEntity::class,
            parentColumns = ["id"],
            childColumns = ["siteId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("siteId")],
)
data class FieldNoteEntity(
    @PrimaryKey val id: String,
    val siteId: String?,
    val title: String,
    val body: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
)
