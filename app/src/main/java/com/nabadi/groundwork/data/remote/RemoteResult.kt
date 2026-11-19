package com.nabadi.groundwork.data.remote

/**
 * What the sync engine needs to know about a remote call: it worked, it may
 * work if retried, or the server refused it for good.
 */
internal sealed interface RemoteResult<out T> {
    data class Success<out T>(val value: T) : RemoteResult<T>

    /** Network failure, timeout, rate limit, or server error. Retry later; local data is fine. */
    data class Transient(val message: String) : RemoteResult<Nothing>

    /** The server permanently refused the request. Retrying unchanged data will not help. */
    data class Rejected(
        val status: Int,
        val code: String,
        val message: String,
    ) : RemoteResult<Nothing>
}
