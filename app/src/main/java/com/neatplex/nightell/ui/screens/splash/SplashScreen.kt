package com.neatplex.nightell.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavController
import com.neatplex.nightell.ui.navigation.MainDestinations
import com.neatplex.nightell.ui.screens.auth.Header
import com.neatplex.nightell.ui.screens.auth.StarField
import com.neatplex.nightell.ui.theme.nightSkyBackground
import kotlinx.coroutines.delay


/** Shows the wordmark over the night sky, then goes to the feed or to sign-in. */
@Composable
fun SplashScreen(
    navController: NavController,
    hasToken: Boolean,
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "splash-fade",
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nightSkyBackground(),
        contentAlignment = Alignment.Center,
    ) {
        StarField()
        Box(modifier = Modifier.alpha(alpha)) {
            Header()
        }
    }

    HandleSplashScreenNavigation(navController, hasToken)
}

@Composable
fun HandleSplashScreenNavigation(navController: NavController, hasToken: Boolean) {
    LaunchedEffect(key1 = true) {
        delay(SPLASH_DURATION_MS)
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
private const val SPLASH_DURATION_MS = 1800L
