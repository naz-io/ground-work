package com.nabadi.groundwork.data.remote.model

import com.nabadi.groundwork.data.remote.Iso8601InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/** A field note in the sync backend's wire format. Sync metadata is local and never sent. */
@Serializable
internal data class RemoteFieldNote(
    val id: String,
    @SerialName("site_id")
    val siteId: String?,
    val title: String,
    val body: String,
    val status: RemoteFieldNoteStatus,
    @SerialName("created_at")
    @Serializable(with = Iso8601InstantSerializer::class)
    val createdAt: Instant,
    @SerialName("updated_at")
    @Serializable(with = Iso8601InstantSerializer::class)
    val updatedAt: Instant,
)

@Serializable
internal enum class RemoteFieldNoteStatus {
    @SerialName("draft") DRAFT,
    @SerialName("active") ACTIVE,
    @SerialName("archived") ARCHIVED,
}
