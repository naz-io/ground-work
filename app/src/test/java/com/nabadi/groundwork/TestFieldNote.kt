package com.nabadi.groundwork

import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.JobSiteId

object TestFieldNote {
    fun fieldNote(
        id: String,
        jobSiteId: String? = "job-site-test",
        title: String = "Test Field Note",
        body: String = "This is a test field note.",
        status: FieldNoteStatus = FieldNoteStatus.ACTIVE,
        createdAt: Long = 0L,
        updatedAt: Long = 0L,
    ): FieldNote = FieldNote(
        id = FieldNoteId(id),
        jobSiteId = jobSiteId?.let { JobSiteId(it) },
        title = title,
        body = body,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}