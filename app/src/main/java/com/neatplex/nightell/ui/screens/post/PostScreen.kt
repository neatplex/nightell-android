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
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
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
                else -> {}
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
) {
    val userId = sharedViewModel.user.value?.id ?: return
    val databaseViewModel: DatabaseViewModel = hiltViewModel()
    val menuExpanded = remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(0) }
    var likesCountNew by remember { mutableStateOf(0) }
    var likeId by remember { mutableStateOf<Int?>(null) }
    var icon by remember { mutableStateOf(Icons.Filled.FavoriteBorder) }
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
                            isServiceRunning = isServiceRunning
                        )

                        PostDescription( post.title, post.description ?: "")

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
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Send,
                                    contentDescription = "send message"
                                )                            }
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
    postViewModel: PostViewModel
) {
    var isBookmarked by remember { mutableStateOf(false) }
    var bookmarkIcon by remember { mutableStateOf(R.drawable.bookmark_border) }
    val showDialog = remember { mutableStateOf(false) }

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

                            navController.navigate("editPostScreen/${post.id}/${Uri.encode(post.title)}/${Uri.encode(post.description?.ifEmpty { " " })}")                        })
                        {
                            Text(text = stringResource(id = R.string.edit))
                        }
                        DropdownMenuItem(onClick = {
                            showDialog.value = true
                            menuExpanded.value = false
                        }) {
                            Text(text = stringResource(id = R.string.delete))
                        }
                    }
                    if (showDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showDialog.value = false },
                            title = { Text(text = "Delete Post") },
                            text = { Text(text = "Are you sure you want to delete this post?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    postViewModel.deletePost(post.id)
                                    showDialog.value = false
                                    navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
                                    navController.popBackStack()
                                }) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog.value = false }) {
                                    Text("Cancel")
                                }
                            },
                            containerColor = Color.White
                        )
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
                            if (mediaViewModel.isPlaying) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24
                        },
                        progressProvider = {
                            Pair(mediaViewModel.progress, mediaViewModel.progressString)
                        },
                        onUiEvent = mediaViewModel::onUIEvent,
                    )
                } else {
                    AudioPlayer(
                        durationString = mediaViewModel.formatDuration(totalDuration),
                        playResourceProvider = { R.drawable.round_play_arrow_24 },
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
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = title.take(30), // Display up to 30 characters
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = description.take(150) // Display up to 150 characters
        )
    }
}
