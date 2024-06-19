package com.neatplex.nightell.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.neatplex.nightell.R

@Composable
fun myHorizontalGradiant(): Brush {
    return Brush.horizontalGradient(
        colors = listOf(
            colorResource(id = R.color.blue_light),
            colorResource(id = R.color.blue),
            colorResource(id = R.color.blue_dark),
        )
    )
}

@Composable
fun myLinearGradiant(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFF4Da1aB),
            Color(0xFF2D436C),
            Color(0xFFDA03BD)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}

@Composable
fun mySliderColors(): SliderColors {
    return SliderDefaults.colors(
        thumbColor = MaterialTheme.colors.secondary,
        activeTrackColor = MaterialTheme.colors.background ,
        inactiveTrackColor = MaterialTheme.colors.secondary,
    )
}
