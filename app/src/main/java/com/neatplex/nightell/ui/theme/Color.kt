package com.neatplex.nightell.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

/*
 * The "Midnight" palette: deep navy surfaces, a soft lavender primary and a warm
 * amber accent, like a night sky with a reading lamp. Every screen takes its
 * colours from MaterialTheme.colorScheme, never from these values directly.
 */

// Dark (the signature look)
internal val NightBackground = Color(0xFF0B1026)
internal val NightSurface = Color(0xFF111833)
internal val NightSurfaceContainerLowest = Color(0xFF080C1E)
internal val NightSurfaceContainerLow = Color(0xFF141C3C)
internal val NightSurfaceContainer = Color(0xFF19224A)
internal val NightSurfaceContainerHigh = Color(0xFF202A57)
internal val NightSurfaceContainerHighest = Color(0xFF283364)
internal val NightOnSurface = Color(0xFFE8E9F7)
internal val NightOnSurfaceVariant = Color(0xFFA7ACCD)
internal val NightOutline = Color(0xFF4A5384)
internal val NightOutlineVariant = Color(0xFF2B3466)

internal val Lavender = Color(0xFFB9ABFF)
internal val OnLavender = Color(0xFF221857)
internal val LavenderContainer = Color(0xFF3B3083)
internal val OnLavenderContainer = Color(0xFFE6E0FF)

internal val Amber = Color(0xFFFFC56E)
internal val OnAmber = Color(0xFF3D2700)
internal val AmberContainer = Color(0xFF5C3F00)
internal val OnAmberContainer = Color(0xFFFFE1B5)

internal val Moonlight = Color(0xFF8AD7E8)
internal val OnMoonlight = Color(0xFF00363F)
internal val MoonlightContainer = Color(0xFF0E4F5B)
internal val OnMoonlightContainer = Color(0xFFB4EEFA)

internal val NightError = Color(0xFFFF8E9E)
internal val OnNightError = Color(0xFF5C0016)
internal val NightErrorContainer = Color(0xFF7A1A2D)
internal val OnNightErrorContainer = Color(0xFFFFD9DD)

// Light (same family, for daytime)
internal val DayBackground = Color(0xFFF6F5FF)
internal val DaySurface = Color(0xFFFCFBFF)
internal val DaySurfaceContainerLowest = Color(0xFFFFFFFF)
internal val DaySurfaceContainerLow = Color(0xFFF2F0FC)
internal val DaySurfaceContainer = Color(0xFFECEAF9)
internal val DaySurfaceContainerHigh = Color(0xFFE6E3F6)
internal val DaySurfaceContainerHighest = Color(0xFFE0DCF2)
internal val DayOnSurface = Color(0xFF171A35)
internal val DayOnSurfaceVariant = Color(0xFF575C7E)
internal val DayOutline = Color(0xFF8C90B2)
internal val DayOutlineVariant = Color(0xFFCAC8E3)

internal val Indigo = Color(0xFF5646D2)
internal val OnIndigo = Color(0xFFFFFFFF)
internal val IndigoContainer = Color(0xFFE4DFFF)
internal val OnIndigoContainer = Color(0xFF1B1159)

internal val DeepAmber = Color(0xFF8A5600)
internal val OnDeepAmber = Color(0xFFFFFFFF)
internal val DeepAmberContainer = Color(0xFFFFDDAE)
internal val OnDeepAmberContainer = Color(0xFF2C1A00)

internal val DayTeal = Color(0xFF00687A)
internal val OnDayTeal = Color(0xFFFFFFFF)
internal val DayTealContainer = Color(0xFFB0ECFA)
internal val OnDayTealContainer = Color(0xFF001F26)

internal val DayError = Color(0xFFBA1A3A)
internal val OnDayError = Color(0xFFFFFFFF)
internal val DayErrorContainer = Color(0xFFFFDADD)
internal val OnDayErrorContainer = Color(0xFF40000F)

/** Colours of the night-sky backdrop used on splash and sign-in screens, in both modes. */
internal val SkyTop = Color(0xFF0B1026)
internal val SkyMiddle = Color(0xFF1E1A4D)
internal val SkyBottom = Color(0xFF3A2470)

/** Text and controls drawn on the night-sky backdrop. */
val OnSky = Color(0xFFF1EEFF)
val OnSkyMuted = Color(0xFFBDB6E6)
