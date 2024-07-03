package com.neatplex.nightell.ui.profile

import android.net.Uri
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
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
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.toJson

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel
) {

    val profileResult by profileViewModel.profileData.observeAsState()
    val user = sharedViewModel.user.value
    val posts by profileViewModel.posts.observeAsState(emptyList())
    val isLoading by profileViewModel.isLoading.observeAsState(false)
    var lastPostId by remember { mutableStateOf<Int?>(null) }
    val isRefreshing by profileViewModel.isRefreshing.observeAsState(false)

    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { profileViewModel.refreshProfile(user!!.id) }
    )

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
                        title = {
                            Text(text = user?.username ?: "Profile", fontWeight = FontWeight.Bold)
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent, // Transparent to use the Box's background
                        )
                    )
                }
            },
            content = { space ->
                Box(
                    modifier = Modifier
                        .padding(space)
                        .pullRefresh(refreshState)
                ) {
                    if (user == null) {
                        // Handle case where user is null
                        Text(
                            text = "Unable to load data. Please check your internet connection.",
                            color = colorResource(id = R.color.purple_light),
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        // trigger the profile loading
                        LaunchedEffect(Unit) {
                            profileViewModel.fetchProfile()
                            profileViewModel.loadPosts(user.id, lastPostId)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            when (val result = profileResult) {
                                is Result.Success -> {
                                    val responsedUser = result.data?.user
                                    val followers = result.data?.followers_count
                                    val followings = result.data?.followings_count
                                    if (responsedUser != null) {
                                        ShowMyProfile(navController, responsedUser, followers!!, followings!!)
                                    }
                                }

                                is Result.Error -> {
                                    // Handle error state
                                    Text(
                                        text = "Error in loading profile: ${result.message}",
                                        color = Color.Red
                                    )
                                }

                                is Result.Loading -> {
                                    // Show loading indicator
                                    CustomCircularProgressIndicator()
                                }

                                else -> {}
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyVerticalGrid(
                                contentPadding = PaddingValues(bottom = 65.dp),
                                columns = GridCells.Fixed(2), // Define the number of columns
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                itemsIndexed(posts!!) { index, post ->
                                    ProfilePostCard(post = post) { selectedPost ->
                                        sharedViewModel.setPost(selectedPost)
                                        val postJson = selectedPost.toJson()
                                        navController.navigate(
                                            "postScreen/${Uri.encode(postJson)}"
                                        )
                                    }
                                    if (index == posts!!.size - 1 && !isLoading && profileViewModel.canLoadMore) {
                                        lastPostId = post.id
                                        profileViewModel.loadPosts(user.id, lastPostId)
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
                }
            }
        )
    }
}

@Composable
fun ShowMyProfile(navController: NavController, user: User, followers: Int, followings: Int) {

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
                val imageResource = rememberAsyncImagePainter(model = R.drawable.default_profile_image)
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
                    navController.navigate("editProfile") {
                        launchSingleTop = true
                    }
                },
                text = "My Profile"
            )
        }
    }
}