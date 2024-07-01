package com.neatplex.nightell.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.neatplex.nightell.R

@Composable
fun ErrorText(text: String){
    Text(
        text = text,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}