package com.neatplex.nightell.ui.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.toJson
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.neatplex.nightell.component.post.HomePostCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel
) {
    val feed by homeViewModel.feed.observeAsState(emptyList())
    val isLoading by homeViewModel.isLoading.observeAsState(false)
    val isRefreshing by homeViewModel.isRefreshing.observeAsState(false)
    val profileResult by homeViewModel.profileData.observeAsState()
    var lastPostId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.loadFeed(lastPostId)
        homeViewModel.fetchProfile()
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
                Box(
                    modifier = Modifier
                        .padding(space)
                        .background(color = Color.LightGray.copy(alpha = 0.5f))
                ) {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing),
                        onRefresh = {
                            homeViewModel.refreshFeed()
                        }
                    ) {
                        Column {
                            // LazyRow for the first 3 posts
                            LazyRow {
                                itemsIndexed(feed.take(3)) { index, post ->
                                    RecentPostCard(post = post) { selectedPost ->
                                        sharedViewModel.setPost(selectedPost)
                                        val postJson = selectedPost.toJson()
                                        navController.navigate(
                                            "postScreen/${Uri.encode(postJson)}"
                                        )
                                    }
                                }
                            }
                            // LazyColumn for the remaining posts
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 65.dp),
                                modifier = Modifier.fillMaxSize(),
                                content = {
                                    itemsIndexed(feed!!.drop(3)) { index, post ->
                                        HomePostCard(post = post) { selectedPost ->
                                            sharedViewModel.setPost(selectedPost)
                                            val postJson = selectedPost.toJson()
                                            navController.navigate(
                                                "postScreen/${Uri.encode(postJson)}"
                                            )
                                        }
                                        if (index == feed!!.drop(3).size - 1 && !isLoading && homeViewModel.canLoadMore) {
                                            lastPostId = post.id
                                            homeViewModel.loadFeed(lastPostId)
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
                }
            }
        )
    }
}