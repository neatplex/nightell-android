package com.neatplex.nightell.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.screens.auth.OtpVerificationScreen
import com.neatplex.nightell.ui.screens.auth.SignInScreen
import com.neatplex.nightell.ui.screens.auth.SignUpScreen
import com.neatplex.nightell.ui.screens.splash.SplashScreen
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import kotlinx.coroutines.delay

/** Top-level destinations: splash, auth, and the tabbed main screen. */
sealed class MainDestinations(val route: String) {
    data object Splash : MainDestinations("splash")
    data object SignIn : MainDestinations("sign_in")
    data object SignUp : MainDestinations("sign_up")
    data object Main : MainDestinations("main")
    data object Otp : MainDestinations("otp")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    mediaViewModel: MediaViewModel,
    isOnline: Boolean,
) {
    var showOfflineMessage by remember { mutableStateOf(false) }

    LaunchedEffect(isOnline) {
        // Debounce, so a brief network switch doesn't flash the offline screen.
        delay(OFFLINE_DEBOUNCE_MS)
        showOfflineMessage = !isOnline
    }

    if (showOfflineMessage) {
        OfflineMessage()
        return
    }

    NavHost(navController = navController, startDestination = MainDestinations.Splash.route) {
        composable(MainDestinations.Splash.route) {
            SplashScreen(navController, hasToken = sessionViewModel.isSignedIn)
        }
        composable(MainDestinations.SignIn.route) { SignInScreen(navController) }
        composable(MainDestinations.SignUp.route) { SignUpScreen(navController) }
        composable(MainDestinations.Otp.route) { OtpVerificationScreen(navController) }
        composable(MainDestinations.Main.route) {
            MainScreen(mediaViewModel = mediaViewModel, sessionViewModel = sessionViewModel)
        }
    }
}

@Composable
private fun OfflineMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.alert),
                contentDescription = "internet connection",
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .size(42.dp)
            )
            Text(
                text = "Check your internet connection!",
                color = colorResource(id = R.color.purple_light),
                fontSize = 18.sp,
            )
        }
    }
}

private const val OFFLINE_DEBOUNCE_MS = 100L
