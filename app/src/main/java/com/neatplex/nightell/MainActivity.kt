package com.neatplex.nightell

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.navigation.BottomNavHost
import com.neatplex.nightell.navigation.BottomNavigationScreen
import com.neatplex.nightell.navigation.Screens
import com.neatplex.nightell.service.ServiceManager
import com.neatplex.nightell.ui.theme.AppTheme
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
            val scaffoldState = rememberScaffoldState()

            // Observe connectivity state
            val isOnline by connectionViewModel.isOnline.observeAsState(initial = true)

            // Observe token state
            val unauthorized by tokenManager.isRemovedToken.collectAsState()

            // Restart the activity if unauthorized
            if (unauthorized) {
                restartActivity()
                Toast.makeText(this, "Something is wrong! You need to login again", Toast.LENGTH_SHORT).show()
            }

            AppContent(
                tokenManager = tokenManager,
                mediaViewModel = mediaViewModel,
                serviceManager = serviceManager,
                scaffoldState = scaffoldState,
                isOnline = isOnline,
                sharedViewModel = sharedViewModel
            )
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppContent(
    tokenManager: TokenManager,
    mediaViewModel: MediaViewModel,
    serviceManager: ServiceManager,
    scaffoldState: ScaffoldState,
    isOnline: Boolean,
    sharedViewModel : SharedViewModel
) {


    AppTheme {
        val listItems = listOf(
            Screens.Home,
            Screens.Search,
            Screens.AddPost,
            Screens.Profile
        )
        val rootNavController = rememberNavController()
        var activeRoute by remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                scaffoldState = scaffoldState,
                bottomBar = {
                    BottomNavigationScreen(rootNavController, listItems, serviceManager, mediaViewModel, activeRoute, sharedViewModel = sharedViewModel)
                }
            ) {
                BottomNavHost(
                    navController = rootNavController,
                    tokenManager = tokenManager,
                    mediaViewModel = mediaViewModel,
                    serviceManager = serviceManager,
                    sharedViewModel = sharedViewModel,
                    onRouteChange = { route -> activeRoute = route }
                )
            }

            if (!isOnline) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.alert),
                            contentDescription = "internet connection",
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .size(42.dp))
                        Text(
                            text = "Check your internet connection!",
                            color = colorResource(id = R.color.purple_light),
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        }
    }
}
