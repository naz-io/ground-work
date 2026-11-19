package com.nabadi.groundwork.data.remote

import java.io.IOException

/**
 * The HTTP-shaped boundary to the sync backend: JSON in, status and JSON out.
 *
 * The simulated backend implements this today; a real HTTP client (Ktor,
 * OkHttp) can implement it later without changing anything above it.
 */
internal interface RemoteTransport {
    /**
     * Sends [request] and returns the server's response, whatever its status.
     *
     * @throws IOException when the request could not reach the server or the
     * response was lost, as a real HTTP client does.
     */
    suspend fun execute(request: RemoteRequest): RemoteResponse
}

internal enum class RemoteMethod { PUT, DELETE }

internal data class RemoteRequest(
    val method: RemoteMethod,
    val path: String,
    val body: String? = null,
)

internal data class RemoteResponse(
    val status: Int,
    val body: String = "",
)

/** Endpoints of the sync backend. IDs are client-generated UUIDs, so they need no escaping. */
internal object RemotePaths {
    fun site(id: String): String = "/sites/$id"
    fun fieldNote(id: String): String = "/field-notes/$id"
}
