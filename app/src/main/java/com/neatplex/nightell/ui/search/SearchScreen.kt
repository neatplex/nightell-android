package com.neatplex.nightell.ui.search

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.CustomSearchField
import com.neatplex.nightell.component.post.HomePostCard
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.toJson


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val posts by searchViewModel.posts.observeAsState(emptyList())
    val isLoading by searchViewModel.isLoading.observeAsState(false)
    var lastPostId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        searchViewModel.search(query, null, false)

    }

    Column(
        modifier = Modifier.background(color = Color.White)
    ) {
        SearchBar(
            query = query,
            onQueryChange = { newQuery ->
                query = newQuery
                lastPostId = null
                searchViewModel.search(newQuery, null, false)
            }
        )

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
                        searchViewModel.search(query, lastPostId, true)
                    }
                }
            )
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
    onLoadMore: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 65.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(posts) { index, post ->
            HomePostCard(post = post, onPostClicked = onPostSelected)
            if (index == posts.size - 1 && !isLoading) {
                onLoadMore()
            }
        }
        if (isLoading) {
            item { CustomCircularProgressIndicator() }
        }
    }
}