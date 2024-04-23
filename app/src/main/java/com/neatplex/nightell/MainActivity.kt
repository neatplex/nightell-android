package com.neatplex.nightell

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.navigation.BottomNavHost
import com.neatplex.nightell.navigation.BottomNavigationScreen
import com.neatplex.nightell.navigation.Screens
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val tokenManager by lazy { TokenManager(applicationContext) }

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
                    BottomNavHost(navController = rootNavController, tokenManager = tokenManager)
                }
            }
        }
    }

}
