package com.neatplex.nightell.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.neatplex.nightell.R

sealed class NavigationItems(
    val route: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home : NavigationItems("home", Icons.Outlined.Home, Icons.Filled.Home)
    data object AddStory : NavigationItems("add_story", Icons.Outlined.Add, Icons.Filled.Add)
    data object Profile : NavigationItems("profile", Icons.Outlined.Person, Icons.Filled.Person)
}


@Composable
fun BottomNavigationBar(navController: NavController) {

    val items = listOf(
        NavigationItems.Home,
        NavigationItems.AddStory,
        NavigationItems.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination
    val route = navBackStackEntry?.destination?.route

    // Determine whether to show the bottom bar based on the current route
    val showBottomBar = route !in listOf("signIn", "signUp", "splash")

    if (showBottomBar) {
        BottomNavigation(
            backgroundColor = Color(0xFF1E275A),
            contentColor = Color.White
        ) {
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painter = rememberVectorPainter(image = item.selectedIcon),
                            contentDescription = null
                        )
                    },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.White.copy(alpha = 0.4f),
                    alwaysShowLabel = false,
                    selected = route == item.route,
                    onClick = {
                        if (route != item.route) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}
