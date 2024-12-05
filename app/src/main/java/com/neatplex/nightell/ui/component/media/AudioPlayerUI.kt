package com.neatplex.nightell.ui.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.neatplex.nightell.ui.viewmodel.UIEvent

@Composable
fun AudioPlayer(
    durationString: String,
    playResourceProvider: () -> Int,
    progressProvider: () -> Pair<Float, String>,
    onUiEvent: (UIEvent) -> Unit
) {
    val (progress, progressString) = progressProvider()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerBar(
            progress = progress,
            durationString = durationString,
            progressString = progressString,
            onUiEvent = onUiEvent
        )

        PlayerControl(
            playResourceProvider = playResourceProvider,
            onUiEvent = onUiEvent
        )
    }
}