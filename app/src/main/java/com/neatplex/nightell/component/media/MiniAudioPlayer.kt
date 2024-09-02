package com.neatplex.nightell.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.ui.viewmodel.UIEvent

@Composable
fun PlayerBox(
              mediaViewModel: MediaViewModel,
              modifier: Modifier = Modifier,
              onMaximizeClick: () -> Unit
) {

    CompactAudioPlayer(
        modifier = modifier.padding(bottom = 0.dp), // Padding around the entire player box
        durationString = mediaViewModel.formatDuration(mediaViewModel.duration),
        playResourceProvider = {
            if (mediaViewModel.isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
        },
        progressProvider = {
            Pair(mediaViewModel.progress, mediaViewModel.progressString)
        },
        onUiEvent = mediaViewModel::onUIEvent,
        onMaximizeClick = {
            onMaximizeClick()
        }
    )
}

@Composable
fun CompactAudioPlayer(
    modifier: Modifier = Modifier,
    durationString: String,
    playResourceProvider: () -> Int,
    progressProvider: () -> Pair<Float, String>,
    onUiEvent: (UIEvent) -> Unit,
    onMaximizeClick: () -> Unit
) {
    val (progress, progressString) = progressProvider()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.8f)) // Apply a curved border with rounded corners
            .padding(horizontal = 16.dp, vertical = 24.dp), // Padding inside the Row
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            modifier = Modifier
                .clickable(onClick = { onUiEvent(UIEvent.PlayPause) })
                .size(32.dp),
            painter = painterResource(id = playResourceProvider()),
            contentDescription = "play/pause Button",
            tint = Color.White // White color for the icon
        )

        Spacer(modifier = Modifier.width(8.dp))

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
                .clickable(onClick = onMaximizeClick)
                .size(32.dp),
            painter = painterResource(id = R.drawable.baseline_open_in_new_24), // Use your maximize icon here
            contentDescription = "Maximize Button",
            tint = Color.White
        )
    }
}
