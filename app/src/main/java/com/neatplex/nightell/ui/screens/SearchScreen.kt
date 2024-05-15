package com.neatplex.nightell.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.component.PostCard
import com.neatplex.nightell.ui.viewmodel.SearchViewModel
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.toJson



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(navController: NavController, sharedViewModel: SharedViewModel, searchViewModel: SearchViewModel = hiltViewModel()) {

    var query by remember { mutableStateOf("") }
    val searchResultPosts by searchViewModel.searchResult.observeAsState(emptyList())
    val isLoading by searchViewModel.isLoading.observeAsState(false)
    val bottomBarHeight = BottomNavigationHeight()



    Scaffold(
        topBar = {
            TopAppBar(
                  {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it
                            searchViewModel.search(query)},
                        label = { Text("Search") },
                        trailingIcon = {
                            IconButton(onClick = { searchViewModel.search(query) }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_search_24),
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                // Trigger search when user hits enter
                                searchViewModel.search(query)
                            }
                        )
                    )
                },
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth().height(80.dp),
                elevation = 4.dp
            )
        },
        content = { space ->
            Box(modifier = Modifier.padding(space)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = bottomBarHeight),
                        content = {
                            itemsIndexed(searchResultPosts) { index, post ->
                                if (post != null) {
                                    PostCard(post = post) { selectedPost ->
                                        sharedViewModel.setPost(selectedPost)
                                        val postJson = selectedPost.toJson()
                                        navController.navigate("postScreen/${Uri.encode(postJson)}")
                                    }
                                }
                            }

                            if (isLoading && searchResultPosts.isNotEmpty()) {
                                item {
                                    // Load more indicator
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
        }
    )
}