package com.neatplex.nightell.ui.component.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.ui.screens.profile.getUserImagePainter
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import com.neatplex.nightell.utils.getTimeAgo

@Composable
fun CommentCard(
    comment: Comment,
    userId: Int,
    onDeleteClick: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        if (userId == comment.user.id) {
                            showDialog = true
                        }
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            val imageResource = getUserImagePainter(comment.user)

            Image(
                painter = imageResource,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = comment.user.username
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = getTimeAgo(comment.created_at),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = comment.text
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = { Text(text = "Do you want to delete this comment?") },
            confirmButton = {
                Button(onClick = {
                    onDeleteClick(comment.id)
                    showDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White
        )
    }
}