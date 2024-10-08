package com.neatplex.nightell.ui.component.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.screens.profile.getUserImagePainter
import com.neatplex.nightell.utils.Constant

@Composable
fun RecentPostCard(post: Post, isLoading: Boolean, onPostClicked: (Post) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading) {
                onPostClicked(post)
            },
        elevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val imageResource = getUserImagePainter(post.user)

                Image(
                    painter = imageResource,
                    contentDescription = "Author Image",
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Check if post.title is not null or empty
                val title = post.user.username
                val desc = post.title

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row {
                val imageResource = if (post.image != null) {
                    rememberAsyncImagePainter(model = Constant.Files_URL + post.image.path)
                } else {
                    rememberAsyncImagePainter(model = R.drawable.slider)
                }
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = imageResource,
                        contentDescription = "Story Image",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}


