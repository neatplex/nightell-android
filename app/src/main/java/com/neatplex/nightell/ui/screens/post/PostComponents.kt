package com.neatplex.nightell.ui.screens.post

import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.component.common.StoryArtwork
import com.neatplex.nightell.ui.component.common.UserAvatar
import com.neatplex.nightell.ui.component.media.AudioPlayer
import com.neatplex.nightell.ui.component.widget.AlertDialogCustom
import com.neatplex.nightell.ui.navigation.Routes
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.formatDuration
import java.io.IOException

/* Building blocks of the post screen. */

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
        Spacer(Modifier.size(12.dp))
        Text(message, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
    }
}

/** Back button, and either a bookmark (others' posts) or an edit/delete menu (own posts). */
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
    var showDeleteDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (isOwnPost) {
                Box {
                    IconButton(onClick = { menuExpanded.value = true }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(expanded = menuExpanded.value, onDismissRequest = { menuExpanded.value = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.edit)) },
                            leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded.value = false
                                navController.navigate(Routes.editPost(post.id, post.title, post.description))
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete)) },
                            leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null) },
                            onClick = {
                                menuExpanded.value = false
                                showDeleteDialog = true
                            },
                        )
                    }
                }
            } else {
                IconButton(onClick = onToggleSaved) {
                    Icon(
                        painterResource(if (isSaved) R.drawable.bookmark else R.drawable.bookmark_border),
                        contentDescription = if (isSaved) "Remove bookmark" else "Bookmark",
                        tint = if (isSaved) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
    )

    if (showDeleteDialog) {
        AlertDialogCustom(
            onDismissRequest = { showDeleteDialog = false },
            onConfirmation = {
                // The screen closes once the delete succeeds.
                onDelete()
                showDeleteDialog = false
            },
            dialogTitle = "Delete this story?",
            dialogText = "It will be removed for everyone. This can't be undone.",
            confirmText = "Delete",
        )
    }
}

/** The story's cover, large and rounded. */
@Composable
fun PostImage(post: Post) {
    StoryArtwork(
        imageUrl = post.image?.url,
        contentDescription = "Cover of ${post.title}",
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .aspectRatio(1.15f),
    )
}

/** Title, author, like button and the audio player. */
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
    val player by mediaViewModel.playerState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            post.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 20.dp),
        )
        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small)
                    .clickable(enabled = currentUserId != post.user.id) {
                        navController.navigate(Routes.user(post.user.id))
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UserAvatar(post.user, size = 36.dp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(post.user.name.ifBlank { post.user.username }, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "@${post.user.username}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            FilterChip(
                selected = isLiked,
                onClick = onLikeClick,
                label = { Text(likesCount.toString()) },
                leadingIcon = {
                    Icon(
                        if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = if (isLiked) "Unlike" else "Like",
                        modifier = Modifier.size(18.dp),
                    )
                },
                shape = MaterialTheme.shapes.extraLarge,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.secondary,
                ),
            )
        }

        Spacer(Modifier.size(20.dp))
        StoryPlayer(post = post, player = player, mediaViewModel = mediaViewModel)
    }
}

@Composable
private fun StoryPlayer(post: Post, player: com.neatplex.nightell.ui.viewmodel.PlayerUiState, mediaViewModel: MediaViewModel) {
    if (player.currentPostId == post.id) {
        AudioPlayer(
            durationString = player.durationText,
            playResourceProvider = { if (player.isPlaying) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24 },
            progressProvider = { Pair(player.progress, player.positionText) },
            onUiEvent = mediaViewModel::onUIEvent,
        )
        return
    }

    // Not playing yet: read the length up front so the player can show it.
    var isLoading by remember(post.audio.url) { mutableStateOf(true) }
    var totalDuration by remember(post.audio.url) { mutableLongStateOf(0L) }
    DisposableEffect(post.audio.url) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnPreparedListener {
            totalDuration = it.duration.toLong()
            isLoading = false
        }
        mediaPlayer.setOnErrorListener { _, _, _ -> isLoading = false; true }
        try {
            mediaPlayer.setDataSource(post.audio.url)
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            isLoading = false
        }
        onDispose { mediaPlayer.release() }
    }

    Column {
        AudioPlayer(
            durationString = if (isLoading) "--:--" else formatDuration(totalDuration),
            playResourceProvider = { R.drawable.round_play_arrow_24 },
            progressProvider = { Pair(0f, "00:00") },
            onUiEvent = { mediaViewModel.play(post) },
        )
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp))
        }
    }
}

/** The story's caption. */
@Composable
fun PostDescription(title: String, description: String) {
    if (description.isBlank()) return
    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
    )
}
