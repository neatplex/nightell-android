package com.neatplex.nightell.ui.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.component.post.SavedPostCard
import com.neatplex.nightell.domain.model.PostEntity
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.ui.viewmodel.DatabaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkedScreen(
    navController: NavController
){
    var savedPostList by remember { mutableStateOf<List<PostEntity>>(emptyList()) }
    val databaseViewModel: DatabaseViewModel = hiltViewModel()

    // Load the bookmark state from the database
    LaunchedEffect(Unit) {
        databaseViewModel.getAllPosts { postEntityList ->
            savedPostList = postEntityList
        }
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
                        title = { Text(text = "Liked Posts") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                ) {
                    Column(modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)) {
                        // LazyColumn for the remaining posts
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 65.dp),
                            modifier = Modifier.fillMaxSize(),
                            content = {
                                itemsIndexed(savedPostList) { index, post ->
                                    SavedPostCard(post = post) {
                                        val postId = it.id
                                        navController.navigate(
                                            "postScreen/${postId}"
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
}