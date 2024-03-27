package com.neatplex.nightell.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.UserProfileViewModel
import com.neatplex.nightell.ui.viewmodel.SharedViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditProfileScreen(navController: NavController, sharedViewModel: SharedViewModel) {

    val userProfileViewModel: UserProfileViewModel = hiltViewModel()
    val user = sharedViewModel.user
    val editProfileResult by userProfileViewModel.userUpdatedData.observeAsState()


    // State for edited fields
    var editedName by remember { mutableStateOf(user.value!!.name ?: "") }
    var editedBio by remember { mutableStateOf(user.value!!.bio ?: "") }
    var editedUsername by remember { mutableStateOf(user.value!!.username ?: "") }


    // Track changes in fields
    var isNameChanged by remember { mutableStateOf(false) }
    var isBioChanged by remember { mutableStateOf(false) }
    var isUsernameChanged by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val imageResource = rememberImagePainter(data = R.drawable.default_profile_image,)

                Image(
                    painter = imageResource,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Text fields to edit name, bio, and username
                EditableField(
                    label = "Name",
                    value = editedName,
                    onValueChange = {
                        editedName = it
                        isNameChanged = true
                    },
                    isChanged = isNameChanged,
                    onSaveClicked = {
                        if (isNameChanged) {
                            userProfileViewModel.changeProfileName(editedName)
                            isNameChanged = false
                        }
                    },
                    onCancelClicked = { editedName = user.value?.name ?: ""
                        isNameChanged = false}
                )
                Spacer(modifier = Modifier.height(16.dp))

                EditableField(
                    label = "Bio",
                    value = editedBio,
                    onValueChange = {
                        editedBio = it
                        isBioChanged = true
                    },
                    isChanged = isBioChanged,
                    onSaveClicked = {
                        if (isBioChanged) {
                            userProfileViewModel.updateBioOfUser(editedBio)
                            isBioChanged = false
                        }
                    },
                    onCancelClicked = { editedBio = user.value?.bio ?: ""
                        isBioChanged = false}
                )
                Spacer(modifier = Modifier.height(16.dp))

                EditableField(
                    label = "Username",
                    value = editedUsername,
                    onValueChange = {
                        editedUsername = it
                        isUsernameChanged = true
                    },
                    isChanged = isUsernameChanged,
                    onSaveClicked = {
                        if (isUsernameChanged) {
                            userProfileViewModel.updateUsernameOfUser(editedUsername)
                            isUsernameChanged = false
                        }
                    },
                    onCancelClicked = { editedUsername = user.value?.username ?: ""
                        isUsernameChanged = false}
                )
                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    )

    // Observe update result
    editProfileResult?.let { result ->
        when (result) {
            is Result.Success -> {
                result.data?.let { updatedUser ->
                    // Update user in shared view model
                    sharedViewModel.setUser(updatedUser.user)
                }
            }
            is Result.Error -> {
                // Handle error
            }
            is Result.Loading -> {
                // Show loading indicator
            }
        }
    }
}

@Composable
fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isChanged: Boolean,
    onSaveClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.weight(1f)
        )
        if (isChanged) {
            IconButton(onClick = onSaveClicked) {
                Icon(Icons.Filled.Check, contentDescription = "Save")
            }
            IconButton(onClick = onCancelClicked) {
                Icon(Icons.Filled.Close, contentDescription = "Cancel")
            }
        }
    }
}