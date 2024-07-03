package com.neatplex.nightell.ui.profile

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.MainActivity
import com.neatplex.nightell.R
import com.neatplex.nightell.component.AlertDialogCustom
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.CustomSimpleButton
import com.neatplex.nightell.component.ErrorText
import com.neatplex.nightell.ui.auth.getUserNameErrorMessage
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.Validation
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val user = sharedViewModel.user
    val updateProfileResult by profileViewModel.userUpdatedData.observeAsState()
    val deleteProfileResult by profileViewModel.accountDeleteResult.observeAsState()

    // State for edited fields
    var editedName by remember { mutableStateOf(user.value!!.name) }
    var editedBio by remember { mutableStateOf(user.value!!.bio) }
    var editedUsername by remember { mutableStateOf(user.value!!.username) }

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

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showSignOutDialog = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.logout),
                            contentDescription = "saved audio",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(26.dp)
                                .align(Alignment.CenterVertically) // Align icon vertically
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // Transparent to use the Box's background
                )
            )
        },
        content = { space ->
            if (isTokenDeletionInProgress) {
                CustomCircularProgressIndicator()
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(space)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val imageResource =
                            rememberAsyncImagePainter(model = R.drawable.default_profile_image)

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
                            singleLine = true,
                            imeAction = ImeAction.Next,
                            height = 65
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
                            isValid = editedName.length <= 75,
                            imeAction = ImeAction.Next,
                            height = 65
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
                            isValid = editedBio.length <= 156,
                            imeAction = ImeAction.Default,
                            height = 200
                        )

                        Spacer(modifier = Modifier.height(32.dp))

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
                            modifier = Modifier
                                .clickable {
                                    showDeleteAccountDialog = true
                                },
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ErrorText(text = errorMessage)

                        if (showSignOutDialog) {
                            AlertDialogCustom(
                                onDismissRequest = {
                                    showSignOutDialog = false
                                },
                                dialogTitle = "Sign Out",
                                dialogText = "Are you sure you want to sign out?",
                                onConfirmation = {
                                        sharedViewModel.deleteToken()
                                        isTokenDeletionInProgress = true
                                        showSignOutDialog = false
                                })
                        }

                        if (showDeleteAccountDialog) {
                            AlertDialogCustom(
                                onDismissRequest = {
                                    showDeleteAccountDialog = false
                                },
                                dialogTitle = "Delete My Account!",
                                dialogText ="Are you sure you want to delete your account?",
                                onConfirmation = {
                                        profileViewModel.deleteAccount()
                                })
                        }
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


    // Observe token deletion state
    val token by sharedViewModel.tokenState.collectAsState()
    LaunchedEffect(isTokenDeletionInProgress, token) {
        if (isTokenDeletionInProgress && token == "") {
            delay(2000)
            signOut(context)
        }
    }
}

fun signOut(context: Context) {

    // Restart the application
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)

    if (context is ComponentActivity) {
        context.finish()
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
    visualTransformation: VisualTransformation = VisualTransformation.None,
    imeAction : ImeAction,
    height : Int
) {
    val purpleErrorColor = colorResource(id = R.color.purple_light)

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= length) {
                onValueChange(it)
            }
        },
        modifier = Modifier.fillMaxWidth().height(height.dp),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        visualTransformation = visualTransformation,
        isError = !isValid && value.isNotEmpty(), // Display error when the field is not empty and not valid
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.Black,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray, // Change text color if needed
            focusedBorderColor = Color.Black, // Change border color when focused
            unfocusedBorderColor = Color.Gray,
            errorBorderColor = purpleErrorColor.copy(alpha = 0.5f), // Change border color when not focused
            errorLabelColor = purpleErrorColor, // Change border color when not focused
            errorTrailingIconColor = purpleErrorColor, // Change border color when not focused
            errorCursorColor = purpleErrorColor, // Change border color when not focused
        ),
        singleLine = singleLine,
        trailingIcon = {
            if (isChanged && isValid) {
                Row {
                    IconButton(
                        onClick = onSaveClicked,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Save",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    )
    if (value.isNotEmpty() && !isValid) { // Only show error text when the field is not empty and not valid
        Text(
            text = errorText,
            color = purpleErrorColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}