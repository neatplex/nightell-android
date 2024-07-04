package com.neatplex.nightell.ui.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.component.post.RecentPostCard
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.toJson
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.post.HomePostCard
import com.neatplex.nightell.ui.theme.feelFree

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { homeViewModel.refreshFeed() }
    )

    LaunchedEffect(Unit) {
        homeViewModel.loadFeed(lastPostId)
        homeViewModel.fetchProfile()
    }

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
                            Text(
                                text = "Nightell",
                                fontFamily = feelFree,
                                fontSize = 40.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        },
                        actions = {
                            IconButton(onClick = {

                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.bookmark),
                                    contentDescription = "saved audio",
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .align(Alignment.CenterVertically) // Align icon vertically
                                )
                            }
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
                                itemsIndexed(feed.drop(3)) { index, post ->
                                    HomePostCard(post = post) { selectedPost ->
                                        sharedViewModel.setPost(selectedPost)
                                        val postJson = selectedPost.toJson()
                                        navController.navigate(
                                            "postScreen/${Uri.encode(postJson)}"
                                        )
                                    }
                                    if (index == feed.drop(3).size - 1 && !isLoading && homeViewModel.canLoadMore) {
                                        lastPostId = post.id
                                        homeViewModel.loadFeed(lastPostId)
                                    }
                                }
                                if (isLoading) {
                                    item {
                                        CustomCircularProgressIndicator()
                                    }
                                }
                            }
                        )
                    }

                    PullRefreshIndicator(
                        refreshing = isRefreshing,
                        state = refreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }

    when (val result = profileResult) {
        is Result.Success -> {
            result.data?.user?.let { sharedViewModel.setUser(it) }
        }

        else -> {
        }
    }

}
