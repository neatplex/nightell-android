package com.neatplex.nightell.ui.profile

import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomSimpleButton
import com.neatplex.nightell.component.post.ProfilePostCard
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.toJson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    postViewModel: ProfileViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel
) {

    val profileResult by profileViewModel.profileData.observeAsState()
    val userId = sharedViewModel.user.value!!.id
    val posts by postViewModel.posts.observeAsState(emptyList())
    val isLoading by postViewModel.isLoading.observeAsState(false)
    var lastPostId by remember { mutableStateOf<Int?>(null) }


    // trigger the profile loading
    LaunchedEffect(Unit) {
        profileViewModel.fetchProfile()
        profileViewModel.loadPosts(userId, lastPostId)
    }

    val (followers, setFollowers) = remember { mutableStateOf(0) }

    // Define a function to update follower count
    val updateFollowerCount: (Int) -> Unit = { increment ->
        setFollowers(followers + increment)
    }

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Profile")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }, content = { space ->
                Box(modifier = Modifier.padding(space)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        when (val result = profileResult) {
                            is Result.Success -> {
                                val user = result.data?.user
                                val followers = result.data?.followers_count
                                val followings = result.data?.followings_count
                                if (user != null) {
                                    ShowMyProfile(navController, user, followers!!, followings!!)
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
                            }

                            else -> {}
                        }

                        Spacer(modifier = Modifier.height(30.dp))

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
                                    profileViewModel.loadPosts(userId,lastPostId)
                                }
                            }
                            if (isLoading) {
                                item {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .padding(vertical = 16.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
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
                .padding(8.dp),
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

                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = user.username,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                .padding(start = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = user.name)
        }
        Row(
            modifier = Modifier
                .width(200.dp)
                .padding(start = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = user.bio,
                fontSize = 14.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp)
        ) {

            CustomSimpleButton(
                onClick = {
                    navController.navigate("editProfile") {
                        launchSingleTop = true
                    }
                },
                text = "Edit Profile"
            )
        }
    }
}