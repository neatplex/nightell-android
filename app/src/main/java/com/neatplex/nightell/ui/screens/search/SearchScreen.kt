package com.neatplex.nightell.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.ui.component.common.EmptyState
import com.neatplex.nightell.ui.component.post.HomePostCard
import com.neatplex.nightell.ui.component.user.UserCard
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.widget.CustomSearchField
import com.neatplex.nightell.ui.navigation.Routes
import com.neatplex.nightell.ui.viewmodel.SessionViewModel

private val tabs = listOf("Stories", "People")

/** Search stories and people. */
@Composable
fun SearchScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel,
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val postsState by searchViewModel.posts.collectAsStateWithLifecycle()
    val usersState by searchViewModel.users.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (postsState.items.isEmpty()) searchViewModel.searchPosts(query)
    }

    fun search(text: String, tab: Int) {
        if (tab == 0) searchViewModel.searchPosts(text) else searchViewModel.searchUsers(text)
    }

    Column(modifier = Modifier.statusBarsPadding()) {
        Text(
            "Discover",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
        )
        CustomSearchField(
            value = query,
            onValueChange = { newQuery ->
                query = newQuery
                search(newQuery, selectedTab)
            },
            onSearch = { search(query, selectedTab) },
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(top = 12.dp),
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        if (selectedTab != index) {
                            selectedTab = index
                            search(query, index)
                        }
                    },
                    text = { Text(label) },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (selectedTab == 0) {
            PostList(
                posts = postsState.items,
                isLoading = postsState.isLoading,
                onPostSelected = { navController.navigate(Routes.post(it.id)) },
                onLoadMore = searchViewModel::loadMorePosts,
            )
        } else {
            UserList(
                users = usersState.items,
                isLoading = usersState.isLoading,
                onUserSelected = { selectedUser ->
                    if (sessionViewModel.currentUser.value?.id != selectedUser.id) {
                        navController.navigate(Routes.user(selectedUser.id))
                    }
                },
                onLoadMore = searchViewModel::loadMoreUsers,
            )
        }
    }
}

@Composable
fun PostList(
    posts: List<Post>,
    isLoading: Boolean,
    onPostSelected: (Post) -> Unit,
    onLoadMore: () -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
        if (posts.isEmpty() && !isLoading) {
            item { EmptyState(icon = Icons.Rounded.Search, title = "No stories found", message = "Try a different word.") }
        }
        itemsIndexed(posts, key = { _, post -> post.id }) { index, post ->
            HomePostCard(post = post, isLoading = false, onPostClicked = onPostSelected)
            if (index == posts.lastIndex && !isLoading) {
                LaunchedEffect(post.id) { onLoadMore() }
            }
        }
        if (isLoading) item { CustomCircularProgressIndicator() }
    }
}

@Composable
fun UserList(
    users: List<User>,
    isLoading: Boolean,
    onUserSelected: (User) -> Unit,
    onLoadMore: () -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
        if (users.isEmpty() && !isLoading) {
            item { EmptyState(icon = Icons.Rounded.Person, title = "No people found", message = "Search by name or username.") }
        }
        itemsIndexed(users, key = { _, user -> user.id }) { index, user ->
            UserCard(user = user, onUserClicked = onUserSelected)
            if (index == users.lastIndex && !isLoading) {
                LaunchedEffect(user.id) { onLoadMore() }
            }
        }
        if (isLoading) item { CustomCircularProgressIndicator() }
    }
}
