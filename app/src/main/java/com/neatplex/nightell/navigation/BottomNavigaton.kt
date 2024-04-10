package com.neatplex.nightell.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavigationItems(
    val route: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home : BottomNavigationItems("home", Icons.Outlined.Home, Icons.Filled.Home)
    data object AddStory : BottomNavigationItems("add_story", Icons.Outlined.Add, Icons.Filled.Add)
    data object Profile :
        BottomNavigationItems("profile", Icons.Outlined.Person, Icons.Filled.Person)
}


@Composable
fun BottomNavigationBar(navController: NavController) {

    val items = listOf(
        BottomNavigationItems.Home,
        BottomNavigationItems.AddStory,
        BottomNavigationItems.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine whether to show the bottom bar based on the current route
    val showBottomBar = currentRoute !in listOf("signIn", "signUp", "splash")


    if (showBottomBar) {

        NavigationBar {
            items.forEach { item ->
                val isSelected = item.route == navBackStackEntry?.destination?.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = if (isSelected) rememberVectorPainter(image = item.selectedIcon) else rememberVectorPainter(
                                image = item.unselectedIcon
                            ),
                            contentDescription = null
                        )
                    },
                    alwaysShowLabel = false,
                    selected = isSelected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

