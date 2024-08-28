package com.neatplex.nightell.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.CustomSimpleButton
import com.neatplex.nightell.component.post.ProfilePostCard
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavController,
    data: User,
    userViewModel: UserViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    isPlayerBoxVisible: Boolean
) {

    val bottomPadding = if (isPlayerBoxVisible) 135.dp else 65.dp

    //Fetch user profile info
    val profileResult by userViewModel.showUserInfoResult.observeAsState()
    val posts by userViewModel.postList.observeAsState(emptyList())
    val user = data

    val isLoading by userViewModel.isLoading.observeAsState(false)
    var isFollowedByMe by remember { mutableStateOf(false) }
    var hasFollowedMe by remember { mutableStateOf(false) }
    var lastPostId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.getUserInfo(user.id)
    }

    AppTheme {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, clip = false) // Add shadow to the Box
                        .background(Color.White)
                ) {
                    TopAppBar(
                        title = { Text(text = user.username, fontSize = 20.sp) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent, // Transparent to use the Box's background
                        )
                    )
                }
            },
            content = { space ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(space),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (val result = profileResult) {

                        is Result.Success -> {
                            val user = result.data!!.user
                            val followersCount = result.data.followers_count
                            val followingsCount = result.data.followings_count
                            isFollowedByMe = result.data.followed_by_me
                            hasFollowedMe = result.data.follows_me

                            // Load posts
                            userViewModel.loadPosts(user.id, lastPostId)

                            // Display the profile
                            ShowProfile(
                                navController,
                                user,
                                followersCount,
                                followingsCount,
                                userViewModel,
                                isFollowedByMe,
                                hasFollowedMe
                            )
                        }

                        is Result.Failure -> {
                            // Handle error state
                            Text(
                                text = "Error in loading profile: ${result.message}",
                                color = Color.Red
                            )
                        }

                        else -> {}
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyVerticalGrid(
                        contentPadding = PaddingValues(bottom = bottomPadding),
                        columns = GridCells.Fixed(2), // Define the number of columns
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(posts) { index, post ->
                            ProfilePostCard(post = post, isLoading = isLoading) { selectedPost ->
                                sharedViewModel.setPost(selectedPost)
                                navController.navigate(
                                    "postScreen/${selectedPost.id}"
                                )
                            }
                            if (index == posts.size - 1 && userViewModel.canLoadMore) {
                                lastPostId = post.id
                                userViewModel.loadPosts(user.id, lastPostId)
                            }
                        }
                        if (isLoading) {
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
    user: User,
    followers: Int,
    followings: Int,
    userViewModel: UserViewModel,
    isFollowedByMe: Boolean,
    followsMe: Boolean,
) {

    // Define a mutable state for the follower count
    var followers by remember { mutableIntStateOf(followers) }
    var isFollowed by remember { mutableStateOf(isFollowedByMe) }
    var hasFollowedMe by remember { mutableStateOf(followsMe) }
    val userId = user.id

    // Function to update follower count
    val updateFollowerCount: (Int) -> Unit = { increment ->
        followers += increment
    }

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
                val imageResource =
                    rememberAsyncImagePainter(model = R.drawable.default_profile_image)
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
                    navController.navigate("followerScreen/${user.id}")
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
                    navController.navigate("followingScreen/${user.id}")
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
                onClick = {
                    if (!isFollowed) {
                        userViewModel.followUser(userId)
                        isFollowed = true
                        updateFollowerCount(1)
                    } else {
                        userViewModel.unfollowUser(userId)
                        isFollowed = false
                        updateFollowerCount(-1)
                    }
                },
                text = if (!isFollowed) {
                    if (hasFollowedMe) "Follow Back" else "Follow"
                } else "Unfollow"
            )
        }
    }
}
