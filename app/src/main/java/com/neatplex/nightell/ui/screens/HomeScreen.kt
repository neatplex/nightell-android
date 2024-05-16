package com.neatplex.nightell.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.component.PostCard
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.post.PostViewModel
import com.neatplex.nightell.ui.shared.SharedViewModel
import com.neatplex.nightell.ui.user.UserProfileViewModel
import com.neatplex.nightell.utils.toJson


@Composable
fun HomeScreen(navController: NavController, postViewModel: PostViewModel = hiltViewModel(), sharedViewModel: SharedViewModel) {


    val posts by postViewModel.posts.observeAsState(emptyList())
    val isLoading by postViewModel.isLoading.observeAsState(false)

    val userProfileViewModel: UserProfileViewModel = hiltViewModel()
    val profileResult by userProfileViewModel.profileData.observeAsState()
    val bottomBarHeight = BottomNavigationHeight()

    LaunchedEffect(Unit) {
        postViewModel.loadFeed()
        userProfileViewModel.fetchProfile()
    }

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Image(
                        painter = painterResource(id = R.drawable.nightell_white),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .scale(1.5f)
                    ) },
                    backgroundColor = colorResource(id = R.color.blue),
                    actions = {
                        IconButton(onClick = {
                            navController.navigate("search")
                        }) {
                            Icon(
                                Icons.Filled.Search,
                                tint = Color.White,
                                contentDescription = "search"
                            )
                        }
                    }
                )
            },
            content = { space ->
                Box(modifier = Modifier.padding(space)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = bottomBarHeight),
                            content = {
                                itemsIndexed(posts) { index, post ->
                                    if (post != null) {
                                        PostCard(post = post) { selectedPost ->
                                            sharedViewModel.setPost(selectedPost)
                                            val postJson = selectedPost.toJson()
                                            navController.navigate("postScreen/${Uri.encode(postJson)}")
                                        }
                                        if (posts.size > 9 && index == posts.size - 1 && !isLoading) {
                                            postViewModel.loadFeed()
                                        }
                                    }
                                }

                                if (isLoading && posts.isNotEmpty()) {
                                    item {
                                        // Load more indicator
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .padding(vertical = 16.dp)
                                                .align(Alignment.CenterHorizontally)
                                        )
                                    }
                                }
                            }
                        )

                        when (val result = profileResult) {
                            is Result.Success -> {
                                result.data?.user?.let { sharedViewModel.setUser(it) }
                            }
                            else -> {
                            }
                        }

                        // Show loading indicator if loading
                        if (isLoading && posts.isEmpty()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }

                    }
                }
            }
        )
    }
}




@Composable
fun BottomNavigationHeight(): Dp {
    return 60.dp
}