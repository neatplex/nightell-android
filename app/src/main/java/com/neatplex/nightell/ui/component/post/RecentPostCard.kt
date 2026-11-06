package com.neatplex.nightell.ui.component.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.component.common.StoryArtwork
import com.neatplex.nightell.ui.component.common.UserAvatar

/**
 * A featured story: large artwork with the title and author over a dark fade,
 * and a play button. Fills the width it is given.
 */
@Composable
fun RecentPostCard(post: Post, isLoading: Boolean, onPostClicked: (Post) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
            .clickable(enabled = !isLoading) { onPostClicked(post) },
    ) {
        StoryArtwork(imageUrl = post.image?.url, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(0.35f to Color.Transparent, 1f to Color(0xE6080C1E))),
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                    UserAvatar(post.user, size = 22.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "@${post.user.username}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            FilledIconButton(onClick = { onPostClicked(post) }, enabled = !isLoading) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Open ${post.title}")
            }
        }
    }
}
