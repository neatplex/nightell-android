package com.neatplex.nightell.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.ErrorText
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@NonRestartableComposable
fun EditProfileScreen(
    parentNavController: NavController,
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val user = sharedViewModel.user
    val updateProfileResult by profileViewModel.userUpdatedData.observeAsState()

    // State for edited fields
    var editedName by remember { mutableStateOf(user.value!!.name ?: "") }
    var editedBio by remember { mutableStateOf(user.value!!.bio ?: "") }
    var editedUsername by remember { mutableStateOf(user.value!!.username ?: "") }

    // State for sign-out confirmation dialog
    var showSignOutDialog by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    // Track changes in fields
    var isNameChanged by remember { mutableStateOf(false) }
    var isBioChanged by remember { mutableStateOf(false) }
    var isUsernameChanged by remember { mutableStateOf(false) }

    // Track original values
    val originalName = user.value?.name ?: ""
    val originalBio = user.value?.bio ?: ""
    val originalUsername = user.value?.username ?: ""

    // State to track the token deletion process
    var isTokenDeletionInProgress by remember { mutableStateOf(false) }
    var shouldNavigateToSplash by remember { mutableStateOf(false) }

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
            if (isTokenDeletionInProgress) {
                // Show a progress bar while the token deletion is in progress
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val imageResource = rememberImagePainter(data = R.drawable.default_profile_image)

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
                                profileViewModel.updateProfileName(editedName)
                                isNameChanged = false
                            }
                        },
                        onCancelClicked = {
                            editedName = originalName
                            isNameChanged = false
                        }
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
                                profileViewModel.updateBioOfUser(editedBio)
                                isBioChanged = false
                            }
                        },
                        onCancelClicked = {
                            editedBio = originalBio
                            isBioChanged = false
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    EditableField(
                        label = "Username",
                        value = editedUsername,
                        onValueChange = {
                            profileViewModel.updateUsernameOfUser(editedUsername)
                            editedUsername = it
                            isUsernameChanged = true
                        },
                        isChanged = isUsernameChanged,
                        onSaveClicked = {
                            if (isUsernameChanged && editedUsername.length >= 5) {
                                profileViewModel.updateUsernameOfUser(editedUsername)
                                isUsernameChanged = false
                            } else {
                                errorMessage = "Username shouldn't be less than 5 character."
                            }
                        },
                        onCancelClicked = {
                            editedUsername = originalUsername
                            isUsernameChanged = false
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        showSignOutDialog = true
                    }) {
                        Text(text = "Sign Out")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ErrorText(text = errorMessage)

                    if (showSignOutDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showSignOutDialog = false
                            },
                            title = {
                                Text(text = "Sign Out")
                            },
                            text = {
                                Text("Are you sure you want to sign out?")
                            },
                            confirmButton = {
                                Button(onClick = {
                                    sharedViewModel.deleteToken()
                                    isTokenDeletionInProgress = true
                                    showSignOutDialog = false
                                }) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                Button(onClick = {
                                    showSignOutDialog = false
                                }) {
                                    Text("No")
                                }
                            }
                        )
                    }
                }
            }
        }
    )

    // Observe update result
    updateProfileResult?.let { result ->
        when (result) {
            is Result.Success -> {
                result.data?.let { updatedUser ->
                    // Update user in shared view model
                    sharedViewModel.setUser(updatedUser.user)
                }
            }

            is Result.Error -> {
                // Reset to original values on error
                errorMessage = result.message
            }

            is Result.Loading -> {
                // Show loading indicator
            }
        }
    }

    // Navigate to splash screen after token deletion
    if (shouldNavigateToSplash) {
        LaunchedEffect(Unit) {
            parentNavController.navigate("splash") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Observe token deletion state
    val token by sharedViewModel.tokenState.collectAsState()
    if (isTokenDeletionInProgress && token == null) {
        isTokenDeletionInProgress = true
        shouldNavigateToSplash = true
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
