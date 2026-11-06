package com.neatplex.nightell.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.widget.CustomSimpleButton
import com.neatplex.nightell.ui.component.post.ProfilePostCard
import com.neatplex.nightell.domain.model.UserProfile
import com.neatplex.nightell.ui.screens.profile.getUserImagePainter
import com.neatplex.nightell.ui.theme.AppTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.navigation.Routes

@Composable
fun UserScreen(
    navController: NavController,
    userViewModel: UserProfileViewModel = hiltViewModel(),
) {
    val uiState by userViewModel.uiState.collectAsStateWithLifecycle()
    val postsState by userViewModel.posts.collectAsStateWithLifecycle()
    val posts = postsState.items

    AppTheme {
        Scaffold(
            topBar = {
                AppBarWithTitle(
                    navController = navController,
                    title = uiState.profile?.user?.username.orEmpty(),
                )
            },
            content = { space ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(space),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val profile = uiState.profile
                    if (profile != null) {
                        ShowProfile(
                            navController = navController,
                            profile = profile,
                            imageResource = getUserImagePainter(profile.user),
                            onToggleFollow = userViewModel::toggleFollow,
                        )
                    }
                    uiState.error?.let { message ->
                        Text(
                            text = "Error in loading profile: $message",
                            color = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(posts) { index, post ->
                            ProfilePostCard(post = post, isLoading = postsState.isLoading) { selectedPost ->
                                navController.navigate(Routes.post(selectedPost.id))
                            }
                            if (index == posts.size - 1 && postsState.canLoadMore) {
                                userViewModel.loadMorePosts()
                            }
                        }
                        if (postsState.isLoading) {
                            item {
                                CustomCircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ShowProfile(
    navController: NavController,
    profile: UserProfile,
    imageResource: Painter,
    onToggleFollow: () -> Unit,
) {
    val user = profile.user
    val followers = profile.followersCount
    val followings = profile.followingsCount
    val isFollowed = profile.followedByMe
    val hasFollowedMe = profile.followsMe

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {

                Image(
                    painter = imageResource,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.clickable {
                    // Navigate to another page when "Followers" is clicked
                    navController.navigate(Routes.followers(user.id))
                }) {
                    Text(text = "Followers")
                }
                Row {
                    Text(text = followers.toString())
                }

            }

            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.clickable {
                    // Navigate to another page when "Followers" is clicked
                    navController.navigate(Routes.followings(user.id))
                }) {
                    Text(text = "Followings")
                }
                Row {
                    Text(text = followings.toString())
                }
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = user.name)
        }
        Row(
            modifier = Modifier
                .width(300.dp)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = user.bio,
                fontSize = 13.sp,
                lineHeight = 16.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            CustomSimpleButton(
                onClick = onToggleFollow,
                text = if (!isFollowed) {
                    if (hasFollowedMe) "Follow Back" else "Follow"
                } else "Unfollow"
            )
        }
    }
}
