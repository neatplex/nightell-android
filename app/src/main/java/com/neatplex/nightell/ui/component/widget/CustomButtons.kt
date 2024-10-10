package com.neatplex.nightell.ui.component.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neatplex.nightell.R

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
            .background(colorResource(id = R.color.night))
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
fun CustomBorderedCircleButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Black.copy(alpha = 0.1f), shape = CircleShape)
            .border(width = 1.dp, color = Color.White, shape = CircleShape)
            .clickable(onClick = onClick) // Make the whole Box clickable
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,// Center text within the Box
            modifier = Modifier
                .padding(4.dp) // Add padding to the Text
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
            .height(45.dp)
            .border(width = 1.dp, color = colorResource(id = R.color.night), shape = RoundedCornerShape(4.dp))
            .clickable(onClick = onClick) // Make the whole Box clickable
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button,
            color = colorResource(id = R.color.night),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,// Center text within the Box
            modifier = Modifier
                .padding(horizontal = 20.dp) // Add padding to the Text
                .align(Alignment.Center) // Align the Text to the center of the Box
        )
    }
}

@Composable
fun CustomLinkButton(
    text: String,
    onClick: () -> Unit,
    color: Color
){
    Box(
        modifier = Modifier
            .clickable(onClick = onClick) // Make the whole Box clickable
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.button,
                color = color,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.padding(4.dp)
            )
            Spacer(modifier = Modifier.width(8.dp)) // Add some space between the icon and the text
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward, // Replace with the actual icon you want to use
                contentDescription = null, // You can add a description for accessibility purposes
                tint = color,
                modifier = Modifier.size(24.dp) // Adjust the size of the icon if needed
            )
        }
    }
}