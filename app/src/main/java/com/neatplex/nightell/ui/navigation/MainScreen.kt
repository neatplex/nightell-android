package com.neatplex.nightell.ui.navigation

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.neatplex.nightell.ui.component.media.PlayerBox
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.SessionViewModel

/** The four bottom-navigation tabs, in display order. */
enum class BottomNavScreens(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    Home("home", "Home", Icons.Outlined.Home, Icons.Rounded.Home),
    Search("search", "Discover", Icons.Outlined.Search, Icons.Rounded.Search),
    AddPost("addPost", "Create", Icons.Outlined.AddCircle, Icons.Rounded.AddCircle),
    Profile("profile", "Profile", Icons.Outlined.Person, Icons.Rounded.Person),
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

    val player by mediaViewModel.playerState.collectAsStateWithLifecycle()

    fun selectTab(tab: BottomNavScreens) {
        if (selectedTab == tab) return
        tabHistory.remove(tab)
        tabHistory.add(tab)
        selectedTab = tab
    }

    Scaffold(
        bottomBar = {
            Column {
                val isOnPostScreen = activeRoute.contains("postScreen")
                val currentPostId = player.currentPostId
                AnimatedVisibility(
                    visible = player.isActive && !isOnPostScreen && currentPostId != null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                ) {
                    PlayerBox(
                        mediaViewModel = mediaViewModel,
                        onMaximizeClick = {
                            if (currentPostId == null) return@PlayerBox
                            selectTab(BottomNavScreens.Home)
                            homeNavController.navigate(Routes.post(currentPostId)) {
                                popUpTo(Routes.FEED) { inclusive = false }
                            }
                        },
                    )
                }
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    BottomNavScreens.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                                    contentDescription = tab.label,
                                )
                            },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Child screens have their own Scaffolds; consume the insets used here so they aren't added twice.
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
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
