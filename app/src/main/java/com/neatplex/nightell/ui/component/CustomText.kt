package com.neatplex.nightell.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun ErrorText(text: String){
    Text(
        text = text,
        color = Color.White,
        fontSize = 18.sp
    )
}