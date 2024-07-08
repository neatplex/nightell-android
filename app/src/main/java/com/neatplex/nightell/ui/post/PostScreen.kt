package com.neatplex.nightell.ui.post


import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material.TextField
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomSimpleButton
import com.neatplex.nightell.component.media.BottomPlayerUI
import com.neatplex.nightell.component.media.formatTime
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.PostEntity
import com.neatplex.nightell.ui.theme.AppTheme
import com.neatplex.nightell.utils.Constant
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.DatabaseViewModel
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.ui.viewmodel.UIEvent
import com.neatplex.nightell.utils.toJson
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    postId: Int,
    mediaViewModel: MediaViewModel,
    startService: () -> Unit,
) {
    val postViewModel: PostViewModel = hiltViewModel()

    // States
    var post by remember { mutableStateOf<Post?>(null) }
    var isPostExist by remember { mutableStateOf(true) }
    // Observe post detail result
    val postDetailResult by postViewModel.postDetailResult.observeAsState()

    // Fetch post and related data
    LaunchedEffect(Unit) {
        postViewModel.getPostDetail(postId)
    }

    // Update UI based on post detail result
    LaunchedEffect(postDetailResult) {
        postDetailResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    isPostExist = true
                    post = result.data!!.post
                }
                is Result.Error -> {
                    isPostExist = false
                    // Handle error case
                }
                else -> {
                }
            }
        }
    }

    // Show loading indicator while loading
    if (postDetailResult is Result.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    // Render UI when post exists
    if (isPostExist && post != null) {
        val mainPost: Post = post!!
        val databaseViewModel: DatabaseViewModel = hiltViewModel()
        val userId = sharedViewModel.user.value?.id ?: return // Ensure userId is not null
        val menuExpanded = remember { mutableStateOf(false) }

        // States for like and unlike handling
        var isLiked by remember { mutableStateOf(false) }
        var likesCount by remember { mutableStateOf(0) }
        var likesCountNew by remember { mutableStateOf(0) }
        var likeId by remember { mutableStateOf<Int?>(null) }
        var icon by remember { mutableStateOf(Icons.Filled.FavoriteBorder) }

        // Bookmark state variables
        var isBookmarked by remember { mutableStateOf(false) }
        var bookmarkIcon by remember { mutableStateOf(R.drawable.bookmark_border) }

        // Fetch likes and user info
        LaunchedEffect(Unit) {
            postViewModel.showLikes(postId)
        }

        // Load the bookmark state from the database
        LaunchedEffect(postId) {
            databaseViewModel.getPostById(postId) { postEntity ->
                isBookmarked = postEntity != null
                bookmarkIcon = if (isBookmarked) R.drawable.bookmark else R.drawable.bookmark_border
            }
        }

        val postDeleteResult by postViewModel.postDeleteResult.observeAsState()
        val likesCountResult by postViewModel.showLikesResult.observeAsState()
        val unlikeResult by postViewModel.unlikeResult.observeAsState()
        val likeResult by postViewModel.likeResult.observeAsState()
        val postUpdateResult by postViewModel.postUpdateResult.observeAsState()

        // Update UI based on likes result
        LaunchedEffect(likesCountResult) {
            likesCountResult?.let { result ->
                when (result) {
                    is Result.Success -> {
                        val likes = result.data?.likes.orEmpty()
                        likesCount = likes.size
                        likesCountNew = likesCount
                        isLiked = likes.any { it.user_id == userId }
                        icon = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                        likeId = if (isLiked) likes.find { it.user_id == userId }?.id else null
                    }
                    is Result.Error -> {
                        // Handle error case
                    }
                    else -> {
                    }
                }
            }
        }

        LaunchedEffect(likeResult) {
            likeResult?.let {
                if (it is Result.Success) {
                    isLiked = true
                    likeId = it.data?.like?.id
                    icon = Icons.Filled.Favorite
                } else {
                    isLiked = false
                }
            }
        }

        LaunchedEffect(unlikeResult) {
            unlikeResult?.let {
                if (it is Result.Success) {
                    isLiked = false
                    icon = Icons.Filled.FavoriteBorder
                    likeId = null
                } else {
                    isLiked = true
                }
            }
        }

        var editedTitle by remember { mutableStateOf(mainPost.title) }
        var editedDescription by remember { mutableStateOf(mainPost.description ?: "") }
        var isEditing by remember { mutableStateOf(false) }

        val audioPath = Constant.Files_URL + (post?.audio?.path ?: "")
        val imagePath = Constant.Files_URL + (post?.image?.path ?: "")

        AppTheme {
            Scaffold(
                topBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, clip = false)
                            .background(Color.White)
                    ) {
                        TopAppBar(
                            title = {},
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    androidx.compose.material3.Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            actions = {
                                if (userId == post?.user?.id) {
                                    IconButton(
                                        onClick = { menuExpanded.value = true },
                                        modifier = Modifier.size(24.dp)
                                    ) {
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
                                                isEditing = true
                                                menuExpanded.value = false
                                            }) {
                                                Text(text = stringResource(id = R.string.edit))
                                            }
                                            DropdownMenuItem(onClick = {
                                                postViewModel.deletePost(post!!.id)
                                                menuExpanded.value = false
                                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                                    "postChanged",
                                                    true
                                                )
                                                navController.popBackStack()
                                            }) {
                                                Text(text = stringResource(id = R.string.delet))
                                            }
                                        }
                                    }
                                } else {
                                    IconButton(onClick = {
                                        val entity = PostEntity(postId, post!!.title, post!!.user.username, post!!.image?.path)
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
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                },
                content = { space ->
                    Box(
                        modifier = Modifier.padding(space)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val imageResource = rememberAsyncImagePainter(
                                model = post?.image?.path?.let { imagePath }
                                    ?: R.drawable.slider
                            )
                            Image(
                                painter = imageResource,
                                contentDescription = "Story Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentScale = ContentScale.Crop
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val imgResource =
                                    rememberAsyncImagePainter(model = R.drawable.default_profile_image)
                                Image(
                                    painter = imgResource,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = post?.user?.username ?: "",
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .clickable {
                                            sharedViewModel.user.value?.id?.let { userId ->
                                                if (userId != post?.user?.id) {
                                                    val userJson = post!!.user.toJson()
                                                    navController.navigate(
                                                        "userScreen/${
                                                            Uri.encode(
                                                                userJson
                                                            )
                                                        }"
                                                    )
                                                }
                                            }
                                        }
                                )

                                Spacer(Modifier.weight(1f))

                                Text(text = likesCountNew.toString())

                                IconButton(onClick = {
                                    if (!isLiked) {
                                        if(likesCountNew <= likesCount) {
                                            likesCountNew++
                                        }
                                        postViewModel.like(post!!.id)
                                        isLiked = true
                                        icon = Icons.Filled.Favorite
                                    } else {
                                        if(likesCountNew >= 1) {
                                            likesCountNew--
                                        }
                                        likeId?.let { id ->
                                            postViewModel.deleteLike(id)
                                        }
                                        isLiked = false
                                        icon = Icons.Filled.FavoriteBorder
                                    }
                                }) {
                                    Icon(
                                        icon, contentDescription = "Like",
                                        modifier = Modifier.size(42.dp),
                                        tint = if (isLiked) Color.Red else Color.Gray
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                if (!mediaViewModel.initial) {
                                    startService()
                                }
                                // Observe data changes and load media when available
                                val isSame = postId.toString() == mediaViewModel.currentPostId

                                if (isSame) {
                                    BottomPlayerUI(
                                        durationString = mediaViewModel.formatDuration(mediaViewModel.duration),
                                        playResourceProvider = {
                                            if (mediaViewModel.isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                                        },
                                        progressProvider = {
                                            Pair(
                                                mediaViewModel.progress,
                                                mediaViewModel.progressString
                                            )
                                        },
                                        onUiEvent = mediaViewModel::onUIEvent
                                    )
                                } else {
                                    var totalDuration by remember { mutableStateOf(0L) }
                                    val mediaPlayer = MediaPlayer()
                                    try {
                                        mediaPlayer.setDataSource(audioPath)
                                        mediaPlayer.prepare()
                                        totalDuration = mediaPlayer.duration.toLong()
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        // Handle the error, e.g., show an error message to the user
                                        // You can also log the error for debugging purposes
                                    }
                                    totalDuration = mediaPlayer.duration.toLong()
                                    BottomPlayerUI(
                                        modifier = Modifier.fillMaxWidth(),
                                        durationString = formatTime(millis = totalDuration), // Default duration string
                                        playResourceProvider = {
                                            R.drawable.baseline_play_arrow_24 // Always show play icon
                                        },
                                        progressProvider = {
                                            Pair(0f, "00:00") // Default progress
                                        },
                                        onUiEvent = {
                                            // Replace audio with the one in mediaViewModel when play button is clicked
                                            mediaViewModel.loadData(
                                                audioPath,
                                                imagePath,
                                                editedTitle,
                                                postId.toString()
                                            )
                                            // Automatically play the audio when it's loaded
                                            mediaViewModel.onUIEvent(UIEvent.PlayPause)
                                        }
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 24.dp)
                            ) {
                                if (isEditing) {
                                    TextField(
                                        value = editedTitle,
                                        onValueChange = {
                                            editedTitle =
                                                it.take(25) // Limiting input to 250 characters
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        label = {
                                            Text("Title", color = Color.Black) // Changing label color
                                        },
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            backgroundColor = Color.White.copy(0.5f), // Changing background color
                                            textColor = Color.Black, // Changing text color
                                            focusedBorderColor = Color.White,
                                            cursorColor = colorResource(id = R.color.night)
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Text
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    TextField(
                                        value = editedDescription,
                                        onValueChange = {
                                            editedDescription =
                                                it.take(250) // Limiting input to 250 characters
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        label = {
                                            Text("Caption", color = Color.Black) // Changing label color
                                        },
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            backgroundColor = Color.White.copy(0.5f), // Changing background color
                                            textColor = Color.Black, // Changing text color
                                            focusedBorderColor = Color.White,
                                            cursorColor = colorResource(id = R.color.night)
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Text
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    CustomSimpleButton(
                                        onClick = {
                                            if (editedTitle != post!!.title || editedDescription != post!!.description) {
                                                postViewModel.editPost(
                                                    post!!.id,
                                                    editedTitle,
                                                    editedDescription
                                                )
                                            } else {
                                                isEditing = false
                                            }
                                        },
                                        text = "Save Changes"
                                    )
                                } else {
                                    Text(
                                        text = editedTitle,
                                        fontSize = 22.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = editedDescription
                                    )
                                }
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                    }
                }
            )
        }

        postDeleteResult?.let {
            if (it is Result.Success) {
                navController.popBackStack()
            }
        }

        postUpdateResult?.let {
            if (it is Result.Success) {
                isEditing = false
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "postChanged",
                    true
                )
            }
        }
    } else if(!isPostExist){
        Box(modifier = Modifier.fillMaxSize()){
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
                    contentDescription = "Choose Audio File"
                )
                Text(text = "This post isn't available anymore!", fontSize = 17.sp)
            }
        }
    }
}