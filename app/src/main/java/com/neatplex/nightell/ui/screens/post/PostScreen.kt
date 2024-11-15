package com.neatplex.nightell.ui.screens.post

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.widget.CustomSimpleButton
import com.neatplex.nightell.ui.component.media.AudioPlayer
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.PostEntity
import com.neatplex.nightell.service.ServiceManager
import com.neatplex.nightell.ui.component.comment.CommentCard
import com.neatplex.nightell.ui.screens.profile.getUserImagePainter
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.Constant
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.DatabaseViewModel
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.ui.viewmodel.UIEvent
import com.neatplex.nightell.utils.toJson
import java.io.IOException

@Composable
fun PostScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    postId: Int,
    mediaViewModel: MediaViewModel,
    serviceManager: ServiceManager,
) {
    val postViewModel: PostViewModel = hiltViewModel()
    val isLoading by postViewModel.isLoading.observeAsState(true)
    val isFetchingPost by postViewModel.isFetching.observeAsState(true)
    val postDetailResult by postViewModel.postDetailResult.observeAsState()
    val isServiceRunning by mediaViewModel.isServiceRunning.collectAsState()


    var post by remember { mutableStateOf<Post?>(null) }
    var isPostExist by remember { mutableStateOf(true) }
    var txtLoadFailure by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        postViewModel.getPostDetail(postId)
    }

    LaunchedEffect(postDetailResult) {
        postDetailResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    post = result.data!!.post
                    isPostExist = true
                }
                is Result.Failure -> {
                    isPostExist = result.code != 404
                    txtLoadFailure = if (result.code == 404) "Post not found" else "Something went wrong"
                }
            }
        }
    }

    if (isFetchingPost) {
        LoadingScreen()
    } else {
        if (isLoading) {
            LoadingScreen()
        }
        if (isPostExist && post != null) {
            PostContent(
                navController = navController,
                sharedViewModel = sharedViewModel,
                post = post!!,
                postViewModel = postViewModel,
                postId = postId,
                mediaViewModel = mediaViewModel,
                serviceManager = serviceManager,
                isServiceRunning = isServiceRunning,
                isEditing = isEditing,
                onEditingChange = { isEditing = it }
            )
        } else {
            ErrorScreen(txtLoadFailure)
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CustomCircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp),
                tint = colorResource(id = R.color.night),
                imageVector = Icons.Filled.Info,
                contentDescription = "error"
            )
            Text(text = message, fontSize = 17.sp)
        }
    }
}

@Composable
fun PostContent(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    post: Post,
    postViewModel: PostViewModel,
    postId: Int,
    mediaViewModel: MediaViewModel,
    serviceManager: ServiceManager,
    isServiceRunning: Boolean,
    isEditing: Boolean,
    onEditingChange: (Boolean) -> Unit,
) {
    val userId = sharedViewModel.user.value?.id ?: return
    val databaseViewModel: DatabaseViewModel = hiltViewModel()
    val menuExpanded = remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(0) }
    var likesCountNew by remember { mutableStateOf(0) }
    var likeId by remember { mutableStateOf<Int?>(null) }
    var icon by remember { mutableStateOf(Icons.Filled.FavoriteBorder) }
    var editedTitle by remember { mutableStateOf(post.title) }
    var editedDescription by remember { mutableStateOf(post.description ?: "") }
    val focusRequester = remember { FocusRequester() }
    var titleError by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf(listOf<Comment>()) }

    // Fetch likes and bookmark state
    LaunchedEffect(Unit) {
        postViewModel.showLikes(postId)
        postViewModel.getComments(postId)
    }

    val likesCountResult by postViewModel.showLikesResult.observeAsState()
    val likeResult by postViewModel.likeResult.observeAsState()
    val unlikeResult by postViewModel.unlikeResult.observeAsState()
    val deleteCommentResult by postViewModel.deleteCommentResult.observeAsState()
    val postDeleteResult by postViewModel.postDeleteResult.observeAsState()
    val postUpdateResult by postViewModel.postUpdateResult.observeAsState()
    val getComments by postViewModel.getCommentsResult.observeAsState(emptyList())
    val commentResult by postViewModel.sendCommentResult.observeAsState()
    val isCommentLoading by postViewModel.isCommentLoading.observeAsState()

    LaunchedEffect(likesCountResult) {
        likesCountResult?.let { result ->
            if (result is Result.Success) {
                val likes = result.data?.likes.orEmpty()
                likesCount = likes.size
                isLiked = likes.any { it.user_id == userId }
                icon = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                likeId = likes.find { it.user_id == userId }?.id
                likesCountNew = likesCount
            }
        }
    }

    LaunchedEffect(likeResult) {
        likeResult?.let {
            if (it is Result.Success) {
                isLiked = true
                likeId = it.data!!.like.id
                // Ensure UI is updated
                icon = Icons.Filled.Favorite
            } else {
                // Handle error
                isLiked = false
            }
        }
    }

    LaunchedEffect(unlikeResult) {
        unlikeResult?.let {
            if (it is Result.Success) {
                isLiked = false
                likeId = null
                // Ensure UI is updated
                icon = Icons.Filled.FavoriteBorder
            } else {
                // Handle error
                isLiked = true
            }
        }
    }

    LaunchedEffect(getComments) {
        comments = comments + getComments
    }

    LaunchedEffect(deleteCommentResult) {
        deleteCommentResult?.let { result ->
            if (result is Result.Success) {
                comments = comments.filter { it.id != result.data }
            }
        }
    }

    LaunchedEffect(commentResult) {
        commentResult?.let {
            if (it is Result.Success) {
                comments = listOf(it.data!!.comment) + comments
            }
        }
    }

    AppTheme {
        Scaffold(
            topBar = {
                PostTopBar(
                    navController = navController,
                    post = post,
                    userId = userId,
                    menuExpanded = menuExpanded,
                    databaseViewModel = databaseViewModel,
                    onEditingChange = onEditingChange,
                    postViewModel = postViewModel
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
                            postId = postId,
                            userId = userId,
                            likesCountNew = likesCountNew,
                            isLiked = isLiked,
                            icon = icon,
                            onLikeClick = {
                                if (isLiked) {
                                    // Unlike the post
                                    likeId?.let {
                                        postViewModel.deleteLike(it)
                                        isLiked = false
                                        icon = Icons.Filled.FavoriteBorder
                                        likesCountNew -= 1
                                    }
                                } else {
                                    // Like the post
                                    postViewModel.like(post.id)
                                    isLiked = true
                                    icon = Icons.Filled.Favorite
                                    likesCountNew += 1
                                }
                            },
                            mediaViewModel = mediaViewModel,
                            serviceManager = serviceManager,
                            isServiceRunning = isServiceRunning)

                        PostDescription(
                            isEditing = isEditing,
                            editedTitle = editedTitle,
                            editedDescription = editedDescription,
                            onTitleChange = { editedTitle = it },
                            onDescriptionChange = { editedDescription = it },
                            onSaveClick = {
                                if (editedTitle.isEmpty()) {
                                    titleError = true
                                    focusRequester.requestFocus() // Focus on title field if empty
                                } else {
                                    titleError = false
                                    if (editedTitle != post.title || editedDescription != post.description) {
                                        // Update the post only if there are changes
                                        postViewModel.updatePost(post.id, editedTitle, editedDescription)
                                    } else {
                                        // Close editing mode if there are no changes
                                        onEditingChange(false)
                                    }
                                }
                            },
                            titleError = titleError,
                            focusRequester = focusRequester
                        )

                        // Handle update result
                        postUpdateResult?.let {
                            if (it is Result.Success) {
                                // Close editing mode if update is successful
                                navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
                                navController.popBackStack()
                                navController.navigate("postScreen/${post.id}")
                                onEditingChange(false)
                            }
                        }

                        // Display comments
                        comments.forEach { comment ->
                            CommentCard(
                                comment = comment,
                                userId = userId,
                                onDeleteClick = { commentId ->
                                    postViewModel.deleteComment(commentId)
                                }
                            )
                        }

                        if (comments.size > 9 && postViewModel.canLoadMore) {
                            val lastCommentId = comments.last().id
                            if(isCommentLoading == true){
                                CustomCircularProgressIndicator()
                            }
                            Text(
                                text = "Load more",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        postViewModel.getComments(postId, lastCommentId)
                                    }
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
                            IconButton(onClick = {
                                if (commentText.isNotEmpty()) {
                                    postViewModel.sendComment(postId, commentText)
                                    commentText = ""
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "send message")
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

    postDeleteResult?.let {
        if (it is Result.Success) {
            navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
            navController.popBackStack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTopBar(
    navController: NavController,
    post: Post,
    userId: Int,
    menuExpanded: MutableState<Boolean>,
    databaseViewModel: DatabaseViewModel,
    onEditingChange: (Boolean) -> Unit,
    postViewModel: PostViewModel
) {
    var isBookmarked by remember { mutableStateOf(false) }
    var bookmarkIcon by remember { mutableStateOf(R.drawable.bookmark_border) }

    // Fetch the bookmark state from the database when the composable is first composed
    LaunchedEffect(post.id) {
        databaseViewModel.getPostById(post.id) { postEntity ->
            isBookmarked = postEntity != null
            bookmarkIcon = if (isBookmarked) R.drawable.bookmark else R.drawable.bookmark_border
        }
    }

    TopAppBar(
        title = { Text(text = "", fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
                onEditingChange(false)
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (userId == post.user.id) {
                IconButton(onClick = { menuExpanded.value = true }, modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false },
                    ) {
                        DropdownMenuItem(onClick = {
                            onEditingChange(true)
                            menuExpanded.value = false
                        }) {
                            Text(text = stringResource(id = R.string.edit))
                        }
                        DropdownMenuItem(onClick = {
                            postViewModel.deletePost(post.id)
                            menuExpanded.value = false
                            navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
                            navController.popBackStack()
                        }) {
                            Text(text = stringResource(id = R.string.delete))
                        }
                    }
                }
            } else {
                IconButton(onClick = {
                    val entity = PostEntity(
                        post.id,
                        post.title,
                        post.user.username,
                        post.image?.path
                    )
                    if (!isBookmarked) {
                        databaseViewModel.savePost(entity)
                        isBookmarked = true
                        bookmarkIcon = R.drawable.bookmark
                    } else {
                        databaseViewModel.unsavePost(entity)
                        isBookmarked = false
                        bookmarkIcon = R.drawable.bookmark_border
                    }
                }) {
                    Icon(
                        painter = painterResource(bookmarkIcon),
                        contentDescription = "save audio",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}


@Composable
fun PostImage(post: Post) {
    val imagePath = Constant.Files_URL + (post.image?.path ?: "")
    AsyncImage(
        model = imagePath,
        contentDescription = "Story Image",
        placeholder = painterResource(R.drawable.slider),
        error = painterResource(R.drawable.slider),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun PostDetails(
    navController: NavController,
    post: Post,
    postId: Int,
    userId: Int,
    likesCountNew: Int,
    isLiked: Boolean,
    icon: ImageVector,
    onLikeClick: () -> Unit,
    mediaViewModel: MediaViewModel,
    serviceManager: ServiceManager,
    isServiceRunning: Boolean,
) {
    val audioPath = Constant.Files_URL + post.audio.path

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageResource = getUserImagePainter(post.user)

        Image(
            painter = imageResource,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            text = post.user.username,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable {
                    if (userId != post.user.id) {
                        val userJson = post.user.toJson()
                        navController.navigate("userScreen/${Uri.encode(userJson)}")
                    }
                }
        )

        Spacer(Modifier.weight(1f))

        Text(text = likesCountNew.toString())

        IconButton(onClick = onLikeClick) {
            Icon(
                icon, contentDescription = "Like",
                modifier = Modifier.size(42.dp),
                tint = if (isLiked) Color.Red else Color.Gray
            )
        }
    }

    if (audioPath.isNotEmpty()) {

        var isAudioLoading by remember { mutableStateOf(true) }
        var isAudioPrepared by remember { mutableStateOf(false) }
        var totalDuration by remember { mutableStateOf(0L) }

        DisposableEffect(audioPath) {
            val mediaPlayer = MediaPlayer()
            val listener = MediaPlayer.OnPreparedListener {
                totalDuration = mediaPlayer.duration.toLong()
                isAudioLoading = false
                isAudioPrepared = true
            }

            mediaPlayer.setOnPreparedListener(listener)
            try {
                mediaPlayer.setDataSource(audioPath)
                mediaPlayer.prepareAsync()
            } catch (e: IOException) {
                e.printStackTrace()
                isAudioLoading = false
            }

            onDispose {
                mediaPlayer.release()
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            if (isAudioLoading) {
                androidx.compose.material.LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = colorResource(id = R.color.night),
                )
            } else if (isAudioPrepared && !isAudioLoading) {
                val isSame = postId.toString() == mediaViewModel.currentPostId

                if (isSame) {
                    AudioPlayer(
                        durationString = mediaViewModel.formatDuration(mediaViewModel.duration),
                        playResourceProvider = {
                            if (mediaViewModel.isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                        },
                        progressProvider = {
                            Pair(mediaViewModel.progress, mediaViewModel.progressString)
                        },
                        onUiEvent = mediaViewModel::onUIEvent,
                    )
                } else {
                    AudioPlayer(
                        durationString = mediaViewModel.formatDuration(totalDuration),
                        playResourceProvider = { R.drawable.baseline_play_arrow_24 },
                        progressProvider = { Pair(0f, "00:00") },
                        onUiEvent = {
                            if (!isServiceRunning) {
                                serviceManager.startMediaService()
                            }
                            mediaViewModel.loadData(audioPath, post.image?.path ?: "", post.title, postId.toString())
                            mediaViewModel.onUIEvent(UIEvent.PlayPause)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PostDescription(
    isEditing: Boolean,
    editedTitle: String,
    editedDescription: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    titleError: Boolean,
    focusRequester: FocusRequester
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        if (isEditing) {
            // Title input
            androidx.compose.material.OutlinedTextField(
                value = editedTitle.take(30), // Limit to 30 characters
                onValueChange = { newValue ->
                    if (newValue.length <= 30) onTitleChange(newValue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text("Title") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White.copy(0.3f),
                    textColor = Color.Black,
                    focusedIndicatorColor = if (titleError) colorResource(id = R.color.purple_light) else colorResource(id = R.color.night), // Pink bottom border if error
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = colorResource(id = R.color.night),
                    errorCursorColor = colorResource(id = R.color.purple_light),
                    errorIndicatorColor = colorResource(id = R.color.purple_light) // Pink for error state
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                isError = titleError
            )

            if (titleError) {
                Text(
                    text = "Title can't be empty",
                    color = colorResource(id = R.color.purple_light), // Pink error message
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Caption input (description)
            androidx.compose.material.OutlinedTextField(
                value = editedDescription, // No need to restrict characters in real-time
                onValueChange = { newValue ->
                    // Update the value without restriction; we will sanitize on save
                    onDescriptionChange(newValue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Caption", color = Color.Black) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White.copy(0.3f),
                    focusedBorderColor = colorResource(id = R.color.night), // Pink bottom border if error
                    textColor = Color.Black,
                    cursorColor = colorResource(id = R.color.night)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                maxLines = 10 // Allow multiple lines
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomSimpleButton(
                onClick = {
                    // Sanitize the description before saving
                    val sanitizedDescription = sanitizeDescription(editedDescription)
                    onDescriptionChange(sanitizedDescription) // Apply sanitized description
                    onSaveClick()
                },
                text = "Save Changes"
            )
        } else {
            Text(
                text = editedTitle.take(30), // Display up to 30 characters
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = editedDescription.take(150) // Display up to 150 characters
            )
        }
    }
}

// Function to sanitize the description
fun sanitizeDescription(description: String): String {
    // Replace more than 3 consecutive newlines with 3 newlines
    val withoutExtraNewlines = description.replace(Regex("\n{4,}"), "\n\n\n")

    // Trim any trailing newlines
    return withoutExtraNewlines.trimEnd()
}
