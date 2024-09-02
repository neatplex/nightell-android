package com.neatplex.nightell.ui.profile

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.MainActivity
import com.neatplex.nightell.R
import com.neatplex.nightell.component.AlertDialogCustom
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.EditProfileTextFieldWithValidation
import com.neatplex.nightell.ui.auth.getUserNameErrorMessage
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val user = sharedViewModel.user
    val updateProfileNameResult by profileViewModel.profileNameUpdatedData.observeAsState()
    val updateUsernameResult by profileViewModel.usernameUpdatedData.observeAsState()
    val updateProfileBioResult by profileViewModel.userBioUpdatedData.observeAsState()
    val deleteProfileResult by profileViewModel.accountDeleteResult.observeAsState()

    var isNameLoading by remember { mutableStateOf(false) }
    var isNameSuccess by remember { mutableStateOf(false) }

    var isUsernameLoading by remember { mutableStateOf(false) }
    var isUsernameSuccess by remember { mutableStateOf(false) }

    var isBioLoading by remember { mutableStateOf(false) }
    var isBioSuccess by remember { mutableStateOf(false) }

    // State for edited fields
    var editedName by remember { mutableStateOf(user.value!!.name) }
    var editedBio by remember { mutableStateOf(user.value!!.bio) }
    var editedUsername by remember { mutableStateOf(user.value!!.username) }

    // State for sign-out confirmation dialog
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var resultErrorMessage by remember { mutableStateOf("") }

    // Track changes in fields
    var isNameChanged by remember { mutableStateOf(false) }
    var isBioChanged by remember { mutableStateOf(false) }
    var isUsernameChanged by remember { mutableStateOf(false) }

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

                        Text(resultErrorMessage)

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
                            isValid = editedUsername.isNotEmpty() && profileViewModel.isValidUsername(
                                editedUsername
                            ),
                            onSaveClicked = {
                                if (isUsernameChanged && editedUsername.length >= 5) {
                                    isUsernameLoading = true
                                    profileViewModel.updateUsernameOfUser(editedUsername)
                                    isUsernameChanged = false
                                } else {
                                    errorMessage = "Username shouldn't be less than 5 character."
                                }
                            },
                            length = 75,
                            singleLine = true,
                            imeAction = ImeAction.Next,
                            height = 65,
                            isLoading = isUsernameLoading,
                            isSuccess = isUsernameSuccess
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
                                    isNameLoading = true
                                    profileViewModel.updateProfileName(editedName)
                                    isNameChanged = false
                                }
                            },
                            errorText = "",
                            length = 75,
                            singleLine = true,
                            isValid = editedName.length <= 75,
                            imeAction = ImeAction.Next,
                            height = 65,
                            isLoading = isNameLoading,
                            isSuccess = isNameSuccess
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
                                    isBioLoading = true
                                    profileViewModel.updateBioOfUser(editedBio)
                                    isBioChanged = false
                                }
                            },
                            errorText = "",
                            length = 156,
                            singleLine = false,
                            isValid = editedBio.length <= 156,
                            imeAction = ImeAction.Default,
                            height = 200,
                            isLoading = isBioLoading,
                            isSuccess = isBioSuccess
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.DarkGray
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

                        if (showSignOutDialog) {
                            AlertDialogCustom(
                                onDismissRequest = {
                                    showSignOutDialog = false
                                },
                                dialogTitle = "Sign Out!",
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
                                dialogTitle = "Delete Account!",
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
    updateProfileNameResult?.let { result ->
        isNameLoading = false
        when (result) {
            is Result.Success -> {
                isNameSuccess = true
                result.data?.let { updatedUser ->
                    // Update user in shared view model
                    sharedViewModel.setUser(updatedUser.user)
                    resultErrorMessage = ""
                }
            }

            is Result.Failure -> {
                // Reset to original values on error
                resultErrorMessage = result.message
                editedName = user.value!!.name
            }
        }
        // isNameSuccess = false
    }

    // Observe update result
    updateUsernameResult?.let { result ->
        isUsernameLoading = false
        when (result) {
            is Result.Success -> {
                isUsernameSuccess = true
                result.data?.let { updatedUser ->
                    // Update user in shared view model
                    sharedViewModel.setUser(updatedUser.user)
                    resultErrorMessage = ""
                }
            }

            is Result.Failure -> {
                // Reset to original values on error
                resultErrorMessage = result.message
                editedUsername = user.value!!.username
            }
        }
        // isUsernameSuccess = false
    }

    // Observe update result
    updateProfileBioResult?.let { result ->
        isBioLoading = false
        when (result) {
            is Result.Success -> {
                isBioSuccess = true
                result.data?.let { updatedUser ->
                    // Update user in shared view model
                    sharedViewModel.setUser(updatedUser.user)
                    resultErrorMessage = ""
                }
            }

            is Result.Failure -> {
                // Reset to original values on error
                resultErrorMessage = result.message
                editedBio = user.value!!.bio
            }
        }
        // isBioSuccess = false
    }

    // Observe delete result
    deleteProfileResult?.let { result ->
        when (result) {
            is Result.Success -> {
                sharedViewModel.deleteToken()
                isTokenDeletionInProgress = true
                showSignOutDialog = false
            }

            is Result.Failure -> {
                // Reset to original values on error
                resultErrorMessage = result.message
            }
        }
    }

    // Observe token deletion state
    val token by sharedViewModel.tokenState.collectAsState()
    LaunchedEffect(isTokenDeletionInProgress, token) {
        if (isTokenDeletionInProgress && token.isNullOrEmpty()) {
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
