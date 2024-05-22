package com.neatplex.nightell.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
    LaunchedEffect(key1 = true) {
        // Simulate a delay for checking token (you can replace this with your actual token check logic)
        delay(2000)

        // Remove the splash screen from the back stack
        navController.popBackStack(Routes.SPLASH, inclusive = true)
        navController.navigate(if (hasToken) Screens.Home.route else Routes.SIGN_IN)
    }

    // Splash screen UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blue_dark)),
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