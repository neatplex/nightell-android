package com.neatplex.nightell.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.R

@Composable
fun CustomPinkButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(), // Modify the button's width to fill its parent
        shape = RoundedCornerShape(4.dp), // Apply rounded corners to the button
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.purple_light).copy(alpha = 0.3f), contentColor = Color.White), // Customize button colors
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.button // Apply button text style
            )
        }
    )
}