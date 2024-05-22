package com.neatplex.nightell.ui.post


import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomSimpleButton
import com.neatplex.nightell.component.media.BottomPlayerUI
import com.neatplex.nightell.component.media.formatTime
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.screens.BottomNavigationHeight
import com.neatplex.nightell.utils.Constant
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.shared.MediaViewModel
import com.neatplex.nightell.ui.shared.SharedViewModel
import com.neatplex.nightell.ui.profile.ProfileViewModel
import com.neatplex.nightell.ui.shared.UIEvent


@Composable
fun PostScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    data: Post?,
    mediaViewModel: MediaViewModel,
    startService: () -> Unit
) {

    var changeState by remember { mutableStateOf(0) }

    // Initialize ViewModels
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val postViewModel: PostViewModel = hiltViewModel()
    val likeViewModel: LikeViewModel = hiltViewModel()

    // Retrieve necessary data
    val post = data!!
    val userId = sharedViewModel.user.value!!.id
    val menuExpanded = remember { mutableStateOf(false) }

    // States for like and unlike handling
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(0) }
    var likeId by remember { mutableStateOf<Int?>(null) }
    var icon by remember { mutableStateOf(Icons.Filled.FavoriteBorder) }

    // Fetch likes and user info
    LaunchedEffect(Unit) {
        likeViewModel.showLikes(post.id)
        profileViewModel.getUserInfo(post.user_id)
    }

    //Observe likes and user info results
    val likesCountResult by likeViewModel.showLikesResult.observeAsState()
    val unlikeResult by likeViewModel.unlikeResult.observeAsState()
    val likeResult by likeViewModel.likeResult.observeAsState()
    val userInfoResult by profileViewModel.showUserInfoResult.observeAsState()
    val postUpdateResult by postViewModel.storePostResult.observeAsState()
    val bottomBarHeight = BottomNavigationHeight()


    // Update UI based on likes result
    LaunchedEffect(likesCountResult) {
        likesCountResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    val likes = result.data?.likes.orEmpty()
                    likesCount = likes.size
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
                likeId = it.data!!.like.id
                icon = Icons.Filled.Favorite
            }else {
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

    var editedTitle by remember { mutableStateOf(post.title ?: "") }
    var editedDescription by remember { mutableStateOf(post.description ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    val audioPath = Constant.Files_URL + post.audio.path
    val imagePath = Constant.Files_URL + post.image?.path
    val postId = post.id

    Column(modifier = Modifier.fillMaxSize()) {

        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(Modifier.weight(1f))

            // Conditional toggle menu for edit/delete
            if (userId == post.user_id) {
                // Show edit and delete menu here
                Box(modifier = Modifier.padding(8.dp)) {
                    IconButton(
                        onClick = { menuExpanded.value = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.Black
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        DropdownMenuItem(onClick = {
                            isEditing = true
                            menuExpanded.value = false
                        }) {
                            Text(text = stringResource(id = R.string.edit))
                        }
                        DropdownMenuItem(onClick = {
                            postViewModel.deletePost(post.id)
                            menuExpanded.value = false
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "postDeleted",
                                true
                            )
                            navController.popBackStack()
                        }) {
                            Text(text = stringResource(id = R.string.delet))
                        }
                    }
                }
            }
        }

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(bottom = bottomBarHeight)
        ) {
            val imageResource = rememberImagePainter(
                data = post.image?.path?.let { imagePath }
                    ?: R.drawable.ic_launcher_background
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
                    .padding(all = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                userInfoResult?.let { userResult ->
                    when (userResult) {
                        is Result.Success -> {
                            Spacer(modifier = Modifier.width(16.dp))
                            userResult.data?.user?.let {
                                val imageResource =
                                    rememberImagePainter(data = R.drawable.default_profile_image)

                                Image(
                                    painter = imageResource,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = it.username,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .clickable {
                                            // Navigate to another page when "Followers" is clicked
                                            if (sharedViewModel.user.value?.id != it.id) {
                                                navController.navigate("userScreen/${it.id}")
                                            }
                                        }
                                )
                            }
                        }

                        else -> {
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                
                Text(text = likesCount.toString())

                IconButton(onClick = {
                    if (!isLiked) {
                        likeViewModel.like(post.id)
                        isLiked = true
                        likesCount++
                        icon = Icons.Filled.Favorite
                    } else {
                        likeId?.let { id ->
                            likeViewModel.deleteLike(id)
                        }
                        isLiked = false
                        likesCount--
                        icon = Icons.Filled.FavoriteBorder
                    }
                }) {
                    Icon(
                        icon, contentDescription = "Like",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {

                if (!mediaViewModel.initial) {
                    startService()
                }
                // Observe data changes and load media when available
                var isSame = postId.toString() == mediaViewModel.currentPostId

                if(isSame){
                    BottomPlayerUI(
                        durationString = mediaViewModel.formatDuration(mediaViewModel.duration),
                        playResourceProvider = {
                            if (mediaViewModel.isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
                        },
                        progressProvider = {
                            Pair(
                                mediaViewModel.progress,
                                mediaViewModel.progressString
                            )
                        },
                        onUiEvent = mediaViewModel::onUIEvent)
                } else {

                    var totalDuration by remember { mutableStateOf(0L) }

                    val mediaPlayer = MediaPlayer().apply {
                        setDataSource(audioPath)
                        prepare()
                        totalDuration = duration.toLong()
                    }

                    totalDuration = mediaPlayer.duration.toLong()

                    BottomPlayerUI(
                        modifier = Modifier.fillMaxWidth(),
                        durationString = formatTime(millis = totalDuration), // Default duration string
                        playResourceProvider = {
                            android.R.drawable.ic_media_play // Always show play icon
                        },
                        progressProvider = {
                            Pair(0f, "00:00") // Default progress
                        },
                        onUiEvent = {
                            // Replace audio with the one in mediaViewModel when play button is clicked
                            mediaViewModel.loadData(audioPath, imagePath, editedTitle, postId.toString())
                            // Automatically play the audio when it's loaded
                            mediaViewModel.onUIEvent(UIEvent.PlayPause)
                        }
                    )
                }
            }

            if (isEditing) {
                TextField(
                    value = editedTitle,
                    onValueChange = {
                        editedTitle = it.take(25) // Limiting input to 250 characters
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Title", color = Color.Black) // Changing label color
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White.copy(0.5f), // Changing background color
                        textColor = Color.Black, // Changing text color
                        focusedBorderColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = editedDescription,
                    onValueChange = {
                        editedDescription = it.take(250) // Limiting input to 250 characters
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Caption", color = Color.Black) // Changing label color
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White.copy(0.5f), // Changing background color
                        textColor = Color.Black, // Changing text color
                        focusedBorderColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    )
                )
                Row(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    CustomSimpleButton(
                        onClick = {
                            if (editedTitle != post.title || editedDescription != post.description) {
                                postViewModel.editPost(post.id, editedTitle, editedDescription)
                                isEditing = false
                            }
                        },
                        text = "Save Changes"
                    )
                }

            } else {
                Text(
                    text = editedTitle,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = editedDescription,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    postUpdateResult?.let {
        if (it is Result.Success) {
            changeState ++
            isEditing = false
        }
    }

}

