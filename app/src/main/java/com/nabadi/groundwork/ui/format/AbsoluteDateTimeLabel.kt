package com.nabadi.groundwork.ui.format

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLocale
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val ABSOLUTE_DATE_TIME_PATTERN = "MMM d, yyyy HH:mm"

/**
 * Remembers one formatter per screen. Call it once above a list and pass the
 * result to the rows, rather than creating a formatter in every row.
 */
@Composable
internal fun rememberAbsoluteDateTimeFormatter(): DateTimeFormatter {
    val locale = LocalLocale.current.platformLocale
    val zone = ZoneId.systemDefault()
    return remember(locale, zone) { absoluteDateTimeFormatter(locale, zone) }
}

internal fun absoluteDateTimeFormatter(
    locale: Locale,
    zone: ZoneId,
): DateTimeFormatter = DateTimeFormatter
    .ofPattern(ABSOLUTE_DATE_TIME_PATTERN, locale)
    .withZone(zone)

internal fun Long.absoluteDateTimeLabel(formatter: DateTimeFormatter): String {
    if (this <= 0L) {
        return ""
    }

    return formatter.format(Instant.ofEpochMilli(this))
}
