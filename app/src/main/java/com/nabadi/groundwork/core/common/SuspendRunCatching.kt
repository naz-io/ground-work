package com.nabadi.groundwork.core.common

import kotlin.coroutines.cancellation.CancellationException

/**
 * [runCatching] for coroutine code: turns a failure into [Result.failure] but rethrows
 * [CancellationException], so cancelling the coroutine still cancels it instead of being
 * reported as a failed operation.
 *
 * Catches [Exception] rather than [Throwable], so errors such as `OutOfMemoryError` are not
 * swallowed either.
 */
inline fun <T> suspendRunCatching(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Result.failure(exception)
    }
