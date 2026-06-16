package com.nabadi.groundwork

import com.nabadi.groundwork.data.local.FieldNoteEntity
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.JobSiteId

object TestFieldNotes {

    fun fieldNote(
        id: String,
        jobSiteId: String? = "job-site-test",
        title: String = "Test Field Note",
        body: String = "This is a test field note.",
        status: FieldNoteStatus = FieldNoteStatus.DRAFT,
        createdAt: Long = 1L,
        updatedAt: Long = 2L,
    ): FieldNote = FieldNote(
        id = FieldNoteId(id),
        jobSiteId = jobSiteId?.let { JobSiteId(it) },
        title = title,
        body = body,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    fun fieldNoteEntity(
        id: String,
        jobsiteId: String? = "job-site-test",
        title: String = "Test Field Note",
        body: String = "This is a test field note.",
        status: String = "DRAFT",
        createdAt: Long = 1L,
        updatedAt: Long = 2L,
    ): FieldNoteEntity = FieldNoteEntity(
        id = id,
        jobSiteId = jobsiteId,
        title = title,
        body = body,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}