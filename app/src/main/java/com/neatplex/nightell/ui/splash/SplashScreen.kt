package com.neatplex.nightell.ui.splash

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.navigation.MainDestinations
import com.neatplex.nightell.ui.theme.feelFree
import kotlinx.coroutines.delay

@SuppressLint("ResourceAsColor")
@Composable
fun SplashScreen(
    navController: NavController,
    hasToken: Boolean,
    animationDurationMillis: Int = 3000,
    gradientColors: List<Color> = listOf(Color(0xFF4Da1aB), Color(0xFF2D436C), Color(0xFFDA03BD))
) {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 1.5f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val gradientBrush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(100f, 100f),
        end = Offset(1000f * offsetX, 1000f * offsetX)
    )

    SplashScreenBackground(gradientBrush)
    SplashScreenContent()

    HandleSplashScreenNavigation(navController, hasToken)
}

@Composable
fun SplashScreenBackground(gradientBrush: Brush) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(brush = gradientBrush)
    }
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.nightell),
            fontFamily = feelFree,
            fontSize = 85.sp,
            color = Color.White
        )
    }
}

@Composable
fun HandleSplashScreenNavigation(navController: NavController, hasToken: Boolean) {
    LaunchedEffect(key1 = true) {
        delay(3000)
        navController.popBackStack()
        if (hasToken) {
            navController.navigate(MainDestinations.Main.route) {
                popUpTo(MainDestinations.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(MainDestinations.SignIn.route) {
                popUpTo(MainDestinations.Splash.route) { inclusive = true }
            }
        }
    }
}