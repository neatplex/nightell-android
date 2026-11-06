package com.neatplex.nightell.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MidnightDark = darkColorScheme(
    primary = Lavender,
    onPrimary = OnLavender,
    primaryContainer = LavenderContainer,
    onPrimaryContainer = OnLavenderContainer,
    secondary = Amber,
    onSecondary = OnAmber,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = OnAmberContainer,
    tertiary = Moonlight,
    onTertiary = OnMoonlight,
    tertiaryContainer = MoonlightContainer,
    onTertiaryContainer = OnMoonlightContainer,
    error = NightError,
    onError = OnNightError,
    errorContainer = NightErrorContainer,
    onErrorContainer = OnNightErrorContainer,
    background = NightBackground,
    onBackground = NightOnSurface,
    surface = NightSurface,
    onSurface = NightOnSurface,
    surfaceVariant = NightSurfaceContainerHigh,
    onSurfaceVariant = NightOnSurfaceVariant,
    surfaceTint = Lavender,
    outline = NightOutline,
    outlineVariant = NightOutlineVariant,
    inverseSurface = NightOnSurface,
    inverseOnSurface = NightBackground,
    inversePrimary = Indigo,
    surfaceBright = NightSurfaceContainerHighest,
    surfaceDim = NightBackground,
    surfaceContainerLowest = NightSurfaceContainerLowest,
    surfaceContainerLow = NightSurfaceContainerLow,
    surfaceContainer = NightSurfaceContainer,
    surfaceContainerHigh = NightSurfaceContainerHigh,
    surfaceContainerHighest = NightSurfaceContainerHighest,
)

private val MidnightLight = lightColorScheme(
    primary = Indigo,
    onPrimary = OnIndigo,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = OnIndigoContainer,
    secondary = DeepAmber,
    onSecondary = OnDeepAmber,
    secondaryContainer = DeepAmberContainer,
    onSecondaryContainer = OnDeepAmberContainer,
    tertiary = DayTeal,
    onTertiary = OnDayTeal,
    tertiaryContainer = DayTealContainer,
    onTertiaryContainer = OnDayTealContainer,
    error = DayError,
    onError = OnDayError,
    errorContainer = DayErrorContainer,
    onErrorContainer = OnDayErrorContainer,
    background = DayBackground,
    onBackground = DayOnSurface,
    surface = DaySurface,
    onSurface = DayOnSurface,
    surfaceVariant = DaySurfaceContainerHigh,
    onSurfaceVariant = DayOnSurfaceVariant,
    surfaceTint = Indigo,
    outline = DayOutline,
    outlineVariant = DayOutlineVariant,
    inverseSurface = NightSurface,
    inverseOnSurface = NightOnSurface,
    inversePrimary = Lavender,
    surfaceBright = DaySurfaceContainerLowest,
    surfaceDim = DaySurfaceContainerHighest,
    surfaceContainerLowest = DaySurfaceContainerLowest,
    surfaceContainerLow = DaySurfaceContainerLow,
    surfaceContainer = DaySurfaceContainer,
    surfaceContainerHigh = DaySurfaceContainerHigh,
    surfaceContainerHighest = DaySurfaceContainerHighest,
)

/**
 * The app's Material 3 theme. Applied once at the root; screens read colours,
 * type and shapes from [MaterialTheme] instead of hard-coding them.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) MidnightDark else MidnightLight,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
