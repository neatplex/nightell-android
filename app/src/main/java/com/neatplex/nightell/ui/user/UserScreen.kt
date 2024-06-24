package com.neatplex.nightell.ui.user

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
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomSimpleButton
import com.neatplex.nightell.component.post.ProfilePostCard
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.ui.profile.ShowMyProfile
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.toJson

@Composable
fun UserScreen(
    navController: NavController,
    userId: Int,
    userViewModel: UserViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel
) {

    //Fetch user profile info
    val profileResult by userViewModel.showUserInfoResult.observeAsState()
    val posts by userViewModel.posts.observeAsState(emptyList())

    //Fetch if user followed this profile
    val followingsResult by userViewModel.usersList.observeAsState()

    val myId = sharedViewModel.user.value!!.id

    val isLoading by userViewModel.isLoading.observeAsState(false)
    var followings: List<User> by remember { mutableStateOf(emptyList()) }

    var isFollowed by remember { mutableStateOf(false) }
    var lastPostId by remember { mutableStateOf<Int?>(null) }



    LaunchedEffect(Unit) {
        userViewModel.fetchUserFollowings(myId)
    }

    when (val result = followingsResult) {
        is Result.Success -> {
            followings = result.data?.users.orEmpty()
            isFollowed = followings.any { it.id == userId }
            userViewModel.getUserInfo(userId)
            userViewModel.loadPosts(userId, lastPostId)
        }

        else -> {
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "User") })
    }, content = { space ->
        Box(modifier = Modifier.padding(space)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                when (val result = profileResult) {
                    is Result.Success -> {
                        val user = result.data!!.user
                        val followers = result.data.followers_count
                        val followings = result.data.followings_count

                        ShowProfile(
                            navController,
                            user,
                            followers,
                            followings,
                            userViewModel,
                            myId,
                            userId,
                            isFollowed
                        )
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
                        if (index == posts!!.size - 1 && !isLoading && userViewModel.canLoadMore) {
                            lastPostId = post.id
                            userViewModel.loadPosts(userId, lastPostId)
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

@Composable
fun ShowProfile(
    navController: NavController,
    user: User,
    followers: Int,
    followings: Int,
    userViewModel: UserViewModel,
    myId: Int,
    userId: Int,
    isFollowed: Boolean
) {

    // Define a mutable state for the follower count
    var followers by remember { mutableIntStateOf(followers) }
    var isFollowed by remember { mutableStateOf(isFollowed) }

    val followResult by userViewModel.followResult.observeAsState()
    val unfollowResult by userViewModel.unfollowResult.observeAsState()

    // Function to update follower count
    val updateFollowerCount: (Int) -> Unit = { increment ->
        followers += increment
    }


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

                androidx.compose.material3.Text(
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
                    androidx.compose.material3.Text(text = "Followers")
                }
                Row {
                    androidx.compose.material3.Text(text = followers.toString())
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
                    androidx.compose.material3.Text(text = "Followings")
                }
                Row {
                    androidx.compose.material3.Text(text = followings.toString())
                }
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Text(text = user.name)
        }
        Row(
            modifier = Modifier
                .width(200.dp)
                .padding(start = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.Text(
                text = user.bio,
                fontSize = 14.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp)
        ) {

            CustomSimpleButton(
                onClick = {
                    if (!isFollowed) {
                        userViewModel.followUser(myId, userId)
                        isFollowed = true
                        updateFollowerCount(1)
                    } else {
                        userViewModel.unfollowUser(myId, userId)
                        isFollowed = false
                        updateFollowerCount(-1)
                    }
                },
                text = if (!isFollowed) "Follow" else "Unfollow"
            )
        }
    }
}
