package com.nabadi.groundwork.domain.model

data class Note(
    val id: NoteId,
    val title: String,
    val body: String,
    val status: NoteStatus,
    val createdAt: Long,
    val updatedAt: Long,
)