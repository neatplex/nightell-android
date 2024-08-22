package com.neatplex.nightell.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.UIEvent
import kotlin.math.roundToInt

@Composable
fun PlayerBox(mediaViewModel: MediaViewModel, modifier: Modifier = Modifier) {
    // State to hold the offset of the PlayerBox
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Modifier to handle dragging
    val dragModifier = Modifier
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                //offsetX += dragAmount.x
                offsetY += dragAmount.y
            }
        }

    // Combine the drag modifier with the existing modifier
    CompactAudioPlayer(
        modifier = modifier
            .then(dragModifier)
            .padding(16.dp), // Padding around the entire player box
        durationString = mediaViewModel.formatDuration(mediaViewModel.duration),
        playResourceProvider = {
            if (mediaViewModel.isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
        },
        progressProvider = {
            Pair(mediaViewModel.progress, mediaViewModel.progressString)
        },
        onUiEvent = mediaViewModel::onUIEvent,
    )
}

@Composable
fun CompactAudioPlayer(
    modifier: Modifier = Modifier,
    durationString: String,
    playResourceProvider: () -> Int,
    progressProvider: () -> Pair<Float, String>,
    onUiEvent: (UIEvent) -> Unit
) {
    val (progress, progressString) = progressProvider()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.8f), shape = RoundedCornerShape(16.dp)) // Apply a curved border with rounded corners
            .padding(24.dp), // Padding inside the Row
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Slider(
                value = progress,
                onValueChange = { newValue ->
                    onUiEvent(UIEvent.UpdateProgress(newProgress = newValue))
                },
                modifier = Modifier.height(8.dp).padding(top = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White, // White color for the thumb of the slider
                    activeTrackColor = Color.White, // White color for the active track of the slider
                    inactiveTrackColor = Color.Gray // Gray color for the inactive track of the slider
                )
            )

            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = progressString, style = MaterialTheme.typography.labelMedium, color = Color.White) // White text color
                Text(text = durationString, style = MaterialTheme.typography.labelMedium, color = Color.White) // White text color
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            modifier = Modifier
                .clickable(onClick = { onUiEvent(UIEvent.PlayPause) })
                .size(32.dp),
            painter = painterResource(id = playResourceProvider()),
            contentDescription = "play/pause Button",
            tint = Color.White // White color for the icon
        )
    }
}
