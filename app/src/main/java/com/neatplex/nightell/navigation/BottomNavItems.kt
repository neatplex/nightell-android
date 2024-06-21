package com.neatplex.nightell.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.wear.compose.material.Text
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.theme.AppTheme


sealed class Screens(
    val route: String,
    val Icon: ImageVector
){
    object Home: Screens("home", Icons.Filled.Home)
    object AddPost: Screens("addPost", Icons.Filled.Add)
    object Profile: Screens("profile", Icons.Filled.Person)
}

object Routes {
    const val SPLASH = "splash"
    const val SIGN_IN = "signIn"
    const val SIGN_UP = "signUp"
}


@Composable
fun BottomNavigationScreen(navController: NavController, items: List<Screens>) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = navBackStackEntry?.destination?.route

    val showBottomBar = currentScreen !in listOf(Routes.SIGN_IN, Routes.SIGN_UP, Routes.SPLASH)

    if (!showBottomBar) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Powered by Neatplex",
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    } else {
        AppTheme {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .border(width = 1.dp, color = Color.LightGray)
                        .background(Color.White), // Use primary color from the theme
                    containerColor = Color.White// Ensure the background color is set
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = item.route == currentDestination?.route
                        BottomNavigationItem(
                            modifier = Modifier
                                .fillMaxHeight(),
                            icon = {
                                if (isSelected) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = if (isSelected) Modifier.background(Color.Transparent) else Modifier // Set the selected item background to transparent
                                    ) {
                                        Icon(
                                            painter = rememberVectorPainter(image = item.Icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp),
                                            tint = Color.Black
                                        )
                                    }
                                } else {
                                    Icon(
                                        painter = rememberVectorPainter(image = item.Icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        tint = Color.Black.copy(alpha = 0.5f)
                                    )
                                }
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
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screens.AddPost.route) {
                            launchSingleTop = true
                            popUpTo(Screens.AddPost.route) {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = -35.dp)
                        .size(70.dp)
                        .shadow(10.dp, shape = CircleShape)
                        .background(Color.Transparent),
                    contentColor = Color.White,
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.Black, CircleShape) // Custom background color
                    ) {
                        Icon(
                            painter = rememberVectorPainter(Icons.Filled.Add), // Replace with your icon
                            contentDescription = null,
                            modifier = Modifier
                                .size(42.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}