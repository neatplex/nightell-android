package com.neatplex.nightell.ui.component.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.R

@Composable
fun CustomCircularProgressIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White.copy(alpha = 0.3f))
    ) {
        CircularProgressIndicator(
            color = colorResource(id = R.color.night),
            strokeWidth = 4.dp,
            modifier = Modifier
                .size(70.dp)
                .padding(top = 20.dp)
        )
    }
}