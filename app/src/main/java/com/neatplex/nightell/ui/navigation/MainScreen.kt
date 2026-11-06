package com.neatplex.nightell.ui.navigation

import android.app.Activity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.media.PlayerBox
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.SessionViewModel

/** The four bottom-navigation tabs, in display order. */
enum class BottomNavScreens(val route: String, val icon: Int) {
    Home("home", R.drawable.home),
    Search("search", R.drawable.search),
    AddPost("addPost", R.drawable.add_post),
    Profile("profile", R.drawable.profile),
}

/**
 * The signed-in shell: a bottom bar with one back stack per tab, and the mini
 * player above it while audio is loaded.
 */
@Composable
fun MainScreen(
    mediaViewModel: MediaViewModel,
    sessionViewModel: SessionViewModel,
) {
    var activeRoute by remember { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavScreens.Home) }
    // Tabs in the order they were visited, so Back can return to the previous tab.
    val tabHistory = remember { mutableStateListOf(BottomNavScreens.Home) }

    val navControllers = BottomNavScreens.entries.associateWith { rememberNavController() }
    val homeNavController = navControllers.getValue(BottomNavScreens.Home)

    val isServiceRunning by mediaViewModel.isServiceRunning.collectAsStateWithLifecycle()
    val player by mediaViewModel.playerState.collectAsStateWithLifecycle()

    fun selectTab(tab: BottomNavScreens) {
        if (selectedTab == tab) return
        tabHistory.remove(tab)
        tabHistory.add(tab)
        selectedTab = tab
    }

    Scaffold(
        bottomBar = {
            AppTheme {
                Box {
                    NavigationBar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(60.dp)
                            .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
                            .background(Color.White),
                        containerColor = Color.White
                    ) {
                        BottomNavScreens.entries.forEach { tab ->
                            val isSelected = selectedTab == tab
                            BottomNavigationItem(
                                selected = isSelected,
                                onClick = { selectTab(tab) },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = tab.icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                        tint = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.5f)
                                    )
                                }
                            )
                        }
                    }

                    val isOnPostScreen = activeRoute.contains("postScreen")
                    val currentPostId = player.currentPostId
                    if (isServiceRunning && !isOnPostScreen && currentPostId != null) {
                        PlayerBox(
                            mediaViewModel = mediaViewModel,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 60.dp),
                            onMaximizeClick = {
                                selectTab(BottomNavScreens.Home)
                                homeNavController.navigate(Routes.post(currentPostId)) {
                                    popUpTo(Routes.FEED) { inclusive = false }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val onRouteChange: (String) -> Unit = { activeRoute = it }
            when (selectedTab) {
                BottomNavScreens.Home ->
                    HomeNavHost(homeNavController, sessionViewModel, mediaViewModel, onRouteChange)
                BottomNavScreens.Search ->
                    SearchNavHost(navControllers.getValue(BottomNavScreens.Search), sessionViewModel, mediaViewModel, onRouteChange)
                BottomNavScreens.AddPost ->
                    AddPostNavHost(navControllers.getValue(BottomNavScreens.AddPost))
                BottomNavScreens.Profile ->
                    ProfileNavHost(navControllers.getValue(BottomNavScreens.Profile), sessionViewModel, mediaViewModel, onRouteChange)
            }
        }
    }

    TabBackHandler(
        currentController = navControllers.getValue(selectedTab),
        tabHistory = tabHistory,
        onSelectTab = { selectedTab = it },
    )
}

/**
 * Back goes up within the current tab first, then to the previously visited
 * tab, then to Home, and finally closes the app.
 */
@Composable
private fun TabBackHandler(
    currentController: NavHostController,
    tabHistory: MutableList<BottomNavScreens>,
    onSelectTab: (BottomNavScreens) -> Unit,
) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val activity = LocalContext.current as? Activity
    val controller by rememberUpdatedState(currentController)

    DisposableEffect(dispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val atTabRoot = controller.currentDestination?.route == controller.graph.startDestinationRoute
                when {
                    !atTabRoot -> controller.popBackStack()
                    tabHistory.size > 1 -> {
                        tabHistory.removeAt(tabHistory.lastIndex)
                        onSelectTab(tabHistory.last())
                    }
                    tabHistory.lastOrNull() != BottomNavScreens.Home -> {
                        tabHistory.clear()
                        tabHistory.add(BottomNavScreens.Home)
                        onSelectTab(BottomNavScreens.Home)
                    }
                    // MainActivity stops the media service when it finishes.
                    else -> activity?.finish()
                }
            }
        }
        dispatcher?.addCallback(callback)
        onDispose { callback.remove() }
    }
}
