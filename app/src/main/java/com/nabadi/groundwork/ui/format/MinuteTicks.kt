package com.nabadi.groundwork.ui.format

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Clock
import kotlin.time.Duration.Companion.milliseconds

private const val MINUTE_MILLIS = 60_000L

/**
 * Emits the clock's current time immediately, then again at each minute
 * boundary. Minutes are the finest step relative time labels show, so this
 * keeps labels such as "5 min ago" current without per-second churn.
 */
internal fun Clock.minuteTicks(): Flow<Long> = flow {
    while (true) {
        val nowMillis = millis()
        emit(nowMillis)
        delay((MINUTE_MILLIS - nowMillis % MINUTE_MILLIS).milliseconds)
    }
}
