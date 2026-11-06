package com.neatplex.nightell.ui.component.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.component.common.StoryArtwork

/** A story tile in a profile grid: square artwork with the title and likes below. */
@Composable
fun ProfilePostCard(post: Post, isLoading: Boolean, onPostClicked: (Post) -> Unit) {
    Column(
        modifier = Modifier
            .padding(6.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(enabled = !isLoading) { onPostClicked(post) }
            .padding(4.dp),
    ) {
        StoryArtwork(
            imageUrl = post.image?.url,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        )
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp, start = 2.dp, end = 2.dp),
        )
        LikesLabel(post.likesCount, modifier = Modifier.padding(start = 2.dp, top = 2.dp))
    }
}
