package com.neatplex.nightell.ui.component.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.common.StoryArtwork
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.UIEvent

/**
 * The floating mini player above the bottom bar: cover, title, progress and a
 * play/pause button. Tapping it opens the story.
 */
@Composable
fun PlayerBox(
    mediaViewModel: MediaViewModel,
    modifier: Modifier = Modifier,
    onMaximizeClick: () -> Unit,
) {
    val player by mediaViewModel.playerState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        Column(modifier = Modifier.clickable(onClick = onMaximizeClick)) {
            Row(
                modifier = Modifier.padding(start = 10.dp, end = 8.dp, top = 10.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StoryArtwork(imageUrl = player.imageUrl, shape = MaterialTheme.shapes.small, modifier = Modifier.size(44.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = player.title.ifEmpty { "Now playing" },
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${player.positionText} / ${player.durationText}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                FilledIconButton(onClick = { mediaViewModel.onUIEvent(UIEvent.PlayPause) }) {
                    Icon(
                        painterResource(if (player.isPlaying) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24),
                        contentDescription = if (player.isPlaying) "Pause" else "Play",
                    )
                }
            }
            LinearProgressIndicator(
                progress = { player.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 8.dp),
                strokeCap = StrokeCap.Round,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}
