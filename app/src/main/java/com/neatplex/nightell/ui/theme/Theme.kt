package com.neatplex.nightell.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MyPrimaryColor,
    onPrimary = Color.White,
    primaryContainer = MyPrimaryDarkColor,
    onPrimaryContainer = Color.White,
    secondary = MySecondaryColor,
    onSecondary = Color.Black,
    background = MyBackgroundColor,
    onBackground = Color.Black,
    surface = Color.DarkGray,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = MyPrimaryColor,
    onPrimary = Color.White,
    primaryContainer = MyPrimaryLightColor,
    onPrimaryContainer = Color.Black,
    secondary = MySecondaryColor,
    onSecondary = Color.Black,
    background = MyBackgroundColor,
    onBackground = Color.Black,
    surface = Color.LightGray,
    onSurface = Color.Black,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}