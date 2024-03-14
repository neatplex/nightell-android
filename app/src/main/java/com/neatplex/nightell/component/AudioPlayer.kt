package com.neatplex.nightell.component

import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun AudioPlayer(navController: NavController, audioPath: String) {


    val context = LocalContext.current
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var musicDuration by remember { mutableStateOf(0) }
    val handler = remember { Handler() }

    LaunchedEffect(audioPath) {
        if (audioPath.isNotEmpty() && audioPath.length > 40) {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(context, Uri.parse(audioPath))
            mediaPlayer.prepare()
            musicDuration = mediaPlayer.duration
            player = mediaPlayer
        }
    }

    DisposableEffect(Unit) {
        val runnable = object : Runnable {
            override fun run() {
                player?.let { mediaPlayer ->
                    currentPosition = mediaPlayer.currentPosition
                    if (isPlaying) {
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        }
        player?.setOnCompletionListener {
            // Handle audio completion
        }
        if (isPlaying) {
            handler.post(runnable)
        }
        onDispose {
            handler.removeCallbacks(runnable)
            player?.release()
        }
    }

    if (audioPath.isNotEmpty() && audioPath.length > 40) {
        Column(
            modifier = Modifier.heightIn(200.dp,300.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display current position and duration
            Text(
                text = "${currentPosition / 1000} / ${musicDuration / 1000} seconds",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )

            // SeekBar
            Slider(
                value = currentPosition.toFloat(),
                onValueChange = {
                    player?.seekTo(it.toInt())
                    currentPosition = it.toInt()
                },
                valueRange = 0f..musicDuration.toFloat(),
                onValueChangeFinished = {
                    // Handle seek bar change finished
                }
            )

            // Play/Pause button
            Button(
                onClick = {
                    player?.let { mediaPlayer ->
                        isPlaying = !isPlaying
                        if (isPlaying) {
                            mediaPlayer.start()
                        } else {
                            mediaPlayer.pause()
                        }
                    }
                }
            ) {
                Text(text = if (isPlaying) "Pause" else "Play")
            }
        }
    } else {
        Text("No audio file available")
    }
}