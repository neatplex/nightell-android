package com.neatplex.nightell.ui.component.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.SavedPost
import com.neatplex.nightell.ui.component.common.StoryArtwork

/** A bookmarked story, with a button to remove the bookmark. */
@Composable
fun SavedPostCard(post: SavedPost, onPostClicked: (SavedPost) -> Unit, onDeleteClicked: (SavedPost) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPostClicked(post) }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StoryArtwork(imageUrl = post.imageUrl, shape = MaterialTheme.shapes.medium, modifier = Modifier.size(72.dp))
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(post.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "@${post.ownerName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = { onDeleteClicked(post) }) {
            Icon(
                painterResource(R.drawable.bookmark),
                contentDescription = "Remove bookmark",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
