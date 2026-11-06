package com.neatplex.nightell.ui.screens.user

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.MaterialTheme
import com.neatplex.nightell.ui.component.common.AppTopBar
import com.neatplex.nightell.ui.component.common.EmptyState
import com.neatplex.nightell.ui.component.common.SectionHeader
import com.neatplex.nightell.ui.component.widget.CustomBorderedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import com.neatplex.nightell.ui.component.user.ProfileHeader
import com.neatplex.nightell.ui.component.widget.ErrorBanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.post.ProfilePostCard
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.navigation.Routes
import androidx.compose.runtime.LaunchedEffect

@Composable
fun UserScreen(
    navController: NavController,
    userViewModel: UserProfileViewModel = hiltViewModel(),
) {
    val uiState by userViewModel.uiState.collectAsStateWithLifecycle()
    val postsState by userViewModel.posts.collectAsStateWithLifecycle()
    val posts = postsState.items
    val profile = uiState.profile

    Scaffold(
        topBar = {
            AppTopBar(
                title = profile?.user?.username?.let { "@$it" }.orEmpty(),
                onBack = { navController.popBackStack() },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { space ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 24.dp),
            modifier = Modifier
                .padding(space)
                .fillMaxSize(),
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
                        FollowButton(
                            isFollowing = profile.followedByMe,
                            followsMe = profile.followsMe,
                            onClick = userViewModel::toggleFollow,
                        )
                    }
                    uiState.isLoading -> CustomCircularProgressIndicator()
                    else -> EmptyState(
                        icon = Icons.Rounded.Refresh,
                        title = "Couldn't load this profile",
                        message = uiState.error,
                    )
                }
            }
            if (profile != null) {
                uiState.error?.let { message ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ErrorBanner(message, modifier = Modifier.padding(horizontal = 6.dp))
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) { SectionHeader("Stories", Modifier.padding(start = 0.dp)) }
                if (posts.isEmpty() && !postsState.isLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyState(icon = Icons.Rounded.Info, title = "No stories yet")
                    }
                }
            }
            itemsIndexed(posts, key = { _, post -> post.id }) { index, post ->
                ProfilePostCard(post = post, isLoading = false) { selectedPost ->
                    navController.navigate(Routes.post(selectedPost.id))
                }
                if (index == posts.lastIndex && postsState.canLoadMore && !postsState.isLoading) {
                    LaunchedEffect(post.id) { userViewModel.loadMorePosts() }
                }
            }
            if (postsState.isLoading && posts.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) { CustomCircularProgressIndicator() }
            }
        }
    }
}

/** "Follow" / "Follow back" as a filled button, "Following" as an outlined one. */
@Composable
private fun FollowButton(isFollowing: Boolean, followsMe: Boolean, onClick: () -> Unit) {
    if (isFollowing) {
        CustomBorderedButton(text = "Following", onClick = onClick)
    } else {
        Button(onClick = onClick, shape = MaterialTheme.shapes.large, modifier = Modifier.height(52.dp)) {
            Text(if (followsMe) "Follow back" else "Follow", modifier = Modifier.padding(horizontal = 24.dp))
        }
    }
}
