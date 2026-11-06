package com.neatplex.nightell

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.ui.navigation.AppNavHost
import com.neatplex.nightell.ui.viewmodel.ConnectivityViewModel
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mediaViewModel: MediaViewModel by viewModels()
    private val connectivityViewModel: ConnectivityViewModel by viewModels()
    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val isOnline by connectivityViewModel.isOnline.collectAsStateWithLifecycle()
            val sessionExpired by sessionViewModel.sessionExpired.collectAsStateWithLifecycle()

            // The server rejected the token: restart so the user signs in again.
            LaunchedEffect(sessionExpired) {
                if (sessionExpired) {
                    sessionViewModel.onSessionExpiredHandled()
                    Toast.makeText(
                        this@MainActivity,
                        "Something is wrong! You need to login again",
                        Toast.LENGTH_SHORT
                    ).show()
                    restartActivity()
                }
            }

            AppNavHost(
                navController = navController,
                sessionViewModel = sessionViewModel,
                mediaViewModel = mediaViewModel,
                isOnline = isOnline,
            )
        }
    }

    private fun restartActivity() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        // onDestroy also runs on rotation; only stop playback when the activity is really closing.
        if (isFinishing) mediaViewModel.stop()
    }
}
