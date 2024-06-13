package com.neatplex.nightell.component.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.ui.shared.UIEvent

@Composable
internal fun PlayerControl(
    playResourceProvider : () -> Int,
    onUiEvent : (UIEvent) -> Unit
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = android.R.drawable.ic_media_rew),
            contentDescription = "backward button",
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = { onUiEvent(UIEvent.Backward) })
                .padding(12.dp)
                .size(34.dp))
        Image(painter = painterResource(id = playResourceProvider()), contentDescription = "play/pause Button",
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = {onUiEvent(UIEvent.PlayPause)} )
                .padding(8.dp)
                .size(56.dp))
        Icon(painter = painterResource(id = android.R.drawable.ic_media_ff),
            contentDescription = "forward button",
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = { onUiEvent(UIEvent.Forward) })
                .padding(12.dp)
                .size(34.dp))
    }
}