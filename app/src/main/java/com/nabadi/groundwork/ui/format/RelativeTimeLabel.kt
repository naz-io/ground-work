package com.nabadi.groundwork.ui.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.nabadi.groundwork.R
import kotlin.math.max

@Composable
internal fun Long.relativeTimeLabel(nowMillis: Long = System.currentTimeMillis()): String {
    if (this <= 0L) {
        return stringResource(R.string.relative_time_unknown)
    }

    val elapsedMillis = max(nowMillis - this, 0L)
    val minuteMillis = 60_000L
    val hourMillis = 60 * minuteMillis
    val dayMillis = 24 * hourMillis
    val weekMillis = 7 * dayMillis
    val monthMillis = 30 * dayMillis
    val yearMillis = 365 * dayMillis

    return when {
        elapsedMillis < minuteMillis -> stringResource(R.string.relative_time_just_now)

        elapsedMillis < hourMillis -> {
            val minutes = (elapsedMillis / minuteMillis).toInt()
            pluralStringResource(R.plurals.relative_time_minutes_ago, minutes, minutes)
        }

        elapsedMillis < dayMillis -> {
            val hours = (elapsedMillis / hourMillis).toInt()
            pluralStringResource(R.plurals.relative_time_hours_ago, hours, hours)
        }

        elapsedMillis < weekMillis -> {
            val days = (elapsedMillis / dayMillis).toInt()
            pluralStringResource(R.plurals.relative_time_days_ago, days, days)
        }

        elapsedMillis < monthMillis -> {
            val weeks = (elapsedMillis / weekMillis).toInt()
            pluralStringResource(R.plurals.relative_time_weeks_ago, weeks, weeks)
        }

        elapsedMillis < yearMillis -> {
            val months = (elapsedMillis / monthMillis).toInt()
            pluralStringResource(R.plurals.relative_time_months_ago, months, months)
        }

        else -> {
            val years = (elapsedMillis / yearMillis).toInt()
            pluralStringResource(R.plurals.relative_time_years_ago, years, years)
        }
    }
}
