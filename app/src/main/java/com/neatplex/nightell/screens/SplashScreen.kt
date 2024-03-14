package com.neatplex.nightell.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.neatplex.nightell.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, hasToken: Boolean) {
    LaunchedEffect(key1 = true) {
        // Simulate a delay for checking token (you can replace this with your actual token check logic)
        delay(2000)

        // Remove the splash screen from the back stack
        navController.popBackStack(route = "splash", inclusive = true)
        navController.navigate(if (hasToken) "home" else "signIn")
    }

    // Splash screen UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        // You can customize the splash screen UI here
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .scale(1.5f)
                .background(MaterialTheme.colors.primary)
        )
    }
}