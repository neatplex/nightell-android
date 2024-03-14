package com.neatplex.nightell.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import coil.compose.rememberImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.ShowPosts
import com.neatplex.nightell.model.User
import com.neatplex.nightell.viewmodels.UserProfileViewModel
import com.neatplex.nightell.util.Result
import com.neatplex.nightell.util.TokenManager
import com.neatplex.nightell.viewmodels.PostViewModel
import com.neatplex.nightell.viewmodels.SharedViewModel
import com.neatplex.nightell.viewmodels.ProfileViewModel


@Composable
fun ProfileScreen(navController: NavController, userProfileViewModel: UserProfileViewModel = hiltViewModel(), postViewModel: PostViewModel = hiltViewModel(), sharedViewModel: SharedViewModel) {

    val profileResult by userProfileViewModel.profileData.observeAsState()
    val userId = sharedViewModel.user.value!!.id
    val posts by postViewModel.posts.observeAsState(emptyList())
    val isLoading by postViewModel.isLoading.observeAsState(false)


    // trigger the profile loading
    LaunchedEffect(Unit) {
        userProfileViewModel.fetchProfile()
        postViewModel.loadUserPosts(userId)
    }

    // handle navigation to edit screen
    var editScreenVisible by remember { mutableStateOf(false) }
    if (editScreenVisible) {
        // navigate to edit screen
        navController.navigate("editProfile") {
            launchSingleTop = true
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Profile") },
            actions = {
                IconButton(onClick = {
                    // show edit screen when edit button is clicked
                    editScreenVisible = true
                }) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit Profile"
                    )
                }
            })
    }, content = { space ->
        Box(modifier = Modifier.padding(space)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ){



                when (val result = profileResult) {

                    is Result.Success -> {
                        val user = result.data?.user
                        val followers = result.data?.followers_count
                        val followings = result.data?.followings_count
                        if (user != null) {
                            ShowProfile(navController ,user, followers!!, followings!!)
                            ShowPosts(posts, navController, sharedViewModel)
                        }
                    }

                    is Result.Error -> {
                        // Handle error state
                        Text(text = "Error in loading profile: ${result.message}", color = Color.Red)
                    }

                    is Result.Loading -> {

                    }

                    else -> {}

                }

                Spacer(modifier = Modifier.height(30.dp))

                // Show loading indicator if loading
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    })
}


@Composable
fun ShowProfile(navController: NavController, user: User, followers: Int, followings: Int) {

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier
                .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally) {
                val imageResource = rememberImagePainter(data = R.drawable.default_profile_image,)
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

            Column(modifier = Modifier
                .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally) {
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

            Column(modifier = Modifier
                .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally) {
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
                .padding(start = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = user.name)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = user.bio)
        }
    }
}

