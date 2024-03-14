package com.neatplex.nightell.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigatonItems(
    val route: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home : NavigatonItems("home", Icons.Outlined.Home, Icons.Filled.Home)
    data object AddStory : NavigatonItems("add_story", Icons.Outlined.Add, Icons.Filled.Add)
    data object Profile : NavigatonItems("profile", Icons.Outlined.Person, Icons.Filled.Person)
}