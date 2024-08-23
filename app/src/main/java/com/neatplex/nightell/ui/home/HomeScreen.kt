package com.neatplex.nightell.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.component.post.RecentPostCard
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.post.HomePostCard
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.theme.feelFree

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    isPlayerBoxVisible: Boolean
) {
    val feed by homeViewModel.feed.observeAsState(emptyList())
    val isLoading by homeViewModel.isLoading.observeAsState(false)
    val isRefreshing by homeViewModel.isRefreshing.observeAsState(false)
    val profileResult by homeViewModel.profileData.observeAsState()
    val bottomPadding = if (isPlayerBoxVisible) 135.dp else 65.dp

    // Observe changes in the savedStateHandle
    val postChanged = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("postChanged")?.observeAsState()

    LaunchedEffect(postChanged?.value) {
        if (postChanged?.value == true) {
            homeViewModel.refreshFeed()
            navController.currentBackStackEntry?.savedStateHandle?.set("postChanged", false)
        }
    }

    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { homeViewModel.refreshFeed() }
    )

    LaunchedEffect(Unit) {
        homeViewModel.loadFeed(null)
        homeViewModel.fetchProfile()
    }

    AppTheme {
        Scaffold(
            topBar = { HomeTopBar(navController) },
            content = { space ->
                HomeContent(space, feed, isLoading, isRefreshing, refreshState, navController, sharedViewModel, homeViewModel, bottomPadding)
            }
        )
    }

    profileResult?.let { result ->
        when (result) {
            is Result.Success -> {
                result.data?.user?.let { sharedViewModel.setUser(it) }
            }
            else -> {
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, clip = false)
            .background(Color.White)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Nightell",
                    fontFamily = feelFree,
                    fontSize = 40.sp,
                    color = Color.Black,
                )
            },
            actions = {
                IconButton(onClick = { navController.navigate("bookmark") }) {
                    Icon(
                        painter = painterResource(R.drawable.bookmark),
                        contentDescription = "saved audio",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            )
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    space: PaddingValues,
    feed: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    refreshState: PullRefreshState,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel,
    bottomPadding: Dp
) {
    Box(
        modifier = Modifier
            .padding(space)
            .pullRefresh(refreshState)
    ) {
        Column {
            HomePosts(feed, isLoading, navController, sharedViewModel, homeViewModel, bottomPadding)
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun HomePosts(
    posts: List<Post>,
    isLoading: Boolean,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel,
    bottomPadding: Dp
) {

    LazyRow {
        itemsIndexed(posts.take(3)) { index, post ->
            RecentPostCard(post = post, isLoading) { selectedPost ->
                sharedViewModel.setPost(selectedPost)
                navController.navigate("postScreen/${post.id}")
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = bottomPadding),
        modifier = Modifier.fillMaxSize(),
        content = {
            itemsIndexed(posts.drop(3)) { index, post ->
                HomePostCard(post = post, isLoading = isLoading) { selectedPost ->
                    if (!isLoading) {
                        navController.navigate("postScreen/${selectedPost.id}")
                    }
                }
                if (index == posts.drop(3).size - 1 && !isLoading && homeViewModel.canLoadMore) {
                    homeViewModel.loadFeed(post.id)
                }
            }
            if (isLoading) {
                item { CustomCircularProgressIndicator() }
            }
        }
    )
}
