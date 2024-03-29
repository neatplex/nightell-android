package com.neatplex.nightell.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.AudioPlayer
import com.neatplex.nightell.utils.Constant
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.LikeViewModel
import com.neatplex.nightell.ui.viewmodel.PostViewModel
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.ui.viewmodel.ProfileViewModel


@Composable
fun PostScreen(navController: NavController, sharedViewModel: SharedViewModel) {

    // Initialize ViewModels
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val postViewModel: PostViewModel = hiltViewModel()
    val likeViewModel: LikeViewModel = hiltViewModel()

    // Retrieve necessary data
    val post = sharedViewModel.post.value ?: return
    val userId = sharedViewModel.user.value!!.id
    val menuExpanded = remember { mutableStateOf(false) }

    // States for like and unlike handling
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableIntStateOf(0) }
    var likeId by remember { mutableStateOf<Int?>(null) }
    var icon by remember { mutableStateOf(Icons.Filled.FavoriteBorder) }

    // Fetch likes and user info
    LaunchedEffect(Unit) {
        likeViewModel.showLikes(post.id)
        profileViewModel.getUserInfo(post.user_id)
    }

    // Observe likes and user info results
    val likesCountResult by likeViewModel.showLikesResult.observeAsState()
    val unlikeResult by likeViewModel.unlikeResult.observeAsState()
    val likeResult by likeViewModel.likeResult.observeAsState()
    val userInfoResult by profileViewModel.showUserInfoResult.observeAsState()
    val postUpdateResult by postViewModel.storePostResult.observeAsState()

    // Update UI based on likes result
    likesCountResult?.let { result ->
        when (result) {
            is Result.Success -> {
                val likes = result.data?.likes.orEmpty()
                likesCount = likes.size
                isLiked = likes.any { it.user_id == userId }
                icon = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                likeId = if (isLiked) likes.find { it.user_id == userId }?.id else null
            }
            is Result.Error -> {
                // Handle error
            }
            else -> {
            }
        }
    }

    likeResult?.let {
        if (it is Result.Success) {
            likeViewModel.showLikes(post.id)
        }
    }

    unlikeResult?.let {
        if (it is Result.Success) {
            likeViewModel.showLikes(post.id)
        }
    }

    var editedTitle by remember { mutableStateOf(post.title ?: "") }
    var editedDescription by remember { mutableStateOf(post.description ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(Modifier.weight(1f))

            // Conditional toggle menu for edit/delete
            if (userId == post.user_id) {
                // Show edit and delete menu here
                Box(modifier = Modifier.padding(8.dp)) {
                    IconButton(
                        onClick = { menuExpanded.value = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.Black
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        DropdownMenuItem(onClick = {
                            isEditing = true
                            menuExpanded.value = false
                        }) {
                            Text(text = stringResource(id = R.string.edit))
                        }
                        DropdownMenuItem(onClick = {
                            postViewModel.deletePost(post.id)
                            menuExpanded.value = false
                            navController.popBackStack()
                        }) {
                            Text(text = stringResource(id = R.string.delet))
                        }
                    }
                }
            }
        }

        val imageResource = rememberImagePainter(
            data = post.image?.path?.let { Constant.Files_URL + it }
                ?: R.drawable.ic_launcher_background
        )

        Image(
            painter = imageResource,
            contentDescription = "Story Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(all = 5.dp),
            verticalAlignment = Alignment.CenterVertically) {

            userInfoResult?.let { userResult ->
                when (userResult) {
                    is Result.Success -> {
                        Spacer(modifier = Modifier.width(16.dp))
                        userResult.data?.user?.let {
                            val imageResource = rememberImagePainter(data = R.drawable.default_profile_image,)

                            Image(
                                painter = imageResource,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = it.username,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable {
                                        // Navigate to another page when "Followers" is clicked
                                        if (sharedViewModel.user.value?.id == it.id) {
                                            navController.navigate("profile")
                                        } else {
                                            navController.navigate("userScreen/${it.id}")
                                        }
                                    }
                            )
                        }
                    }
                    else -> {
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Text(text = likesCount.toString())
            IconButton(onClick = {
                if (isLiked) {
                    likeId?.let { id ->
                        likeViewModel.deleteLike(id)
                    }
                } else if (!isLiked) {
                    likeViewModel.like(post.id)
                }
            }) {
                Icon(icon, contentDescription = "Like",
                    modifier = Modifier.size(35.dp))
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)) {
            AudioPlayer(navController = navController, Constant.Files_URL + post.audio.path)
        }

        if (isEditing) {
            TextField(
                value = editedTitle,
                onValueChange = { editedTitle = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            TextField(
                value = editedDescription,
                onValueChange = { editedDescription = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Button(onClick = {
                postViewModel.editPost(post.id, editedTitle, editedDescription)
                postUpdateResult?.let {
                    if (it is Result.Success) {
                        isEditing = false
                    }
                }
            }) {
                Text(text = "save")
            }
        } else {
            Text(
                text = editedTitle,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = editedDescription,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
