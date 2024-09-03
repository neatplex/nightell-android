package com.neatplex.nightell.ui.search

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.CustomSearchField
import com.neatplex.nightell.component.UserCard
import com.neatplex.nightell.component.post.HomePostCard
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.toJson

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    val posts by searchViewModel.posts.observeAsState(emptyList())
    val users by searchViewModel.users.observeAsState(emptyList())
    val isLoading by searchViewModel.isLoading.observeAsState(false)
    var lastPostId by remember { mutableStateOf<Int?>(null) }
    var lastUserId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier.background(color = Color.White)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth(),
            containerColor = Color.White
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Posts") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Users") }
            )
        }

        SearchBar(
            query = query,
            onQueryChange = { newQuery ->
                query = newQuery
                lastPostId = null
                lastUserId = null
                if (selectedTab == 0) {
                    searchViewModel.searchPost(newQuery, null, false)
                } else {
                    searchViewModel.searchUser(newQuery, null, false)
                }
            }
        )

        if (selectedTab == 0) {
            posts?.let {
                PostList(
                    posts = it,
                    isLoading = isLoading,
                    onPostSelected = { selectedPost ->
                        sharedViewModel.setPost(selectedPost)
                        navController.navigate("postScreen/${selectedPost.id}")
                    },
                    onLoadMore = {
                        if (!isLoading) {
                            lastPostId = posts!!.lastOrNull()?.id
                            searchViewModel.searchPost(query, lastPostId, true)
                        }
                    })
            }
        } else {
            users?.let {
                UserList(
                    users = it,
                    isLoading = isLoading,
                    onUserSelected = { selectedUser ->
                        if (sharedViewModel.user.value?.id != selectedUser.id) {
                            val userJson = selectedUser.toJson()
                            navController.navigate("userScreen/${Uri.encode(userJson)}")
                        }
                    },
                    onLoadMore = {
                        if (!isLoading) {
                            lastUserId = users!!.lastOrNull()?.id
                            searchViewModel.searchUser(query, lastUserId, true)
                        }
                    })
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(modifier = Modifier.padding(16.dp)) {
        CustomSearchField(
            value = query,
            onValueChange = onQueryChange,
            onSearch = { onQueryChange(query) }
        )
    }
}

@Composable
fun PostList(
    posts: List<Post>,
    isLoading: Boolean,
    onPostSelected: (Post) -> Unit,
    onLoadMore: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(posts) { index, post ->
            HomePostCard(post = post, onPostClicked = onPostSelected, isLoading = isLoading)
            if (index == posts.size - 1 && !isLoading) {
                onLoadMore()
            }
        }
        if (isLoading) {
            item { CustomCircularProgressIndicator() }
        }
    }
}

@Composable
fun UserList(
    users: List<User>,
    isLoading: Boolean,
    onUserSelected: (User) -> Unit,
    onLoadMore: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(users) { index, user ->
            UserCard(user = user, onUserClicked = onUserSelected)
            if (index == users.size - 1 && !isLoading) {
                onLoadMore()
            }
        }
        if (isLoading) {
            item { CustomCircularProgressIndicator() }
        }
    }
}
