package com.neatplex.nightell.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.component.ShowPosts
import com.neatplex.nightell.util.Result
import com.neatplex.nightell.viewmodels.PostViewModel
import com.neatplex.nightell.viewmodels.SharedViewModel
import com.neatplex.nightell.viewmodels.UserProfileViewModel


@Composable
fun HomeScreen(navController: NavController, postViewModel: PostViewModel = hiltViewModel(), sharedViewModel: SharedViewModel) {

    val posts by postViewModel.posts.observeAsState(emptyList())
    val isLoading by postViewModel.isLoading.observeAsState(false)

    val userProfileViewModel: UserProfileViewModel = hiltViewModel()
    val profileResult by userProfileViewModel.profileData.observeAsState()

    LaunchedEffect(Unit) {
        postViewModel.loadMorePosts()
        userProfileViewModel.fetchProfile()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Nightell") },
            actions = {
                IconButton(onClick = {
                    navController.navigate("search")
                }) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "search"
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
            ) {

                ShowPosts(posts, navController, sharedViewModel)
                
                when(val result = profileResult){
                    is Result.Success -> {
                        sharedViewModel.setUser(result.data!!.user)
                    }

                    else -> {}
                }

                // Show loading indicator if loading
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Load more button
                if (!isLoading) {
                    Button(onClick = { postViewModel.loadMorePosts() }) {
                        Text(text = "Load More Posts")
                    }
                }
            }
        }
    })
}


