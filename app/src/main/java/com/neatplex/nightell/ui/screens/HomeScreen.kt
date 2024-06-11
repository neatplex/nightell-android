package com.neatplex.nightell.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.component.post.RecentPostCard
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.post.PostViewModel
import com.neatplex.nightell.ui.shared.SharedViewModel
import com.neatplex.nightell.ui.user.UserProfileViewModel
import com.neatplex.nightell.utils.toJson
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.neatplex.nightell.component.post.HomePostCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    postViewModel: PostViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel
) {
    val posts by postViewModel.posts.observeAsState(emptyList())
    val isLoading by postViewModel.isLoading.observeAsState(false)
    val isRefreshing by postViewModel.isRefreshing.observeAsState(false)

    val userProfileViewModel: UserProfileViewModel = hiltViewModel()
    val profileResult by userProfileViewModel.profileData.observeAsState()

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
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate("search")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            content = { space ->
                Box(modifier = Modifier.padding(space)) {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing),
                        onRefresh = { postViewModel.refreshFeed() }
                    ) {
                        Column {

                            LazyRow {
                                itemsIndexed(posts.take(3)) { index, post ->
                                    if (post != null) {
                                        RecentPostCard(post = post) { selectedPost ->
                                            sharedViewModel.setPost(selectedPost)
                                            val postJson = selectedPost.toJson()
                                            navController.navigate(
                                                "postScreen/${
                                                    Uri.encode(
                                                        postJson
                                                    )
                                                }"
                                            )
                                        }
                                    }
                                }
                            }

                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 65.dp),
                                modifier = Modifier.fillMaxSize(),
                                content = {
                                    itemsIndexed(posts.drop(3)) { index, post ->
                                        if (post != null) {
                                            HomePostCard(post = post) { selectedPost ->
                                                sharedViewModel.setPost(selectedPost)
                                                val postJson = selectedPost.toJson()
                                                navController.navigate(
                                                    "postScreen/${
                                                        Uri.encode(
                                                            postJson
                                                        )
                                                    }"
                                                )
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
                        }
                    }

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
                        )
                    }
                }
            }
        )
    }
}