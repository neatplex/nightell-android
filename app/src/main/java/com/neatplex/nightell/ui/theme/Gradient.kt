package com.neatplex.nightell.ui.theme

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

/** The night-sky backdrop of the splash and sign-in screens (the same in light and dark mode). */
val NightSkyBrush: Brush = Brush.verticalGradient(listOf(SkyTop, SkyMiddle, SkyBottom))

/** Paints the night-sky backdrop. */
fun Modifier.nightSkyBackground(): Modifier = background(NightSkyBrush)
