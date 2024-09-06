package com.neatplex.nightell.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
) {
    val feed by homeViewModel.feed.observeAsState(emptyList())
    val isLoading by homeViewModel.isLoading.observeAsState(false)
    val isRefreshing by homeViewModel.isRefreshing.observeAsState(false)
    val profileResult by homeViewModel.profileData.observeAsState()

    // Observe changes in the savedStateHandle
    val postChanged = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("postChanged")?.observeAsState()

    LaunchedEffect(postChanged?.value) {
        if (postChanged?.value == true) {
            navController.navigate("feed") {
                popUpTo("feed") { inclusive = true }
            }
            navController.currentBackStackEntry?.savedStateHandle?.set("postChanged", false)
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.loadFeed()
        homeViewModel.fetchProfile()
    }

    AppTheme {
        Scaffold(
            topBar = { HomeTopBar(navController) },
            content = { space ->
                if (isLoading && feed.isEmpty()) {
                    // Show a loading indicator while loading the feed for the first time
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(space),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    HomeContent(
                        space,
                        feed,
                        isLoading,
                        isRefreshing,
                        navController,
                        sharedViewModel,
                        homeViewModel
                    )
                }
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
    navController: NavController,
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel,
) {

    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            navController.navigate("feed") {
                popUpTo("feed") { inclusive = true }
            }
        }
    )

    val lazyRowState = rememberLazyListState()
    val lazyColumnState = rememberLazyListState()
    var lazyRowHeight by remember { mutableStateOf(280.dp) } // Initial height of LazyRow
    val lazyRowHeightAnim by animateDpAsState(targetValue = lazyRowHeight)

    // Track if the first item was seen after loading
    var firstItemSeenAfterLoad by remember { mutableStateOf(false) }

    // Monitor scroll state and LazyRow visibility
    LaunchedEffect(lazyColumnState.firstVisibleItemIndex, lazyColumnState.firstVisibleItemScrollOffset) {
        val currentIndex = lazyColumnState.firstVisibleItemIndex
        val currentOffset = lazyColumnState.firstVisibleItemScrollOffset

        if (currentIndex == 0 && currentOffset == 0 && !firstItemSeenAfterLoad) {
            // First item is fully visible
            lazyRowHeight = 280.dp
            firstItemSeenAfterLoad = true
        } else if (feed.size > 8 && currentIndex > 0 && lazyRowHeight != 0.dp) {
            // Scrolling down: hide the LazyRow
            lazyRowHeight = 0.dp
            firstItemSeenAfterLoad = false // Reset for next scroll-up
        } else if (feed.size > 8 && currentIndex == 0 && currentOffset > 0 && lazyRowHeight == 0.dp) {
            // If the first item is partially visible, keep the LazyRow hidden
            lazyRowHeight = 0.dp
        }
    }

    // Smooth Scroll Up/Down Logic
    val coroutineScope = rememberCoroutineScope()

    fun scrollToTop() {
        coroutineScope.launch {
            lazyColumnState.animateScrollToItem(0)
            lazyRowState.animateScrollToItem(0)
        }
    }

    Box(
        modifier = Modifier
            .padding(space)
            .pullRefresh(refreshState)
    ) {
        Column{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(lazyRowHeightAnim)
            ) {
                LazyRow(
                    state = lazyRowState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(feed.take(3)) { index, post ->
                        RecentPostCard(post = post, isLoading) { selectedPost ->
                            sharedViewModel.setPost(selectedPost)
                            navController.navigate("postScreen/${post.id}")
                        }
                    }
                }
            }

            LazyColumn(
                state = lazyColumnState,
                modifier = Modifier.fillMaxSize(),
                content = {
                    itemsIndexed(feed.drop(3)) { index, post ->
                        HomePostCard(post = post, isLoading = isLoading) { selectedPost ->
                            sharedViewModel.setPost(selectedPost)
                            navController.navigate("postScreen/${selectedPost.id}")
                        }
                        if (index == feed.drop(3).size - 1 && !isLoading && homeViewModel.canLoadMore) {
                            homeViewModel.loadFeed()
                        }
                    }
                    if (isLoading) {
                        item { CustomCircularProgressIndicator() }
                    }
                }
            )
        }
    }

    // Call scrollToTop() or scrollToBottom() as needed
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            scrollToTop()
        }
    }
}
