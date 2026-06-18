package com.nabadi.groundwork.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sites")
data class SiteEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val location: String,
    val priority: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
)
