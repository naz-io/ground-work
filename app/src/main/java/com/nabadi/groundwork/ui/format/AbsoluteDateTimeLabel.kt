package com.nabadi.groundwork.ui.format

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable

internal fun Long.absoluteDateTimeLabel(): String {
    if (this <= 0L) {
        return ""
    }

    val formatter = remember {
        DateTimeFormatter
            .ofPattern("MMM d, yyyy HH:mm", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
    }
    return formatter.format(Instant.ofEpochMilli(this))
}