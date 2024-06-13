package com.neatplex.nightell.ui.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class BumpShape(private val bumpPosition: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            with(density) {
                val bumpWidth = 60.dp.toPx()
                val bumpHeight = 48.dp.toPx()
                val curveWidth = 20.dp.toPx()

                moveTo(0f, 0f)
                lineTo(bumpPosition - bumpWidth, 0f)
                cubicTo(
                    bumpPosition - curveWidth, 0f,
                    bumpPosition - curveWidth, bumpHeight,
                    bumpPosition, bumpHeight
                )
                cubicTo(
                    bumpPosition + curveWidth, bumpHeight,
                    bumpPosition + curveWidth, 0f,
                    bumpPosition + bumpWidth, 0f
                )
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
        }
        return Outline.Generic(path)
    }
}