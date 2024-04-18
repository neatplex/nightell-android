package com.neatplex.nightell.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neatplex.nightell.ui.screens.AddPostScreen
import com.neatplex.nightell.ui.screens.EditProfileScreen
import com.neatplex.nightell.ui.screens.FollowerScreen
import com.neatplex.nightell.ui.screens.FollowingScreen
import com.neatplex.nightell.ui.screens.FeedScreen
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
    tokenManager: TokenManager
) {


    val hasToken = tokenManager.getToken() != null
    val sharedViewModel : SharedViewModel = hiltViewModel()


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
        composable("home") {
            HomeNavHost(sharedViewModel)
        }
        composable("addPost") {
            AddPostScreen()
        }
        composable("profile") {
            ProfileNavHost(sharedViewModel)
        }
    }
}

@Composable
fun HomeNavHost(sharedViewModel: SharedViewModel){

    val homeNavController = rememberNavController()

    NavHost(navController = homeNavController, startDestination = "feed"){

        composable("feed") {
            FeedScreen(navController = homeNavController, sharedViewModel = sharedViewModel)
        }

        composable("postScreen") {
            PostScreen(navController = homeNavController, sharedViewModel)
        }

        composable("search") {
            SearchScreen(homeNavController, sharedViewModel = sharedViewModel)
        }

        composable(
            "userScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            UserScreen(homeNavController, userId, sharedViewModel = sharedViewModel)
        }

    }
}

@Composable
fun ProfileNavHost(sharedViewModel: SharedViewModel){

    val profileNavController = rememberNavController()

    NavHost(navController = profileNavController, startDestination = "ProfileInfo"){

        composable("ProfileInfo") {
            ProfileScreen(navController = profileNavController, sharedViewModel = sharedViewModel)
        }

        composable("postScreen") {
            PostScreen(navController = profileNavController, sharedViewModel)
        }

        composable(
            "userScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            UserScreen(profileNavController, userId, sharedViewModel = sharedViewModel)
        }

        composable("editProfile") {
            EditProfileScreen(profileNavController, sharedViewModel = sharedViewModel)
        }

        composable(
            "followerScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowerScreen(profileNavController, userId, sharedViewModel = sharedViewModel)
        }
        composable(
            "followingScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowingScreen(profileNavController, userId, sharedViewModel = sharedViewModel)
        }
    }
}

