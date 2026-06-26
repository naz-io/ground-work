package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.FieldNoteEntity
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.SiteId

fun FieldNoteEntity.toDomain(): FieldNote =
    FieldNote(
        id = FieldNoteId(id),
        siteId = siteId?.let(::SiteId),
        title = title,
        body = body,
        status = FieldNoteStatus.valueOf(status),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun FieldNote.toEntity(): FieldNoteEntity =
    FieldNoteEntity(
        id = id.value,
        siteId = siteId?.value,
        title = title,
        body = body,
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )