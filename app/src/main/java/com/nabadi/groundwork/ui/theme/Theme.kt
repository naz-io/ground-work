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

    secondary = GroundWorkSecondary,
    onSecondary = GroundWorkOnSecondary,
    secondaryContainer = GroundWorkSecondaryContainer,
    onSecondaryContainer = GroundWorkOnSecondaryContainer,

    tertiary = GroundWorkTertiary,
    onTertiary = GroundWorkOnTertiary,
    tertiaryContainer = GroundWorkTertiaryContainer,
    onTertiaryContainer = GroundWorkOnTertiaryContainer,

    background = GroundWorkBackground,
    onBackground = GroundWorkOnBackground,

    surface = GroundWorkSurface,
    onSurface = GroundWorkOnSurface,
    surfaceVariant = GroundWorkNeutral,
    onSurfaceVariant = GroundWorkSecondary,

    outline = GroundWorkOutline,
    outlineVariant = GroundWorkOutlineVariant,

    error = GroundWorkError,
    onError = GroundWorkOnError,
    errorContainer = GroundWorkErrorContainer,
    onErrorContainer = GroundWorkOnErrorContainer,

    inverseSurface = GroundWorkSecondary,
    inverseOnSurface = GroundWorkNeutral,
)

private val GroundWorkDarkColorScheme = darkColorScheme(
    primary = GroundWorkPrimary,
    onPrimary = GroundWorkOnPrimaryContainer,
    primaryContainer = GroundWorkOnPrimaryContainer,
    onPrimaryContainer = GroundWorkPrimaryContainer,

    secondary = GroundWorkSecondaryContainer,
    onSecondary = GroundWorkSecondary,
    secondaryContainer = GroundWorkSecondary,
    onSecondaryContainer = GroundWorkSecondaryContainer,

    tertiary = GroundWorkTertiaryContainer,
    onTertiary = GroundWorkTertiary,
    tertiaryContainer = GroundWorkTertiary,
    onTertiaryContainer = GroundWorkTertiaryContainer,

    background = GroundWorkSecondary,
    onBackground = GroundWorkNeutral,

    surface = GroundWorkSecondary,
    onSurface = GroundWorkNeutral,
    surfaceVariant = GroundWorkCharcoal,
    onSurfaceVariant = GroundWorkNeutral,

    outline = GroundWorkOutlineVariant,
    outlineVariant = GroundWorkOutline,

    error = GroundWorkErrorContainer,
    onError = GroundWorkOnErrorContainer,
    errorContainer = GroundWorkError,
    onErrorContainer = GroundWorkErrorContainer,

    inverseSurface = GroundWorkNeutral,
    inverseOnSurface = GroundWorkSecondary,
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
