package com.neatplex.nightell.ui.component.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.utils.Constant

@Composable
fun ProfilePostCard(post: Post,isLoading: Boolean, onPostClicked: (Post) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(enabled = !isLoading) { onPostClicked(post) },
        elevation = 0.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(8.dp))
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
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Row {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.body1,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}