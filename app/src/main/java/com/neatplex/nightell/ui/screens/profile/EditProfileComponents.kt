package com.neatplex.nightell.ui.screens.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.neatplex.nightell.ui.component.common.UserAvatar
import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.clip
import com.neatplex.nightell.domain.model.User
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R

/** The profile picture with an edit badge; tapping it picks a new image. */
@Composable
fun ProfileImageWithUploadIcon(
    user: User,
    isUploading: Boolean,
    onClick: () -> Unit,
) {
    Box(modifier = Modifier.size(112.dp)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .clickable(enabled = !isUploading, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            UserAvatar(user, size = 112.dp)
            if (isUploading) {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator(modifier = Modifier.size(32.dp)) }
            }
        }
        Surface(
            onClick = onClick,
            enabled = !isUploading,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            border = androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.background),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(38.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.Edit,
                    contentDescription = "Change profile picture",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

/** A row in the account section, e.g. "Sign out". */
@Composable
fun AccountAction(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    subtitle: String? = null,
    isDestructive: Boolean = false,
) {
    val color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    ListItem(
        headlineContent = { Text(title, color = color) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = { Icon(icon, contentDescription = null, tint = color) },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
    )
}

@Composable
fun getUserImagePainter(user: User?): AsyncImagePainter {
    return if (user?.image == null) {
        rememberAsyncImagePainter(model = R.drawable.default_profile_image)
    } else {
        rememberAsyncImagePainter(model = user.image?.url)
    }
}