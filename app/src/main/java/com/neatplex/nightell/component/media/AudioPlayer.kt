package com.neatplex.nightell.component.media

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.theme.MyHorizontalGradiant
import com.neatplex.nightell.ui.theme.MySliderColors
import com.neatplex.nightell.ui.shared.MediaViewModel
import kotlinx.coroutines.delay


@Composable
fun AudioPlayer(audioPath: String,imagePath: String,title: String, postId: String, mediaViewModel: MediaViewModel) {

    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackPosition by remember { mutableStateOf(0L) }
    var totalDuration by remember { mutableStateOf(0L) }

    LaunchedEffect(audioPath) {

        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioPath)
            prepare()
            totalDuration = duration.toLong()

            setOnCompletionListener {
                isPlaying = false
                playbackPosition = 0
            }
        }

        totalDuration = mediaPlayer?.duration?.toLong() ?: 0

        while (true) {
            if (isPlaying) {
                mediaPlayer?.let { player ->
                    playbackPosition = player.currentPosition.toLong()
                }
            }
            delay(1000)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${formatTime(playbackPosition)} / ${formatTime(totalDuration)}",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

            // SeekBar
            Slider(
                value = playbackPosition.toFloat(),
                onValueChange = {
                    mediaPlayer?.seekTo(it.toInt())
                    playbackPosition = it.toLong()
                },
                valueRange = 0f..totalDuration.toFloat(),
                onValueChangeFinished = {
                    // Handle seek bar change finished
                },
                colors = MySliderColors()
            )

        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            Surface(
                elevation = 4.dp,
                shape = CircleShape
            ) {
                IconButton(
                    onClick = {
                        mediaViewModel.loadData(audioPath,imagePath,title, postId)
                    },
                    modifier = Modifier.size(80.dp)
                ) {
                    val horizontalGradientBrush = MyHorizontalGradiant()

                    Icon(
                        modifier = Modifier
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        horizontalGradientBrush,
                                        blendMode = BlendMode.SrcAtop
                                    )
                                }
                            },
                        painter = painterResource(
                            id = R.drawable.baseline_play_arrow_24
                        ),
                        contentDescription = "Play Sound"
                    )
                }
            }
        }
    }
}

// Utility function to format time in HH:MM:SS format
@Composable
fun formatTime(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    return String.format("%02d:%02d", minutes % 60, seconds % 60)
}
