package com.nabadi.groundwork.domain.model

data class FieldNote(
    val id: FieldNoteId,
    val title: String,
    val body: String,
    val status: FieldNoteStatus,
    val createdAt: Long,
    val updatedAt: Long,
)