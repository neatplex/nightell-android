package com.neatplex.nightell

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.navigation.BottomNavigationBar
import com.neatplex.nightell.navigation.Navigation
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.TokenManager
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val tokenManager by lazy { TokenManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            window.statusBarColor = getColor(R.color.black)
            MyApp()
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MyApp() {

        AppTheme {
            val navController = rememberNavController()
            val sharedViewModel: SharedViewModel = hiltViewModel()

            Scaffold(
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }
            ) {
                Navigation(navController = navController, tokenManager = tokenManager, sharedViewModel = sharedViewModel)
            }
        }
    }

}
