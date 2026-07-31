package com.nabadi.groundwork

import com.nabadi.groundwork.data.local.FieldNoteEntity
import com.nabadi.groundwork.data.local.SyncMetadataEntity
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SyncMetadata

object TestFieldNotes {

    fun fieldNote(
        id: String,
        siteId: String? = null,
        title: String = "Test Field Note",
        body: String = "This is a test field note.",
        status: FieldNoteStatus = FieldNoteStatus.DRAFT,
        createdAt: Long = 1L,
        updatedAt: Long = 2L,
        syncMetadata: SyncMetadata = SyncMetadata(),
    ): FieldNote = FieldNote(
        id = FieldNoteId(id),
        siteId = siteId?.let { SiteId(it) },
        title = title,
        body = body,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        syncMetadata = syncMetadata,
    )

    fun fieldNoteEntity(
        id: String,
        siteId: String? = null,
        title: String = "Test Field Note",
        body: String = "This is a test field note.",
        status: String = "DRAFT",
        createdAt: Long = 1_000L,
        updatedAt: Long = 2_000L,
        syncMetadata: SyncMetadataEntity = SyncMetadataEntity(),
    ): FieldNoteEntity = FieldNoteEntity(
        id = id,
        siteId = siteId,
        title = title,
        body = body,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        syncMetadata = syncMetadata,
    )
}
