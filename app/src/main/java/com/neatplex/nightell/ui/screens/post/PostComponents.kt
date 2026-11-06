package com.neatplex.nightell.ui.screens.post

import android.media.MediaPlayer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.media.AudioPlayer
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.screens.profile.getUserImagePainter
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import java.io.IOException
import com.neatplex.nightell.ui.viewmodel.formatDuration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.navigation.Routes

/* Building blocks of the post screen. */

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTopBar(
    navController: NavController,
    post: Post,
    isOwnPost: Boolean,
    isSaved: Boolean,
    menuExpanded: MutableState<Boolean>,
    onToggleSaved: () -> Unit,
    onDelete: () -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }
    val bookmarkIcon = if (isSaved) R.drawable.bookmark else R.drawable.bookmark_border

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
            if (isOwnPost) {
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

                            navController.navigate(Routes.editPost(post.id, post.title, post.description))                        })
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
                                    // The screen closes once the delete succeeds.
                                    onDelete()
                                    showDialog.value = false
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
                IconButton(onClick = onToggleSaved) {
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
    AsyncImage(
        model = post.image?.url,
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
    currentUserId: Int?,
    likesCount: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    mediaViewModel: MediaViewModel,
) {
    val audioPath = post.audio.url
    val icon = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
    val player by mediaViewModel.playerState.collectAsStateWithLifecycle()

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
                    if (currentUserId != post.user.id) {
                        navController.navigate(Routes.user(post.user.id))
                    }
                }
        )

        Spacer(Modifier.weight(1f))

        Text(text = likesCount.toString())

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
                if (player.currentPostId == post.id) {
                    AudioPlayer(
                        durationString = player.durationText,
                        playResourceProvider = {
                            if (player.isPlaying) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24
                        },
                        progressProvider = { Pair(player.progress, player.positionText) },
                        onUiEvent = mediaViewModel::onUIEvent,
                    )
                } else {
                    AudioPlayer(
                        durationString = formatDuration(totalDuration),
                        playResourceProvider = { R.drawable.round_play_arrow_24 },
                        progressProvider = { Pair(0f, "00:00") },
                        onUiEvent = { mediaViewModel.play(post) }
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
