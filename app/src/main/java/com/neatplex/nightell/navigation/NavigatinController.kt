package com.neatplex.nightell.navigation


import androidx.compose.runtime.Composable
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
import com.neatplex.nightell.ui.screens.HomeScreen
import com.neatplex.nightell.ui.auth.SignInScreen
import com.neatplex.nightell.ui.profile.ProfileScreen
import com.neatplex.nightell.ui.auth.SignUpScreen
import com.neatplex.nightell.ui.screens.SplashScreen
import com.neatplex.nightell.ui.post.PostScreen
import com.neatplex.nightell.ui.search.SearchScreen
import com.neatplex.nightell.ui.user.UserScreen
import com.neatplex.nightell.ui.shared.MediaViewModel
import com.neatplex.nightell.utils.TokenManager
import com.neatplex.nightell.ui.shared.SharedViewModel
import com.neatplex.nightell.utils.fromJson


@Composable
fun BottomNavHost(
    navController: NavHostController,
    tokenManager: TokenManager,
    mediaViewModel : MediaViewModel,
    startService: () -> Unit
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

        composable(Screens.Home.route) {
            HomeNavHost(sharedViewModel = sharedViewModel, mediaViewModel = mediaViewModel, startService = startService)
        }

        composable(Screens.AddPost.route) {
            AddPostScreen()
        }

        composable(Screens.Profile.route) {
            ProfileNavHost(sharedViewModel, mediaViewModel = mediaViewModel, startService = startService)
        }

    }
}

@Composable
fun HomeNavHost(sharedViewModel: SharedViewModel, mediaViewModel: MediaViewModel, startService: () -> Unit){

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
            PostScreen(navController = homeNavController, sharedViewModel = sharedViewModel, data = post, mediaViewModel = mediaViewModel,
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
fun ProfileNavHost(sharedViewModel: SharedViewModel, mediaViewModel: MediaViewModel, startService: () -> Unit){

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
            PostScreen(navController = profileNavController, sharedViewModel = sharedViewModel, data = post, mediaViewModel = mediaViewModel, startService = startService)
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

