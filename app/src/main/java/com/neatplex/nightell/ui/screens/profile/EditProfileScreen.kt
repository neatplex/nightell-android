package com.neatplex.nightell.ui.screens.profile

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.neatplex.nightell.ui.component.widget.ErrorBanner
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.MaterialTheme
import com.neatplex.nightell.ui.component.common.AppTopBar
import com.neatplex.nightell.ui.component.common.SectionHeader
import com.neatplex.nightell.ui.component.widget.LoadingOverlay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.AlertDialogCustom
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.widget.EditProfileTextFieldWithValidation
import com.neatplex.nightell.ui.screens.auth.getUserNameErrorMessage
import com.neatplex.nightell.ui.screens.post.sanitizeDescription
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
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
    val user = currentUser
    if (user == null) {
        // Not loaded yet, or just signed out (the app is restarting into sign-in).
        CustomCircularProgressIndicator()
        return
    }
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
    var isSigningOut by remember { mutableStateOf(false) }

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
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.my_profile), onBack = { navController.popBackStack() }) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { space ->
        Column(
            modifier = Modifier
                .padding(space)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfileImageWithUploadIcon(
                user = user,
                isUploading = ProfileField.IMAGE in uiState.saving,
                onClick = { chooseImage("image/*") },
            )
            Spacer(Modifier.height(24.dp))
            if (resultErrorMessage.isNotEmpty()) {
                ErrorBanner(resultErrorMessage, modifier = Modifier.padding(bottom = 16.dp))
            }

            EditProfileTextFieldWithValidation(
                label = "Username",
                value = editedUsername,
                onValueChange = {
                    editedUsername = it
                    isUsernameChanged = true
                },
                isChanged = isUsernameChanged,
                errorText = getUserNameErrorMessage(editedUsername),
                isValid = editedUsername.isNotEmpty() && profileViewModel.isValidUsername(editedUsername),
                onSaveClicked = {
                    profileViewModel.updateUsername(editedUsername)
                    isUsernameChanged = false
                },
                length = 75,
                singleLine = true,
                imeAction = ImeAction.Next,
                height = 56,
                isLoading = ProfileField.USERNAME in uiState.saving,
                isSuccess = false
            )
            Spacer(Modifier.height(8.dp))
            EditProfileTextFieldWithValidation(
                label = "Name",
                value = editedName,
                onValueChange = {
                    editedName = it
                    isNameChanged = true
                },
                isChanged = isNameChanged,
                onSaveClicked = {
                    profileViewModel.updateName(editedName)
                    isNameChanged = false
                },
                errorText = "",
                length = 75,
                singleLine = true,
                isValid = editedName.length <= 75,
                imeAction = ImeAction.Next,
                height = 56,
                isLoading = ProfileField.NAME in uiState.saving,
                isSuccess = false
            )
            Spacer(Modifier.height(8.dp))
            EditProfileTextFieldWithValidation(
                label = "Bio",
                value = editedBio,
                onValueChange = {
                    editedBio = it
                    isBioChanged = true
                },
                isChanged = isBioChanged,
                onSaveClicked = {
                    profileViewModel.updateBio(sanitizeDescription(editedBio))
                    isBioChanged = false
                },
                errorText = "",
                length = 156,
                singleLine = false,
                isValid = editedBio.length <= 156,
                imeAction = ImeAction.Default,
                height = 140,
                isLoading = ProfileField.BIO in uiState.saving,
                isSuccess = false
            )

            SectionHeader("Account", Modifier.align(Alignment.Start).padding(start = 0.dp))
            AccountAction(
                icon = Icons.AutoMirrored.Rounded.ExitToApp,
                title = "Sign out",
                onClick = { showSignOutDialog = true },
            )
            AccountAction(
                icon = Icons.Rounded.Delete,
                title = "Delete account",
                subtitle = "Permanently remove your account and stories",
                isDestructive = true,
                onClick = { showDeleteAccountDialog = true },
            )
            Spacer(Modifier.height(24.dp))
        }
    }

    if (isSigningOut || uiState.isDeletingAccount) {
        LoadingOverlay()
    }

    if (showSignOutDialog) {
        AlertDialogCustom(
            onDismissRequest = { showSignOutDialog = false },
            dialogTitle = "Sign out?",
            dialogText = "You can sign back in any time.",
            confirmText = "Sign out",
            onConfirmation = {
                // MainActivity restarts into sign-in once the session is cleared.
                isSigningOut = true
                showSignOutDialog = false
                sessionViewModel.signOut()
            },
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialogCustom(
            onDismissRequest = { showDeleteAccountDialog = false },
            dialogTitle = "Delete your account?",
            dialogText = "Your profile and stories will be removed for good. This can't be undone.",
            confirmText = "Delete",
            onConfirmation = {
                showDeleteAccountDialog = false
                profileViewModel.deleteAccount()
            },
        )
    }
}
