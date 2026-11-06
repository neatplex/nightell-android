package com.neatplex.nightell.ui.screens.home

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.ui.component.post.RecentPostCard
import com.neatplex.nightell.ui.theme.AppTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.post.HomePostCard
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.theme.feelFree
import com.neatplex.nightell.ui.navigation.Routes

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    sessionViewModel: SessionViewModel,
) {
    val feedState by homeViewModel.feed.collectAsStateWithLifecycle()
    val feed = feedState.items
    val isLoading = feedState.isLoading

    // Observe changes in the savedStateHandle
    val postChanged = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("postChanged", false)
        ?.collectAsStateWithLifecycle()

    LaunchedEffect(postChanged?.value) {
        if (postChanged?.value == true) {
            homeViewModel.refresh(showIndicator = false)
            navController.currentBackStackEntry?.savedStateHandle?.set("postChanged", false)
        }
    }

    LaunchedEffect(Unit) {
        sessionViewModel.refreshCurrentUser()
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
                        space = space,
                        feed = feed,
                        isLoading = isLoading,
                        isRefreshing = feedState.isRefreshing,
                        canLoadMore = feedState.canLoadMore,
                        navController = navController,
                        onRefresh = { homeViewModel.refresh() },
                        onLoadMore = homeViewModel::loadMore,
                    )
                }
            }
        )
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
                    text = stringResource(id = R.string.nightell),
                    fontFamily = feelFree,
                    fontSize = 40.sp,
                    color = Color.Black,
                )
            },
            actions = {
                IconButton(onClick = { navController.navigate(Routes.BOOKMARKS) }) {
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
    canLoadMore: Boolean,
    navController: NavController,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
) {
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh,
    )

    val lazyColumnState = rememberLazyListState()
    var lazyRowHeight by remember { mutableStateOf(280.dp) } // Initial height of LazyRow
    val lazyRowHeightAnim by animateDpAsState(targetValue = lazyRowHeight)

    // Gradually change the height based on scroll
    LaunchedEffect(lazyColumnState.firstVisibleItemIndex, lazyColumnState.firstVisibleItemScrollOffset) {
        val firstItemVisible = lazyColumnState.firstVisibleItemIndex == 0
        val scrollOffset = lazyColumnState.firstVisibleItemScrollOffset

        if (firstItemVisible) {
            // Adjust height based on the scroll offset of the first item
            lazyRowHeight = 280.dp - (scrollOffset / 3).dp
            if (lazyRowHeight < 0.dp) {
                lazyRowHeight = 0.dp
            }
        } else if (lazyRowHeight != 0.dp) {
            // Once scrolled beyond the first item, hide the LazyRow
            lazyRowHeight = 0.dp
        }
    }

    Box(
        modifier = Modifier
            .padding(space)
            .pullRefresh(refreshState)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(lazyRowHeightAnim)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(feed.take(3)) { _, post ->
                        RecentPostCard(post = post, isLoading = isLoading) { selectedPost ->
                            navController.navigate(Routes.post(selectedPost.id))
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
                            navController.navigate(Routes.post(selectedPost.id))
                        }
                        if (index == feed.drop(3).size - 1 && !isLoading && canLoadMore) {
                            onLoadMore()
                        }
                    }
                    if (isLoading) {
                        item { CustomCircularProgressIndicator() }
                    }
                }
            )
        }
    }

    if (isRefreshing) {
        CustomCircularProgressIndicator()
    }

    // Call scrollToTop() or scrollToBottom() as needed
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            lazyColumnState.scrollToItem(0)
        }
    }
}

