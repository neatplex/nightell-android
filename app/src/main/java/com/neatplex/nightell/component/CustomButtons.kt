package com.neatplex.nightell.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.theme.myHorizontalGradiant

@Composable
fun CustomSimpleButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clip(RoundedCornerShape(4.dp)) // Set rounded corners to the Box
            .background(colorResource(id = R.color.blue))
            .clickable(onClick = onClick) // Make the whole Box clickable
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button,
            color = Color.White,
            textAlign = TextAlign.Center, // Center text within the Box
            modifier = Modifier
                .padding(4.dp) // Add padding to the Text
                .align(Alignment.Center) // Align the Text to the center of the Box
        )
    }
}

@Composable
fun CustomGradiantButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp)) // Set rounded corners to the Box
            .background(brush = myHorizontalGradiant())
            .clickable(onClick = onClick) // Make the whole Box clickable
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button,
            color = Color.White,
            textAlign = TextAlign.Center, // Center text within the Box
            modifier = Modifier
                .padding(16.dp) // Add padding to the Text
                .align(Alignment.Center) // Align the Text to the center of the Box
        )
    }
}

@Composable
fun CustomBorderedButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(width = 1.dp, color = Color.White, shape = CircleShape)
            .clickable(onClick = onClick) // Make the whole Box clickable
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button,
            color = Color.White,
            textAlign = TextAlign.Center, // Center text within the Box
            modifier = Modifier
                .padding(4.dp) // Add padding to the Text
                .align(Alignment.Center) // Align the Text to the center of the Box
        )
    }
}