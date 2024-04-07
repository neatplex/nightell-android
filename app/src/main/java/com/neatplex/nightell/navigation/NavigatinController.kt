package com.neatplex.nightell.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
fun Navigation(
    navController: NavHostController,
    tokenManager: TokenManager,
    sharedViewModel: SharedViewModel
) {


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
        composable(NavigationItems.Home.route) {
            HomeScreen(navController = navController, sharedViewModel = sharedViewModel)
        }
        composable(NavigationItems.AddStory.route) {
            AddPostScreen()
        }
        composable(NavigationItems.Profile.route) {
            ProfileScreen(navController = navController, sharedViewModel = sharedViewModel)
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
        composable(
            "followerScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowerScreen(navController, userId, sharedViewModel = sharedViewModel)
        }
        composable(
            "followingScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowingScreen(navController, userId, sharedViewModel = sharedViewModel)
        }
        composable(
            "userScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            UserScreen(navController, userId, sharedViewModel = sharedViewModel)
        }
    }
}

