package com.nabadi.groundwork

import com.nabadi.groundwork.data.local.FieldNoteEntity
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus

object TestFieldNotes {

    fun fieldNote(
        id: String,
        title: String = "Test Field Note",
        body: String = "This is a test field note.",
        status: FieldNoteStatus = FieldNoteStatus.DRAFT,
        createdAt: Long = 1L,
        updatedAt: Long = 2L,
    ): FieldNote = FieldNote(
        id = FieldNoteId(id),
        title = title,
        body = body,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    fun fieldNoteEntity(
        id: String,
        title: String = "Test Field Note",
        body: String = "This is a test field note.",
        status: String = "DRAFT",
        createdAt: Long = 1L,
        updatedAt: Long = 2L,
    ): FieldNoteEntity = FieldNoteEntity(
        id = id,
        title = title,
        body = body,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}