package com.nabadi.groundwork.data.remote.model

import kotlinx.serialization.Serializable

/** Success body for a write: the record's id and its new server revision. */
@Serializable
internal data class RemoteAck(
    val id: String,
    val revision: Long,
)

/** Error body for a rejected request: `{ "error": { "code": ..., "message": ... } }`. */
@Serializable
internal data class RemoteErrorResponse(
    val error: RemoteErrorDetail,
)

@Serializable
internal data class RemoteErrorDetail(
    val code: String,
    val message: String,
)
