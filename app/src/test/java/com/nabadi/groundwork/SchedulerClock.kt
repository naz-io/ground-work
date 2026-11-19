package com.nabadi.groundwork

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * A clock that advances with the test scheduler's virtual time, starting at
 * [startMillis]. Use it to test code that both reads a Clock and delays.
 */
fun TestScope.schedulerClock(startMillis: Long): Clock = SchedulerClock(this, startMillis)

private class SchedulerClock(
    private val testScope: TestScope,
    private val startMillis: Long,
) : Clock() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun instant(): Instant =
        Instant.ofEpochMilli(startMillis + testScope.testScheduler.currentTime)

    override fun getZone(): ZoneId = ZoneOffset.UTC

    override fun withZone(zone: ZoneId): Clock = this
}
