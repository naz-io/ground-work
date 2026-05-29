package com.nabadi.groundwork.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GroundWorkLightColorScheme = lightColorScheme(
    primary = GroundWorkBurntOrange,
    onPrimary = GroundWorkSurface,
    primaryContainer = GroundWorkSafetyOrange,
    onPrimaryContainer = GroundWorkOrangeDark,

    secondary = GroundWorkSteel,
    onSecondary = GroundWorkSurface,
    secondaryContainer = GroundWorkSteelContainer,
    onSecondaryContainer = GroundWorkSteelDark,

    tertiary = GroundWorkCharcoal,
    onTertiary = GroundWorkSurface,
    tertiaryContainer = GroundWorkSurfaceHighest,
    onTertiaryContainer = GroundWorkInk,

    background = GroundWorkBackground,
    onBackground = GroundWorkInk,

    surface = GroundWorkSurface,
    onSurface = GroundWorkInk,
    surfaceVariant = GroundWorkSurfaceHighest,
    onSurfaceVariant = GroundWorkOutline,

    outline = GroundWorkOutline,
    outlineVariant = GroundWorkOutlineVariant,

    error = GroundWorkError,
    onError = GroundWorkSurface,
    errorContainer = GroundWorkErrorContainer,
    onErrorContainer = GroundWorkOnErrorContainer,

    inverseSurface = GroundWorkCharcoal,
    inverseOnSurface = GroundWorkInkInverse,
    inversePrimary = GroundWorkOrangeLight,

    surfaceTint = GroundWorkBurntOrange,
)

private val GroundWorkDarkColorScheme = darkColorScheme(
    primary = GroundWorkOrangeLight,
    onPrimary = GroundWorkOrangeDark,
    primaryContainer = GroundWorkBurntOrange,
    onPrimaryContainer = GroundWorkOrangeContainer,

    secondary = GroundWorkSteelContainer,
    onSecondary = GroundWorkInk,
    secondaryContainer = GroundWorkSteelDark,
    onSecondaryContainer = GroundWorkSteelContainer,

    tertiary = GroundWorkSurfaceHighest,
    onTertiary = GroundWorkInk,
    tertiaryContainer = GroundWorkCharcoal,
    onTertiaryContainer = GroundWorkSurfaceHighest,

    background = GroundWorkInk,
    onBackground = GroundWorkInkInverse,

    surface = GroundWorkCharcoal,
    onSurface = GroundWorkInkInverse,
    surfaceVariant = GroundWorkSteelDark,
    onSurfaceVariant = GroundWorkSteelContainer,

    outline = GroundWorkOutlineVariant,
    outlineVariant = GroundWorkOutline,

    error = GroundWorkErrorContainer,
    onError = GroundWorkOnErrorContainer,
    errorContainer = GroundWorkError,
    onErrorContainer = GroundWorkErrorContainer,

    inverseSurface = GroundWorkInkInverse,
    inverseOnSurface = GroundWorkInk,
    inversePrimary = GroundWorkBurntOrange,

    surfaceTint = GroundWorkOrangeLight,
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