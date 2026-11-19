package com.nabadi.groundwork.ui.format

import com.nabadi.groundwork.schedulerClock
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class MinuteTicksTest {

    @Test
    fun `emits the current time immediately and then on each minute boundary`() = runTest {
        val start = Instant.parse("2025-01-01T10:00:30Z").toEpochMilli()

        val ticks = schedulerClock(start).minuteTicks().take(3).toList()

        assertEquals(
            listOf(
                start, // 10:00:30, immediately
                start + 30_000L, // 10:01:00, the next boundary
                start + 90_000L, // 10:02:00
            ),
            ticks,
        )
    }

    @Test
    fun `a time exactly on a minute boundary waits a full minute`() = runTest {
        val start = Instant.parse("2025-01-01T10:00:00Z").toEpochMilli()

        val ticks = schedulerClock(start).minuteTicks().take(2).toList()

        assertEquals(listOf(start, start + 60_000L), ticks)
    }
}
