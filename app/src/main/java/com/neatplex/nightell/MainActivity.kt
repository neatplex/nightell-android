package com.neatplex.nightell

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.navigation.BottomNavHost
import com.neatplex.nightell.navigation.BottomNavigationScreen
import com.neatplex.nightell.navigation.Screens
import com.neatplex.nightell.service.SimpleMediaService
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val tokenManager by lazy { TokenManager(applicationContext) }
    private val mediaViewModel : MediaViewModel by viewModels()
    var isServiceRunning = false

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
                    BottomNavHost(navController = rootNavController, tokenManager = tokenManager, mediaViewModel = mediaViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, SimpleMediaService::class.java))
        isServiceRunning = false
    }

    private fun startService(){
        if(isServiceRunning){
            val intent = Intent(this, SimpleMediaService::class.java)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startForegroundService(intent)
            }
            isServiceRunning = true
        }
    }

}
