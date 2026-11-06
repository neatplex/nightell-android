package com.neatplex.nightell.ui.screens.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.neatplex.nightell.ui.component.common.AppTopBar
import com.neatplex.nightell.ui.component.common.EmptyState
import com.neatplex.nightell.ui.component.common.SectionHeader
import com.neatplex.nightell.ui.component.widget.CustomBorderedButton
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import com.neatplex.nightell.ui.component.user.ProfileHeader
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.post.ProfilePostCard
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.navigation.Routes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    sessionViewModel: SessionViewModel,
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val postsState by profileViewModel.posts.collectAsStateWithLifecycle()
    val profile = uiState.profile
    val user = profile?.user ?: sessionViewModel.currentUser.collectAsStateWithLifecycle().value
    val posts = postsState.items
    val isLoading = postsState.isLoading

    // Keep the app-wide current user in sync with the freshly loaded profile.
    LaunchedEffect(profile?.user) {
        profile?.user?.let(sessionViewModel::setCurrentUser)
    }

    // Observe changes in the savedStateHandle
    val postChanged = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("postChanged", false)
        ?.collectAsStateWithLifecycle()

    LaunchedEffect(postChanged?.value) {
        if (postChanged?.value == true) {
            profileViewModel.refresh(showIndicator = false)
            navController.currentBackStackEntry?.savedStateHandle?.set("postChanged", false)
        }
    }

    val refreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { profileViewModel.refresh() }
    )

    Scaffold(
        topBar = {
            AppTopBar(title = user?.username?.let { "@$it" } ?: "Profile") {
                IconButton(onClick = { navController.navigate(Routes.EDIT_PROFILE) { launchSingleTop = true } }) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Edit profile and settings")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { space ->
        Box(
            modifier = Modifier
                .padding(space)
                .pullRefresh(refreshState)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 24.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    when {
                        profile != null -> ProfileHeader(
                            user = profile.user,
                            followers = profile.followersCount,
                            followings = profile.followingsCount,
                            storyCount = null,
                            onFollowersClick = { navController.navigate(Routes.followers(profile.user.id)) },
                            onFollowingsClick = { navController.navigate(Routes.followings(profile.user.id)) },
                        ) {
                            CustomBorderedButton(
                                text = "Edit profile",
                                onClick = { navController.navigate(Routes.EDIT_PROFILE) { launchSingleTop = true } },
                            )
                        }
                        !uiState.isLoading -> EmptyState(
                            icon = Icons.Rounded.Refresh,
                            title = "Couldn't load your profile",
                            message = "Check your connection and pull down to try again.",
                        )
                        else -> CustomCircularProgressIndicator()
                    }
                }
                if (profile != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) { SectionHeader("My stories", Modifier.padding(start = 0.dp)) }
                    if (posts.isEmpty() && !isLoading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyState(
                                icon = Icons.Rounded.Add,
                                title = "No stories yet",
                                message = "Tap Create below to record your first bedtime story.",
                            )
                        }
                    }
                }
                itemsIndexed(posts, key = { _, post -> post.id }) { index, post ->
                    ProfilePostCard(post = post, isLoading = false) { selectedPost ->
                        navController.navigate(Routes.post(selectedPost.id))
                    }
                    if (index == posts.lastIndex && !isLoading && postsState.canLoadMore) {
                        LaunchedEffect(post.id) { profileViewModel.loadMorePosts() }
                    }
                }
                if (isLoading && posts.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) { CustomCircularProgressIndicator() }
                }
            }
            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
