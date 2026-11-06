package com.neatplex.nightell.ui.screens.bookmark

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.neatplex.nightell.ui.component.common.AppTopBar
import com.neatplex.nightell.ui.component.common.EmptyState
import com.neatplex.nightell.ui.component.post.SavedPostCard
import com.neatplex.nightell.ui.navigation.Routes
import com.neatplex.nightell.ui.viewmodel.BookmarkViewModel

/** Stories bookmarked on this device. */
@Composable
fun BookmarkedScreen(navController: NavController) {
    val bookmarkViewModel: BookmarkViewModel = hiltViewModel()
    val savedPosts by bookmarkViewModel.savedPosts.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(title = "Saved stories", onBack = { navController.popBackStack() }) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            if (savedPosts.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Rounded.FavoriteBorder,
                        title = "Nothing saved yet",
                        message = "Tap the bookmark on a story to keep it here for later.",
                    )
                }
            }
            items(savedPosts, key = { it.id }) { post ->
                SavedPostCard(
                    post = post,
                    onPostClicked = { navController.navigate(Routes.post(it.id)) },
                    onDeleteClicked = { bookmarkViewModel.remove(it.id) },
                )
            }
        }
    }
}
