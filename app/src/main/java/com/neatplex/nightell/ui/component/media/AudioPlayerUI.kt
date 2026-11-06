package com.neatplex.nightell.ui.component.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.viewmodel.UIEvent

/** The full player on a story's page: seek bar with times, and rewind/play/forward. */
@Composable
fun AudioPlayer(
    durationString: String,
    playResourceProvider: () -> Int,
    progressProvider: () -> Pair<Float, String>,
    onUiEvent: (UIEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (progress, progressString) = progressProvider()
    // While dragging, show the thumb where the finger is rather than the playback position.
    var dragValue by remember { mutableStateOf<Float?>(null) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Slider(
                value = dragValue ?: progress,
                onValueChange = { dragValue = it },
                onValueChangeFinished = {
                    dragValue?.let { onUiEvent(UIEvent.UpdateProgress(it)) }
                    dragValue = null
                },
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TimeLabel(progressString)
                TimeLabel(durationString)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onUiEvent(UIEvent.Backward) }, modifier = Modifier.size(52.dp)) {
                    Icon(painterResource(R.drawable.round_fast_rewind_24), "Rewind", Modifier.size(32.dp))
                }
                FilledIconButton(onClick = { onUiEvent(UIEvent.PlayPause) }, modifier = Modifier.size(72.dp)) {
                    Icon(painterResource(playResourceProvider()), "Play or pause", Modifier.size(40.dp))
                }
                IconButton(onClick = { onUiEvent(UIEvent.Forward) }, modifier = Modifier.size(52.dp)) {
                    Icon(painterResource(R.drawable.round_fast_forward_24), "Fast forward", Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
private fun TimeLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}
