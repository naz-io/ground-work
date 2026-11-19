package com.nabadi.groundwork.ui.format

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale

class AbsoluteDateTimeLabelTest {

    private val usUtcFormatter = absoluteDateTimeFormatter(Locale.US, ZoneOffset.UTC)

    @Test
    fun `formats a timestamp with the shared pattern`() {
        assertEquals("Nov 14, 2023 22:13", NOV_14_2023_22_13_UTC.absoluteDateTimeLabel(usUtcFormatter))
    }

    @Test
    fun `formats in the formatter's zone rather than the device zone`() {
        val tokyoFormatter = absoluteDateTimeFormatter(Locale.US, ZoneId.of("Asia/Tokyo"))

        assertEquals("Nov 15, 2023 07:13", NOV_14_2023_22_13_UTC.absoluteDateTimeLabel(tokyoFormatter))
    }

    @Test
    fun `formats month names in the formatter's locale`() {
        val germanFormatter = absoluteDateTimeFormatter(Locale.GERMANY, ZoneOffset.UTC)

        assertEquals("Nov. 14, 2023 22:13", NOV_14_2023_22_13_UTC.absoluteDateTimeLabel(germanFormatter))
    }

    @Test
    fun `returns an empty label for unset timestamps`() {
        assertEquals("", 0L.absoluteDateTimeLabel(usUtcFormatter))
        assertEquals("", (-1L).absoluteDateTimeLabel(usUtcFormatter))
    }

    private companion object {
        const val NOV_14_2023_22_13_UTC = 1_700_000_000_000L
    }
}
