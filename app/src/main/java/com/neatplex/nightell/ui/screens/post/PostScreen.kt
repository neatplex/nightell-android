package com.neatplex.nightell.ui.screens.post

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import com.neatplex.nightell.ui.component.common.SectionHeader
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Send
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.component.comment.CommentCard
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.common.PagedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PostScreen(
    navController: NavController,
    mediaViewModel: MediaViewModel,
    postViewModel: PostViewModel = hiltViewModel(),
) {
    val uiState by postViewModel.uiState.collectAsStateWithLifecycle()
    val commentsState by postViewModel.comments.collectAsStateWithLifecycle()

    // Reload after the post was edited, and let the previous screen know too.
    val postChanged = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("postChanged", false)
        ?.collectAsStateWithLifecycle()
    LaunchedEffect(postChanged?.value) {
        if (postChanged?.value == true) {
            postViewModel.loadPost()
            navController.currentBackStackEntry?.savedStateHandle?.set("postChanged", false)
            navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
        }
    }

    // Close the screen once the post is deleted.
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
            navController.popBackStack()
        }
    }

    val post = uiState.post
    when {
        uiState.isLoading && post == null -> LoadingScreen()
        post != null -> PostContent(
            navController = navController,
            post = post,
            uiState = uiState,
            comments = commentsState,
            postViewModel = postViewModel,
            mediaViewModel = mediaViewModel,
        )
        else -> ErrorScreen(uiState.loadError.orEmpty())
    }
}

@Composable
fun PostContent(
    navController: NavController,
    post: Post,
    uiState: PostUiState,
    comments: PagedState<Comment>,
    postViewModel: PostViewModel,
    mediaViewModel: MediaViewModel,
) {
    val menuExpanded = remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            PostTopBar(
                navController = navController,
                post = post,
                isOwnPost = uiState.isOwnPost,
                isSaved = uiState.isSaved,
                menuExpanded = menuExpanded,
                onToggleSaved = postViewModel::toggleSaved,
                onDelete = postViewModel::deletePost,
            )
        },
        bottomBar = {
            CommentComposer(
                text = commentText,
                onTextChange = { commentText = it },
                isSending = uiState.isSendingComment,
                onSend = {
                    postViewModel.sendComment(commentText)
                    commentText = ""
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { space ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = space.calculateTopPadding(), bottom = space.calculateBottomPadding() + 16.dp),
        ) {
            item { PostImage(post) }
            item {
                PostDetails(
                    navController = navController,
                    post = post,
                    currentUserId = uiState.currentUserId,
                    likesCount = uiState.likesCount,
                    isLiked = uiState.isLiked,
                    onLikeClick = postViewModel::toggleLike,
                    mediaViewModel = mediaViewModel,
                )
            }
            item { PostDescription(post.title, post.description.orEmpty()) }
            item {
                SectionHeader(
                    if (post.commentsCount > 0) "Comments · ${post.commentsCount}" else "Comments",
                )
            }
            if (comments.items.isEmpty() && !comments.isLoading) {
                item {
                    Text(
                        "No comments yet. Be the first to say something nice.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    )
                }
            }
            items(comments.items, key = { it.id }) { comment ->
                CommentCard(
                    comment = comment,
                    userId = uiState.currentUserId ?: -1,
                    onDeleteClick = postViewModel::deleteComment,
                )
            }
            item {
                if (comments.isLoading) {
                    CustomCircularProgressIndicator()
                } else if (comments.canLoadMore && comments.items.isNotEmpty()) {
                    TextButton(
                        onClick = postViewModel::loadMoreComments,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    ) { Text("Show more comments") }
                }
            }
        }
    }
}

/** A rounded comment box pinned above the keyboard. */
@Composable
private fun CommentComposer(
    text: String,
    onTextChange: (String) -> Unit,
    isSending: Boolean,
    onSend: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer, tonalElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Add a comment…") },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.extraLarge,
                maxLines = 4,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
            Spacer(Modifier.width(8.dp))
            FilledIconButton(onClick = onSend, enabled = text.isNotBlank() && !isSending) {
                Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send comment")
            }
        }
    }
}
