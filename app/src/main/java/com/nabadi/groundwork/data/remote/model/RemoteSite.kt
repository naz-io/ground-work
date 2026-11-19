package com.nabadi.groundwork.data.remote.model

import com.nabadi.groundwork.data.remote.Iso8601InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/** A site in the sync backend's wire format. Sync metadata is local and never sent. */
@Serializable
internal data class RemoteSite(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val priority: RemoteSitePriority,
    val status: RemoteSiteStatus,
    @SerialName("created_at")
    @Serializable(with = Iso8601InstantSerializer::class)
    val createdAt: Instant,
    @SerialName("updated_at")
    @Serializable(with = Iso8601InstantSerializer::class)
    val updatedAt: Instant,
)

@Serializable
internal enum class RemoteSitePriority {
    @SerialName("low") LOW,
    @SerialName("normal") NORMAL,
    @SerialName("high") HIGH,
}

@Serializable
internal enum class RemoteSiteStatus {
    @SerialName("active") ACTIVE,
    @SerialName("archived") ARCHIVED,
}
