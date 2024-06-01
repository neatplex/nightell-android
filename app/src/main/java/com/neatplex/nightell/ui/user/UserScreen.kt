package com.neatplex.nightell.ui.user

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.PostCard
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.post.PostViewModel
import com.neatplex.nightell.ui.profile.ProfileViewModel
import com.neatplex.nightell.ui.shared.SharedViewModel
import com.neatplex.nightell.utils.toJson

@Composable
fun UserScreen(
    navController: NavController,
    userId: Int,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    postViewModel: PostViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel
) {

    //Fetch user profile info
    val profileResult by profileViewModel.showUserInfoResult.observeAsState()
    val posts by postViewModel.userPosts.observeAsState(emptyList())

    //Fetch if user followed this profile
    val followingViewModel: UserProfileViewModel = hiltViewModel()
    val followingsResult by followingViewModel.userData.observeAsState()

    val myId = sharedViewModel.user.value!!.id

    var isLoading by remember { mutableStateOf(true) }
    var followings: List<User> by remember { mutableStateOf(emptyList()) }

    var isFollowed by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        profileViewModel.getUserInfo(userId)
        postViewModel.loadUserPosts(userId)
        followingViewModel.fetchUserFollowings(myId)
    }

    when (val result = followingsResult) {
        is Result.Success -> {
            followings = result.data?.users.orEmpty()
            isFollowed = followings.any { it.id == userId }
        }

        else -> {}
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "User") })
    }, content = { space ->
        Box(modifier = Modifier.padding(space)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (val result = profileResult) {
                    is Result.Success -> {
                        val user = result.data!!.user
                        val followers = result.data.followers_count
                        val followings = result.data.followings_count

                        if (user != null) {

                            ShowProfile(
                                navController,
                                user,
                                followers,
                                followings,
                                profileViewModel,
                                myId,
                                userId,
                                isFollowed
                            )
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


                LazyColumn(
                    modifier = Modifier
                        .padding(bottom = 50.dp),
                    content = {
                        itemsIndexed(posts) { index, post ->
                            if (post != null) {
                                PostCard(post = post) { selectedPost ->
                                    isLoading = false
                                    sharedViewModel.setPost(selectedPost)
                                    val postJson = selectedPost.toJson()
                                    navController.navigate("postScreen/${Uri.encode(postJson)}")
                                }
                            } else {
                                isLoading = false
                            }
                        }
                    }
                )

            }
        }
    })
}

@Composable
fun ShowProfile(
    navController: NavController,
    user: User,
    followers: Int,
    followings: Int,
    profileViewModel: ProfileViewModel,
    myId: Int,
    userId: Int,
    isFollowed: Boolean
) {

    // Define a mutable state for the follower count
    var followers by remember { mutableIntStateOf(followers) }
    var isFollowed by remember { mutableStateOf(isFollowed) }

    val followResult by profileViewModel.followResult.observeAsState()
    val unfollowResult by profileViewModel.unfollowResult.observeAsState()

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
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val imageResource = rememberImagePainter(data = R.drawable.default_profile_image)
                Image(
                    painter = imageResource,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(
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
                .padding(start = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = user.name)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = user.bio)
        }
    }

    Row {
        Button(
            onClick = {
                if (!isFollowed) {
                    profileViewModel.followUser(myId, userId)
                    isFollowed = true
                    updateFollowerCount(1)
                } else {
                    profileViewModel.unfollowUser(myId, userId)
                    isFollowed = false
                    updateFollowerCount(-1)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (!isFollowed) "Follow" else "Unfollow")
        }
    }
}
