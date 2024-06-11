package com.neatplex.nightell.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.navigation.Routes
import com.neatplex.nightell.navigation.Screens
import kotlinx.coroutines.delay

@SuppressLint("ResourceAsColor")
@Composable
fun SplashScreen(navController: NavController, hasToken: Boolean) {

    val infiniteTransition = rememberInfiniteTransition()

    // Animate the offset for the gradient
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val colors = listOf(Color(0xFF4Da1aB), Color(0xFF2D436C), Color(0xFFDA03BD))

    // Create the gradient brush
    val gradientBrush = Brush.linearGradient(
        colors = colors,
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1000f * offsetX, 1000f * offsetX)
    )

    // Draw the canvas with the animated gradient
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(brush = gradientBrush)
    }

    LaunchedEffect(key1 = true) {
        // Simulate a delay for checking token (you can replace this with your actual token check logic)
        delay(3000)

        // Remove the splash screen from the back stack
        navController.popBackStack(Routes.SPLASH, inclusive = true)
        navController.navigate(if (hasToken) Screens.Home.route else Routes.SIGN_IN)
    }

     //Splash screen UI
    Box(
        modifier = Modifier
            .fillMaxSize(),
            contentAlignment = Alignment.Center
    ) {
        // You can customize the splash screen UI here
        Image(
            painter = painterResource(id = R.drawable.nightell_white),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .scale(1.5f)
        )
    }
}