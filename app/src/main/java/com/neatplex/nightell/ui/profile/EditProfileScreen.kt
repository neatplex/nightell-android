package com.neatplex.nightell.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomGrayButton
import com.neatplex.nightell.component.ErrorText
import com.neatplex.nightell.ui.auth.getUserNameErrorMessage
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.Validation
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
    val deleteProfileResult by profileViewModel.accountDeleteResult.observeAsState()

    // State for edited fields
    var editedName by remember { mutableStateOf(user.value!!.name ?: "") }
    var editedBio by remember { mutableStateOf(user.value!!.bio ?: "") }
    var editedUsername by remember { mutableStateOf(user.value!!.username ?: "") }

    // State for sign-out confirmation dialog
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    // Track changes in fields
    var isNameChanged by remember { mutableStateOf(false) }
    var isBioChanged by remember { mutableStateOf(false) }
    var isUsernameChanged by remember { mutableStateOf(false) }

    // Track original values
    var originalName = user.value?.name ?: ""
    var originalBio = user.value?.bio ?: ""
    var originalUsername = user.value?.username ?: ""

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
                    val imageResource =
                        rememberImagePainter(data = R.drawable.default_profile_image)

                    Image(
                        painter = imageResource,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    EditProfileTextFieldWithValidation(
                        label = "Username",
                        value = editedUsername,
                        onValueChange = {
                            editedUsername = it
                            isUsernameChanged = true
                        },
                        isChanged = isUsernameChanged,
                        errorText = getUserNameErrorMessage(editedUsername),
                        isValid = editedUsername.isEmpty() || Validation.isValidUsername(
                            editedUsername
                        ),
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
                        },
                        length = 75,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text fields to edit name, bio, and username
                    EditProfileTextFieldWithValidation(
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
                        },
                        errorText = "",
                        length = 75,
                        singleLine = true,
                        isValid = editedName.length <= 75
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    EditProfileTextFieldWithValidation(
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
                        },
                        errorText = "",
                        length = 156,
                        singleLine = false,
                        isValid = editedBio.length <= 156
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    CustomGrayButton(
                        onClick = {
                            showSignOutDialog = true
                        },
                        text = "Sign Out"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Gray
                                )
                            ) {
                                append("Do you want to delete your account? ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = colorResource(id = R.color.blue_light),
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("Delete Account")
                            }
                        },
                        modifier = Modifier.clickable {
                            showDeleteAccountDialog = true
                        }
                    )

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
                    if (showDeleteAccountDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDeleteAccountDialog = false
                            },
                            title = {
                                Text(text = "Delete My Account!")
                            },
                            text = {
                                Text("Are you sure you want to delete your account?")
                            },
                            confirmButton = {
                                Button(onClick = {
                                    profileViewModel.deleteAccount()
                                }) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                Button(onClick = {
                                    showDeleteAccountDialog = false
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
                    originalName = user.value?.name ?: ""
                    originalBio = user.value?.bio ?: ""
                    originalUsername = user.value?.username ?: ""
                    errorMessage = ""
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

    // Observe delete result
    deleteProfileResult?.let { result ->
        when (result) {
            is Result.Success -> {
                sharedViewModel.deleteToken()
                isTokenDeletionInProgress = true
                showSignOutDialog = false
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
        parentNavController.navigate("splash") {
            popUpTo(0) { inclusive = true }
        }
    }

    // Observe token deletion state
    val token by sharedViewModel.tokenState.collectAsState()
    if (isTokenDeletionInProgress && token == null) {
        isTokenDeletionInProgress = false
        shouldNavigateToSplash = true
    }
}

@Composable
fun EditProfileTextFieldWithValidation(
    value: String,
    onValueChange: (String) -> Unit,
    isChanged: Boolean,
    onSaveClicked: () -> Unit,
    onCancelClicked: () -> Unit,
    label: String,
    errorText: String,
    length: Int,
    isValid: Boolean,
    singleLine: Boolean,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val purpleErrorColor = colorResource(id = R.color.purple_light)
    val saveColor = Color.Green
    val cancelColor = Color.Red

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= length) {
                onValueChange(it)
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        visualTransformation = visualTransformation,
        isError = !isValid && value.isNotEmpty(), // Display error when the field is not empty and not valid
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = Color.Black,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray,// Change text color if needed
            focusedBorderColor = Color.Black, // Change border color when focused
            unfocusedBorderColor = Color.Gray,
            errorBorderColor = purpleErrorColor.copy(alpha = 0.5f), // Change border color when not focused
            errorLabelColor = purpleErrorColor, // Change border color when not focused
            errorTrailingIconColor = purpleErrorColor, // Change border color when not focused
            errorCursorColor = purpleErrorColor, // Change border color when not focused
            backgroundColor = Color.White.copy(alpha = 0.5f) // Set background color with 50% opacity
        ),
        singleLine = singleLine
    )
    if (value.isNotEmpty() && !isValid) { // Only show error text when the field is not empty and not valid
        Text(
            text = errorText,
            color = purpleErrorColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    } else {
        if (isChanged) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onSaveClicked,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clip(CircleShape)
                        .background(saveColor.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Save",
                        tint = saveColor
                    )
                }
                IconButton(
                    onClick = onCancelClicked,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(cancelColor.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Cancel",
                        tint = cancelColor
                    )
                }
            }
        }
    }
}