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
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.upload.AddPostScreen
import com.neatplex.nightell.ui.profile.EditProfileScreen
import com.neatplex.nightell.ui.user.FollowerScreen
import com.neatplex.nightell.ui.user.FollowingScreen
import com.neatplex.nightell.ui.home.HomeScreen
import com.neatplex.nightell.ui.auth.SignInScreen
import com.neatplex.nightell.ui.profile.ProfileScreen
import com.neatplex.nightell.ui.auth.SignUpScreen
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
    startService: () -> Unit
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
            HomeNavHost(parentNavController = navController, sharedViewModel = sharedViewModel, mediaViewModel = mediaViewModel, startService = startService, tokenState)
        }

        composable(Screens.AddPost.route) {
            AddPostScreen(tokenState, navController,
                onLogout = {
                }
            )
        }

        composable(Screens.Profile.route) {
            ProfileNavHost(parentNavController = navController, sharedViewModel, mediaViewModel = mediaViewModel, startService = startService,tokenState)
        }

    }
}

@Composable
fun HomeNavHost(parentNavController: NavHostController, sharedViewModel: SharedViewModel, mediaViewModel: MediaViewModel, startService: () -> Unit, tokenState: String?){

    val homeNavController = rememberNavController()

    NavHost(navController = homeNavController, startDestination = "feed"){

        composable("feed") {
            HomeScreen(navController = homeNavController, sharedViewModel = sharedViewModel)
        }

        composable(
            route = "postScreen/{post}",
            arguments = listOf(navArgument("post") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val postString = backStackEntry.arguments?.getString("post") ?: ""
            val post = postString.fromJson(Post::class.java)
            PostScreen(parentNavController = parentNavController, navController = homeNavController, sharedViewModel = sharedViewModel, data = post, mediaViewModel = mediaViewModel,
                startService = startService)
        }

        composable("search") {
            SearchScreen(homeNavController,
                sharedViewModel = sharedViewModel)
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
fun ProfileNavHost(parentNavController: NavHostController, sharedViewModel: SharedViewModel, mediaViewModel: MediaViewModel, startService: () -> Unit, tokenState: String?){

    val profileNavController = rememberNavController()

    NavHost(navController = profileNavController, startDestination = "ProfileInfo"){
        composable("ProfileInfo") {
            ProfileScreen(navController = profileNavController, sharedViewModel = sharedViewModel)
        }

        composable(
            route = "postScreen/{post}",
            arguments = listOf(navArgument("post") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val postString = backStackEntry.arguments?.getString("post") ?: ""
            val post = postString.fromJson(Post::class.java)
            PostScreen(parentNavController = parentNavController, navController = profileNavController, sharedViewModel = sharedViewModel, data = post, mediaViewModel = mediaViewModel, startService = startService)
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
            EditProfileScreen(parentNavController = parentNavController, profileNavController, sharedViewModel = sharedViewModel)
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