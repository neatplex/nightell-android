package com.neatplex.nightell

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.navigation.AppNavHost
import com.neatplex.nightell.service.ServiceManager
import com.neatplex.nightell.ui.viewmodel.ConnectivityViewModel
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var tokenManager: TokenManager
    @Inject lateinit var serviceManager: ServiceManager

    private val mediaViewModel: MediaViewModel by viewModels()
    private val connectionViewModel: ConnectivityViewModel by viewModels()
    private val sharedViewModel : SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            // Observe connectivity state
            val isOnline by connectionViewModel.isOnline.observeAsState(initial = true)

            // Observe token state
            val unauthorized by tokenManager.isRemovedToken.collectAsState()

            // Restart the activity if unauthorized
            if (unauthorized) {
                restartActivity()
                Toast.makeText(this, "Something is wrong! You need to login again", Toast.LENGTH_SHORT).show()
            }

            AppNavHost(
                navController = navController,
                tokenManager = tokenManager,
                mediaViewModel = mediaViewModel,
                serviceManager = serviceManager,
                isOnline = isOnline,
                sharedViewModel = sharedViewModel)

        }
    }

    private fun restartActivity() {
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceManager.stopMediaService()
    }
}
