package com.neatplex.nightell.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.AlertDialogCustom
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.widget.EditProfileTextFieldWithValidation
import com.neatplex.nightell.ui.screens.auth.getUserNameErrorMessage
import com.neatplex.nightell.ui.screens.post.sanitizeDescription
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import kotlinx.coroutines.delay
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.common.ObserveAsEvents
import com.neatplex.nightell.ui.util.rememberFilePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel,
    profileViewModel: EditProfileViewModel = hiltViewModel(),
) {
    val currentUser by sessionViewModel.currentUser.collectAsStateWithLifecycle()
    val user = currentUser ?: return
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val resultErrorMessage = uiState.error.orEmpty()

    // State for edited fields
    var editedName by remember { mutableStateOf(user.name) }
    var editedBio by remember { mutableStateOf(user.bio) }
    var editedUsername by remember { mutableStateOf(user.username) }

    // State for sign-out confirmation dialog
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    // Track changes in fields
    var isNameChanged by remember { mutableStateOf(false) }
    var isBioChanged by remember { mutableStateOf(false) }
    var isUsernameChanged by remember { mutableStateOf(false) }

    // State to track the token deletion process
    var isTokenDeletionInProgress by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val imageResource = getUserImagePainter(user)

    val chooseImage = rememberFilePicker { picked ->
        profileViewModel.onImageChosen(picked.name, picked.sizeBytes, picked::toLocalFile)
    }

    ObserveAsEvents(profileViewModel.events) { event ->
        when (event) {
            is EditProfileEvent.Saved -> sessionViewModel.setCurrentUser(event.user)
            // Put back the value that is still saved on the server.
            is EditProfileEvent.SaveFailed -> when (event.field) {
                ProfileField.NAME -> editedName = user.name
                ProfileField.BIO -> editedBio = user.bio
                ProfileField.USERNAME -> editedUsername = user.username
                ProfileField.IMAGE -> Unit
            }
            // The use case has already cleared the session.
            EditProfileEvent.AccountDeleted -> {
                sessionViewModel.signOut()
                isTokenDeletionInProgress = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.my_profile)) },
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

                        ProfileImageWithUploadIcon(
                            imageResource = imageResource, // Dummy resource
                            chooseImageLauncher = {
                                chooseImage("image/*")
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(resultErrorMessage, color = colorResource(id = R.color.purple_light), fontSize = 17.sp)

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
                                    profileViewModel.updateUsername(editedUsername)
                                    isUsernameChanged = false
                                } else {
                                    errorMessage = "Username shouldn't be less than 5 characters."
                                }
                            },
                            length = 75,
                            singleLine = true,
                            imeAction = ImeAction.Next,
                            height = 65,
                            isLoading = ProfileField.USERNAME in uiState.saving,
                            isSuccess = false
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
                                    profileViewModel.updateName(editedName)
                                    isNameChanged = false
                                }
                            },
                            errorText = "",
                            length = 75,
                            singleLine = true,
                            isValid = editedName.length <= 75,
                            imeAction = ImeAction.Next,
                            height = 65,
                            isLoading = ProfileField.NAME in uiState.saving,
                            isSuccess = false
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
                                    val sanitizedBio = sanitizeDescription(editedBio)
                                    profileViewModel.updateBio(sanitizedBio)
                                    isBioChanged = false
                                }
                            },
                            errorText = "",
                            length = 156,
                            singleLine = false,
                            isValid = editedBio.length <= 156,
                            imeAction = ImeAction.Default,
                            height = 200,
                            isLoading = ProfileField.BIO in uiState.saving,
                            isSuccess = false
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
                                    sessionViewModel.signOut()
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
                                dialogText = "Are you sure you want to delete your account?",
                                onConfirmation = {
                                    profileViewModel.deleteAccount()
                                })
                        }
                    }
                }
            }
        }
    )

    // Restart the app once the token is gone.
    val token by sessionViewModel.token.collectAsStateWithLifecycle()
    LaunchedEffect(isTokenDeletionInProgress, token) {
        if (isTokenDeletionInProgress && token.isNullOrEmpty()) {
            delay(2000)
            signOut(context)
        }
    }
}
