package com.neatplex.nightell.ui.component.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.ui.viewmodel.UIEvent

@Composable
fun PlayerBar(
    progress : Float,
    durationString : String,
    progressString : String,
    onUiEvent : (UIEvent) -> Unit
) {
    val newProgressValue = remember { mutableStateOf(0f) }
    val useNewProgressValue = remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
                .height(10.dp) // height of the Slider's track
        ) {
            Slider(
                value = if (useNewProgressValue.value) newProgressValue.value else progress,
                onValueChange = { newValue ->
                    useNewProgressValue.value = true
                    newProgressValue.value = newValue
                    onUiEvent(UIEvent.UpdateProgress(newProgress = newValue))
                },
                onValueChangeFinished = {
                    useNewProgressValue.value = false
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = progressString)
            Text(text = durationString)
        }
    }
}