package com.neatplex.nightell.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.service.ServiceManager
import com.neatplex.nightell.ui.upload.AddPostScreen
import com.neatplex.nightell.ui.profile.EditProfileScreen
import com.neatplex.nightell.ui.user.FollowerScreen
import com.neatplex.nightell.ui.user.FollowingScreen
import com.neatplex.nightell.ui.home.HomeScreen
import com.neatplex.nightell.ui.auth.SignInScreen
import com.neatplex.nightell.ui.profile.ProfileScreen
import com.neatplex.nightell.ui.auth.SignUpScreen
import com.neatplex.nightell.ui.bookmark.BookmarkedScreen
import com.neatplex.nightell.ui.splash.SplashScreen
import com.neatplex.nightell.ui.post.PostScreen
import com.neatplex.nightell.ui.search.SearchScreen
import com.neatplex.nightell.ui.user.UserScreen
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.utils.TokenManager
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.fromJson

@Composable
fun BottomNavHost(
    navController: NavHostController,
    tokenManager: TokenManager,
    mediaViewModel : MediaViewModel,
    serviceManager: ServiceManager
) {

    val tokenState by tokenManager.tokenState.collectAsState()
    val sharedViewModel : SharedViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController, hasToken = tokenState != null)
        }

        composable("signIn") {
            SignInScreen(navController = navController)
        }

        composable("signUp") {
            SignUpScreen(navController = navController)
        }

        composable(Screens.Home.route) {
            HomeNavHost(sharedViewModel = sharedViewModel, mediaViewModel = mediaViewModel, serviceManager = serviceManager)
        }

        composable(Screens.AddPost.route) {
            AddPostScreen(tokenState, navController,
                onLogout = {
                }
            )
        }

        composable(Screens.Search.route) {
            SearchNavHost(sharedViewModel = sharedViewModel, mediaViewModel = mediaViewModel, serviceManager = serviceManager)
        }

        composable(Screens.Profile.route) {
            ProfileNavHost(sharedViewModel, mediaViewModel = mediaViewModel, serviceManager = serviceManager)
        }

    }
}

@Composable
fun HomeNavHost(sharedViewModel: SharedViewModel, mediaViewModel: MediaViewModel, serviceManager: ServiceManager){

    val homeNavController = rememberNavController()

    NavHost(navController = homeNavController, startDestination = "feed"){

        composable("feed") {
            HomeScreen(navController = homeNavController, sharedViewModel = sharedViewModel)
        }

        composable(
            "userScreen/{user}",
            arguments = listOf(navArgument("user") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userString = backStackEntry.arguments?.getString("user") ?: ""
            val user = userString.fromJson(User::class.java)
            UserScreen(navController = homeNavController, data = user, sharedViewModel = sharedViewModel)
        }

        composable(
            route = "postScreen/{postId}",
            arguments = listOf(navArgument("postId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: -1
            PostScreen(navController = homeNavController, sharedViewModel = sharedViewModel, postId = postId, mediaViewModel = mediaViewModel,
                startService = serviceManager::startMediaService)
        }

        composable(
            "followerScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowerScreen(navController = homeNavController, userId, sharedViewModel = sharedViewModel)
        }

        composable(
            "followingScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowingScreen(navController = homeNavController, userId, sharedViewModel = sharedViewModel)
        }

        composable("bookmark") {
            BookmarkedScreen(navController = homeNavController)
        }
    }
}

@Composable
fun SearchNavHost(sharedViewModel: SharedViewModel, mediaViewModel: MediaViewModel, serviceManager: ServiceManager){

    val searchNavController = rememberNavController()

    NavHost(navController = searchNavController, startDestination = "search"){

        composable("search") {
            SearchScreen(navController = searchNavController, sharedViewModel = sharedViewModel)
        }

        composable(
            route = "postScreen/{postId}",
            arguments = listOf(navArgument("postId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: -1
            PostScreen(navController = searchNavController, sharedViewModel = sharedViewModel, postId = postId, mediaViewModel = mediaViewModel,
                startService = serviceManager::startMediaService)
        }

        composable(
            "userScreen/{user}",
            arguments = listOf(navArgument("user") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userString = backStackEntry.arguments?.getString("user") ?: ""
            val user = userString.fromJson(User::class.java)
            UserScreen(navController = searchNavController, data = user, sharedViewModel = sharedViewModel)
        }

        composable(
            "followerScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowerScreen(navController = searchNavController, userId, sharedViewModel = sharedViewModel)
        }

        composable(
            "followingScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowingScreen(navController = searchNavController, userId, sharedViewModel = sharedViewModel)
        }
    }
}

@Composable
fun ProfileNavHost(sharedViewModel: SharedViewModel, mediaViewModel: MediaViewModel, serviceManager: ServiceManager){

    val profileNavController = rememberNavController()

    NavHost(navController = profileNavController, startDestination = "ProfileInfo"){
        composable("ProfileInfo") {
            ProfileScreen(navController = profileNavController, sharedViewModel = sharedViewModel)
        }

        composable(
            route = "postScreen/{postId}",
            arguments = listOf(navArgument("postId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: -1
            PostScreen(navController = profileNavController, sharedViewModel = sharedViewModel, postId = postId, mediaViewModel = mediaViewModel,
                startService = serviceManager::startMediaService)
        }

        composable(
            "userScreen/{user}",
            arguments = listOf(navArgument("user") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userString = backStackEntry.arguments?.getString("user") ?: ""
            val user = userString.fromJson(User::class.java)
            UserScreen(navController = profileNavController, data = user, sharedViewModel = sharedViewModel)
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