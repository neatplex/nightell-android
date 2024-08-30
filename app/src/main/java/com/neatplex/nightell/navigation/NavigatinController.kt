package com.neatplex.nightell.navigation

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neatplex.nightell.R
import com.neatplex.nightell.component.media.PlayerBox
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
fun AppNavHost(
    navController: NavHostController,
    tokenManager: TokenManager,
    mediaViewModel: MediaViewModel,
    serviceManager: ServiceManager,
    isOnline: Boolean,
    sharedViewModel: SharedViewModel
) {

    NavHost(
        navController = navController,
        startDestination = MainDestinations.Splash.route
    ) {

        composable(MainDestinations.Splash.route) {
            SplashScreen(
                navController,
                hasToken = tokenManager.getToken() != null
            )
        }
        composable(MainDestinations.SignIn.route) { SignInScreen(navController) }
        composable(MainDestinations.SignUp.route) { SignUpScreen(navController) }
        composable(MainDestinations.Main.route) {
            MainScreen(
                mediaViewModel = mediaViewModel,
                serviceManager = serviceManager,
                sharedViewModel = sharedViewModel
            )
        }
    }

    if (!isOnline) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.alert),
                    contentDescription = "internet connection",
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(42.dp)
                )
                Text(
                    text = "Check your internet connection!",
                    color = colorResource(id = R.color.purple_light),
                    fontSize = 18.sp,
                )
            }
        }
    }
}

sealed class BottomNavScreens(
    val route: String,
    val icon: Int
) {
    data object Home : BottomNavScreens("home", R.drawable.home)
    data object AddPost : BottomNavScreens("addPost", R.drawable.add_post)
    data object Profile : BottomNavScreens("profile", R.drawable.profile)
    data object Search : BottomNavScreens("search", R.drawable.search)
}

sealed class MainDestinations(val route: String) {
    object Splash : MainDestinations("splash")
    object SignIn : MainDestinations("sign_in")
    object SignUp : MainDestinations("sign_up")
    object Main : MainDestinations("main")
}

@Composable
fun MainScreen(
    mediaViewModel: MediaViewModel,
    serviceManager: ServiceManager,
    sharedViewModel: SharedViewModel
) {
    val selectedTab = rememberSaveable { mutableStateOf(BottomNavScreens.Home.route) }
    val tabStack = remember { mutableStateListOf(BottomNavScreens.Home.route) }

    // Separate NavHostControllers for each tab
    val homeNavController = rememberNavController()
    val searchNavController = rememberNavController()
    val addPostNavController = rememberNavController()
    val profileNavController = rememberNavController()

    val isServiceRunning by serviceManager.isServiceRunning.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    .background(Color.White),
                containerColor = Color.White
            ) {
                BottomNavigationItem(
                    selected = selectedTab.value == BottomNavScreens.Home.route,
                    onClick = {
                        handleTabClick(BottomNavScreens.Home.route, selectedTab, tabStack)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = BottomNavScreens.Home.icon),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = if (selectedTab.value == BottomNavScreens.Home.route) Color.Black else Color.Black.copy(
                                alpha = 0.5f
                            )
                        )
                    }
                )
                BottomNavigationItem(
                    selected = selectedTab.value == BottomNavScreens.Search.route,
                    onClick = {
                        handleTabClick(BottomNavScreens.Search.route, selectedTab, tabStack)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = BottomNavScreens.Search.icon),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = if (selectedTab.value == BottomNavScreens.Search.route) Color.Black else Color.Black.copy(
                                alpha = 0.5f
                            )
                        )
                    }
                )
                BottomNavigationItem(
                    selected = selectedTab.value == BottomNavScreens.AddPost.route,
                    onClick = {
                        handleTabClick(BottomNavScreens.AddPost.route, selectedTab, tabStack)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = BottomNavScreens.AddPost.icon),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = if (selectedTab.value == BottomNavScreens.AddPost.route) Color.Black else Color.Black.copy(
                                alpha = 0.5f
                            )
                        )
                    }
                )
                BottomNavigationItem(
                    selected = selectedTab.value == BottomNavScreens.Profile.route,
                    onClick = {
                        Log.d("profile", profileNavController.toString())
                        handleTabClick(BottomNavScreens.Profile.route, selectedTab, tabStack)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = BottomNavScreens.Profile.icon),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = if (selectedTab.value == BottomNavScreens.Profile.route) Color.Black else Color.Black.copy(
                                alpha = 0.5f
                            )
                        )
                    }
                )
            }
            if (isServiceRunning) {
                PlayerBox(
                    navController = homeNavController,
                    mediaViewModel = mediaViewModel,
                    sharedViewModel = sharedViewModel,
                    modifier = Modifier.padding(vertical = 50.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab.value) {
                BottomNavScreens.Home.route -> HomeNavHost(
                    homeNavController,
                    sharedViewModel,
                    mediaViewModel,
                    serviceManager
                )

                BottomNavScreens.Search.route -> SearchNavHost(
                    searchNavController,
                    sharedViewModel,
                    mediaViewModel,
                    serviceManager
                )

                BottomNavScreens.AddPost.route -> AddPostNavHost(addPostNavController)
                BottomNavScreens.Profile.route -> ProfileNavHost(
                    profileNavController,
                    sharedViewModel,
                    mediaViewModel,
                    serviceManager
                )
            }
        }
    }

    // Handle back press
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    DisposableEffect(Unit) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentController = when (selectedTab.value) {
                    BottomNavScreens.Home.route -> homeNavController
                    BottomNavScreens.Search.route -> searchNavController
                    BottomNavScreens.AddPost.route -> addPostNavController
                    BottomNavScreens.Profile.route -> profileNavController
                    else -> homeNavController
                }

                // Check if the current nav controller is at the start destination
                val isAtStartDestination =
                    currentController.currentDestination?.route == currentController.graph.startDestinationRoute

                if (isAtStartDestination) {
                    if (tabStack.size > 1) {
                        // If more tabs exist in the stack, switch to the previous tab
                        tabStack.removeLastOrNull()
                        selectedTab.value = tabStack.last()
                    } else {
                        // No more tabs in the stack, close the app
                        backPressedDispatcher?.onBackPressed()
                    }
                } else {
                    // Navigate back within the current tab's stack
                    currentController.popBackStack()
                }
            }
        }
        backPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }
}

    private fun handleTabClick(
    tabRoute: String,
    selectedTab: MutableState<String>,
    tabStack: SnapshotStateList<String>
) {
    if (selectedTab.value != tabRoute) {
        // If the tab already exists in the stack, remove it
        if (tabRoute in tabStack) {
            tabStack.remove(tabRoute)
        }
        // Add the tab to the top of the stack
        tabStack.add(tabRoute)
        // Update the selected tab
        selectedTab.value = tabRoute
    }
}

@Composable
fun AddPostNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "addPost") {
        composable("addPost") {
            AddPostScreen(navController = navController)
        }
    }
}

@Composable
fun HomeNavHost(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    mediaViewModel: MediaViewModel,
    serviceManager: ServiceManager
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    val isServiceRunning by serviceManager.isServiceRunning.collectAsState()

    NavHost(navController = navController, startDestination = "feed") {
        composable("feed") {
            HomeScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                isPlayerBoxVisible = isServiceRunning
            )
        }
        composable(
            "userScreen/{user}",
            arguments = listOf(navArgument("user") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userString = backStackEntry.arguments?.getString("user") ?: ""
            val user = userString.fromJson(User::class.java)
            UserScreen(
                navController = navController,
                data = user,
                sharedViewModel = sharedViewModel,
                isPlayerBoxVisible = isServiceRunning
            )
        }
        composable(
            route = "postScreen/{postId}",
            arguments = listOf(navArgument("postId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: -1
            PostScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                postId = postId,
                mediaViewModel = mediaViewModel,
                serviceManager = serviceManager,
                isPlayerBoxVisible = isServiceRunning
            )
        }
        composable(
            "followerScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowerScreen(
                navController = navController,
                userId,
                sharedViewModel = sharedViewModel
            )
        }
        composable(
            "followingScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowingScreen(
                navController = navController,
                userId,
                sharedViewModel = sharedViewModel
            )
        }
        composable("bookmark") {
            BookmarkedScreen(navController = navController)
        }
    }
}

@Composable
fun SearchNavHost(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    mediaViewModel: MediaViewModel,
    serviceManager: ServiceManager
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    val isServiceRunning by serviceManager.isServiceRunning.collectAsState()

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                isPlayerBoxVisible = isServiceRunning
            )
        }
        composable(
            "userScreen/{user}",
            arguments = listOf(navArgument("user") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userString = backStackEntry.arguments?.getString("user") ?: ""
            val user = userString.fromJson(User::class.java)
            UserScreen(
                navController = navController,
                data = user,
                sharedViewModel = sharedViewModel,
                isPlayerBoxVisible = isServiceRunning
            )
        }
        composable(
            route = "postScreen/{postId}",
            arguments = listOf(navArgument("postId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: -1
            PostScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                postId = postId,
                mediaViewModel = mediaViewModel,
                serviceManager = serviceManager,
                isPlayerBoxVisible = isServiceRunning
            )
        }

        composable(
            "followerScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowerScreen(
                navController = navController,
                userId,
                sharedViewModel = sharedViewModel
            )
        }
        composable(
            "followingScreen/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            FollowingScreen(
                navController = navController,
                userId,
                sharedViewModel = sharedViewModel
            )
        }
    }
}

@Composable
fun ProfileNavHost(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    mediaViewModel: MediaViewModel,
    serviceManager: ServiceManager
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    val isServiceRunning by serviceManager.isServiceRunning.collectAsState()

    NavHost(navController = navController, startDestination = "profile") {
        composable("profile") {
            ProfileScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                isPlayerBoxVisible = isServiceRunning
            )
        }
        composable(
            route = "postScreen/{postId}",
            arguments = listOf(navArgument("postId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: -1
            PostScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                postId = postId,
                mediaViewModel = mediaViewModel,
                serviceManager = serviceManager,
                isPlayerBoxVisible = isServiceRunning
            )
        }

        composable(
            "userScreen/{user}",
            arguments = listOf(navArgument("user") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val userString = backStackEntry.arguments?.getString("user") ?: ""
            val user = userString.fromJson(User::class.java)
            UserScreen(
                navController = navController,
                data = user,
                sharedViewModel = sharedViewModel,
                isPlayerBoxVisible = isServiceRunning
            )
        }

        composable("editProfile") {
            EditProfileScreen(navController, sharedViewModel = sharedViewModel)
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
    }
}
