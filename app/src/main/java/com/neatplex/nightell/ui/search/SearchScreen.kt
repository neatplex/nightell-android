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

    searchViewModel.search(query, null, false)

    Column(
        modifier = Modifier
            .background(color = Color.White)
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            CustomSearchField(
                value = query,
                onValueChange = { newQuery ->
                    query = newQuery
                    searchViewModel.search(newQuery, null, false)
                },
                onSearch = {
                    searchViewModel.search(query, null, false)
                }
            )
        }

        Row(Modifier.padding(vertical = 8.dp)) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 65.dp),
                modifier = Modifier.fillMaxSize(),
                content = {
                    itemsIndexed(posts!!) { index, post ->
                        HomePostCard(post = post) { selectedPost ->
                            sharedViewModel.setPost(selectedPost)
                            val postJson = selectedPost.toJson()
                            navController.navigate(
                                "postScreen/${Uri.encode(postJson)}"
                            )
                        }
                        if (index == posts!!.size - 1 && !isLoading) {
                            lastPostId = post.id
                            searchViewModel.search(query, lastPostId, true)
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

    }
}