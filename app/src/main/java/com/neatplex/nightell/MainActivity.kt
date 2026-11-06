package com.neatplex.nightell

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.ui.navigation.AppNavHost
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.viewmodel.ConnectivityViewModel
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import com.neatplex.nightell.ui.viewmodel.SignOutReason
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mediaViewModel: MediaViewModel by viewModels()
    private val connectivityViewModel: ConnectivityViewModel by viewModels()
    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Draw behind the system bars; their icons follow the light/dark setting.
        enableEdgeToEdge()

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val isOnline by connectivityViewModel.isOnline.collectAsStateWithLifecycle()
                val signOutReason by sessionViewModel.signOutReason.collectAsStateWithLifecycle()

                // However the user stopped being signed in (sign out, account deleted,
                // token rejected), start over on a fresh task so no screen, draft or
                // playback from the old session survives.
                LaunchedEffect(signOutReason) {
                    val reason = signOutReason ?: return@LaunchedEffect
                    if (reason == SignOutReason.SESSION_EXPIRED) {
                        sessionViewModel.onSessionExpiredHandled()
                        Toast.makeText(
                            this@MainActivity,
                            "Something is wrong! You need to login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    restartActivity()
                }

                AppNavHost(
                    navController = navController,
                    sessionViewModel = sessionViewModel,
                    mediaViewModel = mediaViewModel,
                    isOnline = isOnline,
                )
            }
        }
    }

    private fun restartActivity() {
        val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // onDestroy also runs on rotation; only stop playback when the activity is really closing.
        if (isFinishing) mediaViewModel.stop()
    }
}
