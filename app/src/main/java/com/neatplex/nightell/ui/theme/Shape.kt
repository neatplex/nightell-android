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
fun MyHorizontalGradiant(): Brush {
    return Brush.horizontalGradient(
        colors = listOf(
            colorResource(id = R.color.blue_light),
            colorResource(id = R.color.blue),
            colorResource(id = R.color.blue_dark),
        )
    )
}

@Composable
fun MyVerticalGradiant(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            colorResource(id = R.color.blue_light),
            colorResource(id = R.color.blue),
            colorResource(id = R.color.blue_dark),
            colorResource(id = R.color.purple)
        )
    )
}

@Composable
fun MySliderColors(): SliderColors {
    return SliderDefaults.colors(
        thumbColor = MaterialTheme.colors.secondary,
        activeTrackColor = MaterialTheme.colors.background ,
        inactiveTrackColor = MaterialTheme.colors.secondary,
    )
}
