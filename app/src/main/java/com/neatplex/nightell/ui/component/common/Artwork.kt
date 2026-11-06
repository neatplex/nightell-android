package com.neatplex.nightell.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.User

/**
 * A story's cover image. Shows a soft gradient while loading, and the default
 * artwork when the story has none.
 */
@Composable
fun StoryArtwork(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    contentDescription: String? = null,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .clip(shape)
            .background(Brush.linearGradient(listOf(colors.primaryContainer, colors.tertiaryContainer))),
    ) {
        AsyncImage(
            model = imageUrl ?: R.drawable.slider,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.slider),
            modifier = Modifier.matchParentSize(),
        )
    }
}

/** A user's round profile picture, or the default one. */
@Composable
fun UserAvatar(user: User?, size: Dp = 40.dp, modifier: Modifier = Modifier) {
    AsyncImage(
        model = user?.image?.url ?: R.drawable.default_profile_image,
        contentDescription = user?.let { "${it.username}'s profile picture" },
        contentScale = ContentScale.Crop,
        error = painterResource(R.drawable.default_profile_image),
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
    )
}
