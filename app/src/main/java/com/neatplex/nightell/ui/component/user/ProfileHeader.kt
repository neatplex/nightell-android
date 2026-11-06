package com.neatplex.nightell.ui.component.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.ui.component.common.UserAvatar

/**
 * The top of a profile: a soft gradient banner, a large avatar, name, handle,
 * bio, follower counts and one action (e.g. "Edit profile" or "Follow").
 */
@Composable
fun ProfileHeader(
    user: User,
    followers: Int,
    followings: Int,
    storyCount: Int?,
    onFollowersClick: () -> Unit,
    onFollowingsClick: () -> Unit,
    action: @Composable () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.linearGradient(listOf(colors.primaryContainer, colors.tertiaryContainer)),
                        MaterialTheme.shapes.extraLarge,
                    ),
            )
        }
        Surface(
            shape = CircleShape,
            color = colors.background,
            modifier = Modifier.overlapAbove(),
        ) {
            UserAvatar(user, size = 96.dp, modifier = Modifier.padding(4.dp))
        }
        Text(
            user.name.ifBlank { user.username },
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text("@${user.username}", style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        if (user.bio.isNotBlank()) {
            Text(
                user.bio,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
            )
        } else {
            Spacer(Modifier.height(12.dp))
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            storyCount?.let { Stat(it, "Stories", onClick = null, modifier = Modifier.weight(1f)) }
            Stat(followers, "Followers", onFollowersClick, Modifier.weight(1f))
            Stat(followings, "Following", onFollowingsClick, Modifier.weight(1f))
        }
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) { action() }
    }
}

@Composable
private fun Stat(value: Int, label: String, onClick: (() -> Unit)?, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 8.dp)) {
            Text(value.toString(), style = MaterialTheme.typography.titleLarge)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Pulls the element up by half its height, so the avatar overlaps the banner. */
private fun Modifier.overlapAbove(): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val overlap = placeable.height / 2
    layout(placeable.width, placeable.height - overlap) { placeable.place(0, -overlap) }
}
