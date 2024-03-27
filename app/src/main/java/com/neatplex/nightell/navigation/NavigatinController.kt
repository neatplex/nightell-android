package com.neatplex.nightell.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neatplex.nightell.ui.screens.AddPostScreen
import com.neatplex.nightell.ui.screens.EditProfileScreen
import com.neatplex.nightell.ui.screens.FollowerScreen
import com.neatplex.nightell.ui.screens.FollowingScreen
import com.neatplex.nightell.ui.screens.HomeScreen
import com.neatplex.nightell.ui.screens.SignInScreen
import com.neatplex.nightell.ui.screens.ProfileScreen
import com.neatplex.nightell.ui.screens.SignUpScreen
import com.neatplex.nightell.ui.screens.SplashScreen
import com.neatplex.nightell.ui.screens.PostScreen
import com.neatplex.nightell.ui.screens.SearchScreen
import com.neatplex.nightell.ui.screens.UserScreen
import com.neatplex.nightell.utils.TokenManager
import com.neatplex.nightell.ui.viewmodel.SharedViewModel


@Composable
fun Navigation(navController: NavHostController, tokenManager: TokenManager, sharedViewModel: SharedViewModel) {


    val hasToken = tokenManager.getToken() != null


    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController = navController, hasToken)
        }
        composable("signIn") {
            SignInScreen(navController = navController)
        }
        composable("signUp") {
            SignUpScreen(navController = navController)
        }
        composable(NavigatonItems.Home.route) {
            HomeScreen(navController = navController,sharedViewModel = sharedViewModel)
        }
        composable(NavigatonItems.AddStory.route) {
            AddPostScreen()
        }
        composable(NavigatonItems.Profile.route) {
            ProfileScreen(navController = navController,sharedViewModel = sharedViewModel)
        }
        composable("postScreen") {
            PostScreen(navController = navController, sharedViewModel)
        }
        composable("editProfile") {
            EditProfileScreen(navController, sharedViewModel = sharedViewModel)
        }
        composable("search") {
            SearchScreen(navController, sharedViewModel = sharedViewModel)
        }
        composable("followerScreen/{userId}",
            arguments = listOf(navArgument("userId"){
                type = NavType.IntType
            })
        ){ backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowerScreen(navController, userId, sharedViewModel = sharedViewModel)
        }
        composable("followingScreen/{userId}",
            arguments = listOf(navArgument("userId"){
                type = NavType.IntType
            })
        ){ backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowingScreen(navController, userId, sharedViewModel = sharedViewModel)
        }
        composable("userScreen/{userId}",
            arguments = listOf(navArgument("userId"){
                type = NavType.IntType
            })
        ){ backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            UserScreen(navController, userId, sharedViewModel = sharedViewModel)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigatonItems.Home,
        NavigatonItems.AddStory,
        NavigatonItems.Profile
    )

    val rootNavController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine whether to show the bottom bar based on the current route
    val showBottomBar = currentRoute !in listOf("signIn", "signUp", "splash")

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
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // No popUpTo configuration here
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}
