package com.neatplex.nightell.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.wear.compose.material.Text
import com.neatplex.nightell.R
import com.neatplex.nightell.component.media.PlayerBox
import com.neatplex.nightell.service.ServiceManager
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.SharedViewModel


sealed class Screens(
    val route: String,
    val icon: Int
) {
    data object Home : Screens("home", R.drawable.home)
    data object AddPost : Screens("addPost", R.drawable.add_post)
    data object Profile : Screens("profile", R.drawable.profile)
    data object Search : Screens("search", R.drawable.search)
}

object Routes {
    const val SPLASH = "splash"
    const val SIGN_IN = "signIn"
    const val SIGN_UP = "signUp"
}

@Composable
fun BottomNavigationScreen(navController: NavController,
                           items: List<Screens>,
                           serviceManager: ServiceManager,
                           mediaViewModel: MediaViewModel,
                           activeRoute: String,
                           sharedViewModel: SharedViewModel
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = navBackStackEntry?.destination?.route
    val isServiceRunning by serviceManager.isServiceRunning.collectAsState()
    val showBottomBar = currentScreen !in listOf(Routes.SIGN_IN, Routes.SIGN_UP, Routes.SPLASH)

    if (!showBottomBar) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Created by Neatplex",
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    } else {
        AppTheme {
            Box {
                // First, place the NavigationBar at the bottom of the screen.
                NavigationBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
                        .background(Color.White),
                    containerColor = Color.White
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = item.route == currentDestination?.route
                        val icon = painterResource(id = item.icon)
                        BottomNavigationItem(
                            modifier = Modifier.fillMaxHeight(),
                            icon = {
                                Icon(
                                    painter = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp),
                                    tint = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.5f)
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    popUpTo(item.route) {
                                        inclusive = true
                                    }
                                    restoreState = true
                                }
                            }
                        )
                    }
                }


                if (isServiceRunning) {
                    // Check if the current screen is showing the post being played
                    val isCurrentPostScreen = activeRoute.contains("postScreen") &&
                            sharedViewModel.post.value?.id == mediaViewModel.currentPostId.toInt()

                    // Display PlayerBox if not on the screen showing the currently running post
                    if (!isCurrentPostScreen) {
                        PlayerBox(
                            navController = navController,  // Pass the navController here
                            mediaViewModel = mediaViewModel,
                            sharedViewModel = sharedViewModel,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(vertical = 50.dp) // Adjust this padding to ensure it sits right above the NavigationBar
                        )
                    }
                }
            }
        }
    }
}
