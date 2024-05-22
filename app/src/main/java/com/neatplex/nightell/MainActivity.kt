package com.neatplex.nightell

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Scaffold
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.navigation.BottomNavHost
import com.neatplex.nightell.navigation.BottomNavigationScreen
import com.neatplex.nightell.navigation.Screens
import com.neatplex.nightell.service.MediaService
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.shared.MediaViewModel
import com.neatplex.nightell.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val tokenManager by lazy { TokenManager(applicationContext) }

    private val mediaViewModel : MediaViewModel by viewModels()
    private var isServiceRunning = false

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val listItems = listOf(
                Screens.Home,
                Screens.AddPost,
                Screens.Profile
            )

            window.statusBarColor = getColor(R.color.black)
            val rootNavController = rememberNavController()


            AppTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigationScreen(rootNavController,listItems)
                    }
                ) {
                    BottomNavHost(navController = rootNavController, tokenManager = tokenManager, mediaViewModel = mediaViewModel, startService = ::startService)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MediaService::class.java))
        isServiceRunning = false
    }

    private fun startService(){
        if(!isServiceRunning){
            val intent = Intent(this, MediaService::class.java)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isServiceRunning = true
        }
    }

}
