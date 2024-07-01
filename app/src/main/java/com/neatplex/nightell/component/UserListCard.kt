package com.neatplex.nightell.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.toJson

@Composable
fun ShowUsers(users: List<User?>?, navController: NavController, viewModel: SharedViewModel) {
    users?.forEach { user ->
        if (user != null) {
            UserCard(user = user) {
                if (viewModel.user.value?.id == user.id) {

                } else {
                    val userJson = user.toJson()
                    navController.navigate("userScreen/${Uri.encode(userJson)}")
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, onUserClicked: (User) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
            .clickable {
                onUserClicked(user)
            },
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageResource =
                rememberAsyncImagePainter(model = R.drawable.default_profile_image)

            Image(
                painter = imageResource,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = user.username
                )
                Text(
                    text = user.name
                )
            }
        }
    }
}