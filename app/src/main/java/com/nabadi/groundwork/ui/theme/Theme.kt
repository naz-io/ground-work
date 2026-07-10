package com.nabadi.groundwork.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GroundWorkLightColorScheme = lightColorScheme(
    primary = GroundWorkPrimary,
    onPrimary = GroundWorkOnPrimary,
    primaryContainer = GroundWorkPrimaryContainer,
    onPrimaryContainer = GroundWorkOnPrimaryContainer,

    surface = GroundWorkSurface,
    onSurface = GroundWorkOnSurface,
    surfaceVariant = GroundWorkSurfaceContainer,
    onSurfaceVariant = GroundWorkOnSurfaceVariant,

    outline = GroundWorkOutline,
    outlineVariant = GroundWorkOutlineVariant,

    error = GroundWorkError,
    onError = GroundWorkOnError,

    background = GroundWorkSurfaceContainerLowest,
    onBackground = GroundWorkOnSurface,
)

private val GroundWorkDarkColorScheme = darkColorScheme(
    primary = GroundWorkPrimary,
    onPrimary = GroundWorkOnPrimaryContainer,
    primaryContainer = GroundWorkOnPrimaryContainer,
    onPrimaryContainer = GroundWorkPrimaryContainer,

    surface = GroundWorkOnSurface,
    onSurface = GroundWorkSurface,
    surfaceVariant = GroundWorkOnSurfaceVariant,
    onSurfaceVariant = GroundWorkSurfaceContainer,

    outline = GroundWorkOutlineVariant,
    outlineVariant = GroundWorkOutline,

    error = GroundWorkError,
    onError = GroundWorkOnError,

    background = GroundWorkOnSurface,
    onBackground = GroundWorkSurface,
)

@Composable
fun GroundWorkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> GroundWorkDarkColorScheme
        else -> GroundWorkLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
