package com.neatplex.nightell.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.neatplex.nightell.ui.component.common.EmptyState
import com.neatplex.nightell.ui.component.common.SectionHeader
import com.neatplex.nightell.ui.theme.WordmarkStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.ui.component.post.RecentPostCard
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.post.HomePostCard
import com.neatplex.nightell.domain.model.Post
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

    Scaffold(
        topBar = { HomeTopBar(navController) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { space ->
        if (isLoading && feed.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(space), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            HomeContent(
                space = space,
                feed = feed,
                isLoading = isLoading,
                isRefreshing = feedState.isRefreshing,
                canLoadMore = feedState.canLoadMore,
                error = feedState.error,
                navController = navController,
                onRefresh = { homeViewModel.refresh() },
                onLoadMore = homeViewModel::loadMore,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.nightell),
                style = WordmarkStyle,
                color = MaterialTheme.colorScheme.onBackground,
            )
        },
        actions = {
            IconButton(onClick = { navController.navigate(Routes.BOOKMARKS) }) {
                Icon(
                    painter = painterResource(R.drawable.bookmark_border),
                    contentDescription = "Saved stories",
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    space: PaddingValues,
    feed: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    canLoadMore: Boolean,
    error: String?,
    navController: NavController,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
) {
    val refreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = onRefresh)
    // Feature the newest few only when there are enough left over for the list below.
    val featuredCount = if (feed.size > FEATURED_COUNT) FEATURED_COUNT else minOf(1, feed.size)
    val featured = feed.take(featuredCount)
    val latest = feed.drop(featuredCount)
    val openPost: (Post) -> Unit = { navController.navigate(Routes.post(it.id)) }

    Box(
        modifier = Modifier
            .padding(space)
            .pullRefresh(refreshState)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            if (feed.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Rounded.Refresh,
                        title = if (error != null) "Couldn't load stories" else "No stories yet",
                        message = if (error != null) "Pull down to try again." else "Stories from people you follow will appear here.",
                    )
                }
            }
            if (featured.isNotEmpty()) {
                item { SectionHeader("Tonight's picks") }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(featured, key = { it.id }) { post ->
                            val width = if (featured.size == 1) Modifier.fillParentMaxWidth() else Modifier.width(300.dp)
                            Box(modifier = width.height(200.dp)) {
                                RecentPostCard(post = post, isLoading = false, onPostClicked = openPost)
                            }
                        }
                    }
                }
            }
            if (latest.isNotEmpty()) {
                item { SectionHeader("Latest stories") }
                itemsIndexed(latest, key = { _, post -> post.id }) { index, post ->
                    HomePostCard(post = post, isLoading = false, onPostClicked = openPost)
                    if (index == latest.lastIndex && !isLoading && canLoadMore) {
                        LaunchedEffect(post.id) { onLoadMore() }
                    }
                }
            }
            if (isLoading && feed.isNotEmpty()) {
                item { CustomCircularProgressIndicator() }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.primary,
        )
    }
}

private const val FEATURED_COUNT = 3
