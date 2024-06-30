package com.neatplex.nightell.component.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.utils.Constant

@Composable
fun HomePostCard(post: Post, onPostClicked: (Post) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPostClicked(post) },
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {

                val imageResource = if (post.image != null) {
                    rememberAsyncImagePainter(model = Constant.Files_URL + post.image.path)
                } else {
                    rememberAsyncImagePainter(model = R.drawable.slider)
                }
                Image(
                    painter = imageResource,
                    contentDescription = "Story Image",
                    modifier = Modifier
                        .size(95.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }


            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(8.dp)
            ) {
                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = post.title,
                    style = MaterialTheme.typography.body1,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = post.description!!.take(20) + "...",
                    style = MaterialTheme.typography.body1,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(start = 3.dp).width(50.dp)) {
                            Text(
                                post.likes_count.toString(),
                                fontSize = 14.sp
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)) {
                        Column {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_message_24),
                                contentDescription = null,
                                tint = colorResource(id = R.color.blue),
                                modifier = Modifier
                                    .size(16.dp)
                                    .graphicsLayer {
                                        scaleX = -1f
                                    }
                            )
                        }
                        Column(modifier = Modifier.padding(start = 3.dp)) {
                            Text(
                                post.comments_count.toString(),
                                fontSize = 14.sp
                            )
                        }

                    }
                }
            }
        }
    }
}