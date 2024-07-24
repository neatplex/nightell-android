package com.neatplex.nightell.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.theme.myLinearGradiant
import com.neatplex.nightell.ui.viewmodel.UIEvent

@Composable
internal fun PlayerControl(
    playResourceProvider : () -> Int,
    onUiEvent : (UIEvent) -> Unit
){
    val linearGradientBrush = myLinearGradiant()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clickable(onClick = { onUiEvent(UIEvent.Backward) })
                .size(42.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(linearGradientBrush, blendMode = BlendMode.SrcAtop)
                    }
                }
            ,
            painter = painterResource(id = R.drawable.baseline_fast_rewind_24),
            contentDescription = "backward button"
        )
        
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))

        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(CircleShape)
                .border(width = 2.dp, brush = myLinearGradiant(), shape = CircleShape),
            contentAlignment = Alignment.Center
        )
        {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(color = Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    modifier = Modifier
                        .clickable(onClick = { onUiEvent(UIEvent.PlayPause) })
                        .padding(8.dp)
                        .size(56.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(linearGradientBrush, blendMode = BlendMode.SrcAtop)
                            }
                        },
                    painter = painterResource(id = playResourceProvider()),
                    contentDescription = "play/pause Button"
                )
            }
        }

        Spacer(modifier = Modifier.padding(horizontal = 8.dp))

        Icon(
            modifier = Modifier
                .clickable(onClick = { onUiEvent(UIEvent.Forward) })
                .size(42.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(linearGradientBrush, blendMode = BlendMode.SrcAtop)
                    }
                },
            painter = painterResource(id = R.drawable.baseline_fast_forward_24),
            contentDescription = "forward button"
            )
    }
}