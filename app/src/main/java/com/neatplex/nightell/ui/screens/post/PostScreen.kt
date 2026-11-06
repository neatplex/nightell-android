package com.neatplex.nightell.ui.screens.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.component.comment.CommentCard
import com.neatplex.nightell.ui.theme.AppTheme
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

    AppTheme {
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
            content = { space ->
                Box(modifier = Modifier
                    .padding(space)
                    .verticalScroll(rememberScrollState())) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        PostImage(post)
                        PostDetails(
                            navController = navController,
                            post = post,
                            currentUserId = uiState.currentUserId,
                            likesCount = uiState.likesCount,
                            isLiked = uiState.isLiked,
                            onLikeClick = postViewModel::toggleLike,
                            mediaViewModel = mediaViewModel,
                        )

                        PostDescription(post.title, post.description ?: "")

                        comments.items.forEach { comment ->
                            CommentCard(
                                comment = comment,
                                userId = uiState.currentUserId ?: -1,
                                onDeleteClick = postViewModel::deleteComment,
                            )
                        }

                        if (comments.isLoading) {
                            CustomCircularProgressIndicator()
                        } else if (comments.canLoadMore && comments.items.isNotEmpty()) {
                            Text(
                                text = "Load more",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { postViewModel.loadMoreComments() }
                                    .padding(16.dp),
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            bottomBar = {
                Row {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Add a comment") },
                        trailingIcon = {
                            IconButton(
                                enabled = !uiState.isSendingComment,
                                onClick = {
                                    if (commentText.isNotBlank()) {
                                        postViewModel.sendComment(commentText)
                                        commentText = ""
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Send,
                                    contentDescription = "send message"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        )
    }
}
