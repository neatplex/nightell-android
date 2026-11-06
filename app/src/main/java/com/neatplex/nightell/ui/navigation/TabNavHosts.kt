package com.neatplex.nightell.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.neatplex.nightell.ui.screens.bookmark.BookmarkedScreen
import com.neatplex.nightell.ui.screens.home.HomeScreen
import com.neatplex.nightell.ui.screens.post.EditPostScreen
import com.neatplex.nightell.ui.screens.post.PostScreen
import com.neatplex.nightell.ui.screens.profile.EditProfileScreen
import com.neatplex.nightell.ui.screens.profile.ProfileScreen
import com.neatplex.nightell.ui.screens.search.SearchScreen
import com.neatplex.nightell.ui.screens.upload.AddPostScreen
import com.neatplex.nightell.ui.screens.user.FollowerScreen
import com.neatplex.nightell.ui.screens.user.FollowingScreen
import com.neatplex.nightell.ui.screens.user.UserScreen
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.SessionViewModel

/** Each tab has its own back stack. These are the graphs for the four tabs. */

@Composable
fun HomeNavHost(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    mediaViewModel: MediaViewModel,
    onRouteChange: (String) -> Unit,
) {
    ReportRoute(navController, Routes.FEED, onRouteChange)
    NavHost(navController = navController, startDestination = Routes.FEED) {
        composable(Routes.FEED) {
            HomeScreen(navController = navController, sessionViewModel = sessionViewModel)
        }
        composable(Routes.BOOKMARKS) {
            BookmarkedScreen(navController = navController)
        }
        sharedDestinations(navController, sessionViewModel, mediaViewModel)
    }
}

@Composable
fun SearchNavHost(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    mediaViewModel: MediaViewModel,
    onRouteChange: (String) -> Unit,
) {
    ReportRoute(navController, Routes.SEARCH, onRouteChange)
    NavHost(navController = navController, startDestination = Routes.SEARCH) {
        composable(Routes.SEARCH) {
            SearchScreen(navController = navController, sessionViewModel = sessionViewModel)
        }
        sharedDestinations(navController, sessionViewModel, mediaViewModel)
    }
}

@Composable
fun AddPostNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.ADD_POST) {
        composable(Routes.ADD_POST) {
            AddPostScreen(navController = navController)
        }
    }
}

@Composable
fun ProfileNavHost(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    mediaViewModel: MediaViewModel,
    onRouteChange: (String) -> Unit,
) {
    ReportRoute(navController, Routes.PROFILE, onRouteChange)
    NavHost(navController = navController, startDestination = Routes.PROFILE) {
        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController, sessionViewModel = sessionViewModel)
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(navController, sessionViewModel = sessionViewModel)
        }
        sharedDestinations(navController, sessionViewModel, mediaViewModel)
    }
}

/** Screens reachable from every tab: a post, a user, and follower lists. */
private fun NavGraphBuilder.sharedDestinations(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    mediaViewModel: MediaViewModel,
) {
    composable(
        route = Routes.POST,
        arguments = listOf(navArgument(Routes.ARG_POST_ID) { type = NavType.IntType }),
    ) {
        PostScreen(navController = navController, mediaViewModel = mediaViewModel)
    }

    composable(
        route = Routes.EDIT_POST,
        arguments = listOf(
            navArgument(Routes.ARG_POST_ID) { type = NavType.IntType },
            navArgument(Routes.ARG_POST_TITLE) { type = NavType.StringType },
            navArgument(Routes.ARG_POST_DESCRIPTION) { type = NavType.StringType },
        ),
    ) { entry ->
        val args = checkNotNull(entry.arguments)
        EditPostScreen(
            navController,
            args.getInt(Routes.ARG_POST_ID),
            args.getString(Routes.ARG_POST_TITLE).orEmpty(),
            args.getString(Routes.ARG_POST_DESCRIPTION).orEmpty(),
        )
    }

    composable(
        route = Routes.USER,
        arguments = listOf(navArgument(Routes.ARG_USER_ID) { type = NavType.IntType }),
    ) {
        UserScreen(navController = navController)
    }

    composable(
        route = Routes.FOLLOWERS,
        arguments = listOf(navArgument(Routes.ARG_USER_ID) { type = NavType.IntType }),
    ) { entry ->
        FollowerScreen(
            navController = navController,
            userId = checkNotNull(entry.arguments).getInt(Routes.ARG_USER_ID),
            sessionViewModel = sessionViewModel,
        )
    }

    composable(
        route = Routes.FOLLOWINGS,
        arguments = listOf(navArgument(Routes.ARG_USER_ID) { type = NavType.IntType }),
    ) { entry ->
        FollowingScreen(
            navController = navController,
            userId = checkNotNull(entry.arguments).getInt(Routes.ARG_USER_ID),
            sessionViewModel = sessionViewModel,
        )
    }
}

/** Tells the parent which route is showing, so it can hide the mini player on a post. */
@Composable
private fun ReportRoute(navController: NavHostController, startRoute: String, onRouteChange: (String) -> Unit) {
    val entry by navController.currentBackStackEntryAsState()
    LaunchedEffect(entry) {
        onRouteChange(entry?.destination?.route ?: startRoute)
    }
}
