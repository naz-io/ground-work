package com.nabadi.groundwork.data.remote

import com.nabadi.groundwork.data.remote.model.RemoteFieldNote
import com.nabadi.groundwork.data.remote.model.RemoteFieldNoteStatus
import com.nabadi.groundwork.data.remote.model.RemoteSite
import com.nabadi.groundwork.data.remote.model.RemoteSitePriority
import com.nabadi.groundwork.data.remote.model.RemoteSiteStatus
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import java.time.Instant

// Domain -> wire only: v0.9 sync is push-only. Wire -> domain mapping arrives
// with pulling remote changes in v1.0.

internal fun Site.toRemote(): RemoteSite = RemoteSite(
    id = id.value,
    name = name,
    description = description,
    location = location,
    priority = priority.toRemote(),
    status = status.toRemote(),
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
)

internal fun FieldNote.toRemote(): RemoteFieldNote = RemoteFieldNote(
    id = id.value,
    siteId = siteId?.value,
    title = title,
    body = body,
    status = status.toRemote(),
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
)

internal fun SitePriority.toRemote(): RemoteSitePriority = when (this) {
    SitePriority.LOW -> RemoteSitePriority.LOW
    SitePriority.NORMAL -> RemoteSitePriority.NORMAL
    SitePriority.HIGH -> RemoteSitePriority.HIGH
}

internal fun SiteStatus.toRemote(): RemoteSiteStatus = when (this) {
    SiteStatus.ACTIVE -> RemoteSiteStatus.ACTIVE
    SiteStatus.ARCHIVED -> RemoteSiteStatus.ARCHIVED
}

internal fun FieldNoteStatus.toRemote(): RemoteFieldNoteStatus = when (this) {
    FieldNoteStatus.DRAFT -> RemoteFieldNoteStatus.DRAFT
    FieldNoteStatus.ACTIVE -> RemoteFieldNoteStatus.ACTIVE
    FieldNoteStatus.ARCHIVED -> RemoteFieldNoteStatus.ARCHIVED
}
