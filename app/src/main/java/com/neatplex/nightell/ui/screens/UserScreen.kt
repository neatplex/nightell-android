package com.neatplex.nightell.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.component.ShowPosts
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.PostViewModel
import com.neatplex.nightell.ui.viewmodel.ProfileViewModel
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.ui.viewmodel.UserProfileViewModel

@Composable
fun UserScreen(navController: NavController, userId: Int, profileViewModel: ProfileViewModel = hiltViewModel(), postViewModel: PostViewModel = hiltViewModel(), sharedViewModel: SharedViewModel){

    val profileResult by profileViewModel.showUserInfoResult.observeAsState()
    val posts by postViewModel.posts.observeAsState()

    val followingViewModel : UserProfileViewModel = hiltViewModel()
    val myId = sharedViewModel.user.value?.id
    val followingsResult by followingViewModel.userData.observeAsState()

    var isFollowed by remember { mutableStateOf(false) }
    var followings : List<User> by remember { mutableStateOf(emptyList()) }


    LaunchedEffect(Unit) {
        profileViewModel.getUserInfo(userId)
        postViewModel.loadUserPosts(userId)
        followingViewModel.fetchUserFollowings(myId!!)
    }

    when(val result = followingsResult){
        is Result.Success ->{
            followings = result.data!!.users
            for(following in followings){
                if(following.id == userId) isFollowed = true
            }
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
                    .verticalScroll(rememberScrollState())
            ) {
                when (val result = profileResult) {
                    is Result.Success -> {
                        val user = result.data?.user
                        val followers = result.data?.followers_count
                        val followings = result.data?.followings_count
                        if (user != null) {
                            ShowProfile(navController, user, followers!!, followings!!)
                            Row {
                                if(!isFollowed){
                                    Button(onClick = {
                                        profileViewModel.followUser(myId!!,userId)
                                    }) {
                                        Text(text = "Follow")
                                    }
                                } else{
                                    Button(onClick = {
                                        profileViewModel.unfollowUser(myId!!,userId)
                                    }) {
                                        Text(text = "Unfollow")
                                    }
                                }
                            }
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

                ShowPosts(posts, navController, sharedViewModel)


            }
        }
    })
}