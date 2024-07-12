package com.neatplex.nightell

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.navigation.BottomNavHost
import com.neatplex.nightell.navigation.BottomNavigationScreen
import com.neatplex.nightell.navigation.Screens
import com.neatplex.nightell.service.ServiceManager
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var tokenManager: TokenManager
    @Inject lateinit var serviceManager: ServiceManager

    private val mediaViewModel: MediaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppContent(tokenManager, mediaViewModel, serviceManager)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        serviceManager.stopMediaService()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppContent(
    tokenManager: TokenManager,
    mediaViewModel: MediaViewModel,
    serviceManager: ServiceManager
) {
    AppTheme {
        val listItems = listOf(
            Screens.Home,
            Screens.Search,
            Screens.AddPost,
            Screens.Profile
        )
        val rootNavController = rememberNavController()

        Scaffold(
            bottomBar = {
                BottomNavigationScreen(rootNavController, listItems)
            }
        ) {
            BottomNavHost(
                navController = rootNavController,
                tokenManager = tokenManager,
                mediaViewModel = mediaViewModel,
                serviceManager = serviceManager
            )
        }
    }
}