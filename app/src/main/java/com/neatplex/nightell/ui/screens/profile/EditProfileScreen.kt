package com.neatplex.nightell.ui.screens.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Create
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.MainActivity
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.AlertDialogCustom
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.widget.EditProfileTextFieldWithValidation
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.ui.screens.auth.getUserNameErrorMessage
import com.neatplex.nightell.ui.screens.post.sanitizeDescription
import com.neatplex.nightell.ui.screens.upload.UploadViewModel
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.Constant
import com.neatplex.nightell.utils.getFileNameAndUri
import com.neatplex.nightell.utils.getFileSize
import com.neatplex.nightell.utils.uriToFile
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val uploadViewModel: UploadViewModel = hiltViewModel()
    val user = sharedViewModel.user
    val updateProfileNameResult by profileViewModel.profileNameUpdatedData.observeAsState()
    val updateUsernameResult by profileViewModel.usernameUpdatedData.observeAsState()
    val updateProfileBioResult by profileViewModel.userBioUpdatedData.observeAsState()
    val updateProfileImageResult by profileViewModel.userImageUpdatedData.observeAsState()
    val deleteProfileResult by profileViewModel.accountDeleteResult.observeAsState()
    val isLoading by profileViewModel.isLoading.observeAsState(false)

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

    var resultErrorMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    // Track changes in fields
    var isNameChanged by remember { mutableStateOf(false) }
    var isBioChanged by remember { mutableStateOf(false) }
    var isUsernameChanged by remember { mutableStateOf(false) }

    // State to track the token deletion process
    var isTokenDeletionInProgress by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val uploadFileResults by uploadViewModel.uploadState.observeAsState()
    var imageId by rememberSaveable { mutableStateOf(user.value!!.image_id) }

    var imageResource = getUserImagePainter(user.value)

    val chooseImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { imageUri ->
                val (fileName, mainUri) = getFileNameAndUri(context, imageUri)
                selectedImage = mainUri
                val fileExtension = fileName?.substringAfterLast('.', "")
                val fileSize = getFileSize(context, selectedImage!!)
                val file = uriToFile(context, selectedImage!!)
                if (fileExtension.equals("jpg", ignoreCase = true) ||
                    fileExtension.equals("jpeg", ignoreCase = true)
                ) {
                    if (fileSize <= 2 * 1024 * 1024) {
                        uploadViewModel.uploadFile(file!!, "JPG")
                    } else {
                        resultErrorMessage = "Image file must be less than 2MB!"
                    }
                } else {
                    resultErrorMessage = "Only .jpg type is allowed for post image!"
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

                // Observe audio and image upload results
                when (val result = uploadFileResults) {
                    is Result.Success -> {
                        result.data?.let {
                            if (it.file.extension.equals("JPG", ignoreCase = true)) {
                                imageId = it.file.id
                            }
                        }
                        profileViewModel.updateProfileImage(imageId!!)
                    }

                    is Result.Failure -> {
                        resultErrorMessage = result.message
                    }

                    else -> {}
                }

                when (val result = updateProfileImageResult) {
                    is Result.Success -> {
                        result.data?.let {
                            sharedViewModel.setUser(it.user)
                            imageResource = getUserImagePainter(user = it.user)
                        }
                    }
                    is Result.Failure -> {
                        resultErrorMessage = result.message
                    }

                    else -> {}
                }

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
                                chooseImageLauncher.launch("image/*")
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
                                    isUsernameLoading = true
                                    profileViewModel.updateUsernameOfUser(editedUsername)
                                    isUsernameChanged = false
                                } else {
                                    errorMessage = "Username shouldn't be less than 5 characters."
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
                                    val sanitizedBio = sanitizeDescription(editedBio)
                                    profileViewModel.updateBioOfUser(sanitizedBio)
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

    // Observe update result for Name
    updateProfileNameResult?.let { result ->
        isNameLoading = false
        when (result) {
            is Result.Success -> {
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
    }

    // Observe update result for Username
    updateUsernameResult?.let { result ->
        isUsernameLoading = false
        when (result) {
            is Result.Success -> {
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
    }

    // Observe update result for Bio
    updateProfileBioResult?.let { result ->
        isBioLoading = false
        when (result) {
            is Result.Success -> {
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


@Composable
fun ProfileImageWithUploadIcon(
    imageResource: AsyncImagePainter,
    chooseImageLauncher: () -> Unit
) {
    Box(
        modifier = Modifier.size(120.dp).clickable {
            chooseImageLauncher()
        }
    ) {
        // Profile Image
        Image(
            painter = imageResource,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // Upload Icon in a Circle at the Bottom Right
        Box(
            modifier = Modifier
                .size(32.dp)  // Size of the circular icon container
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.BottomEnd)
                .border(1.dp, Color.LightGray, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "Upload Icon",
                tint = Color.Gray,  // Set the color of the upload icon
                modifier = Modifier
                    .size(20.dp) // Size of the upload icon itself
                    .align(Alignment.Center)
            )
        }
    }
}


@Composable
fun getUserImagePainter(user: User?): AsyncImagePainter {
    return if (user?.image == null) {
        rememberAsyncImagePainter(model = R.drawable.default_profile_image)
    } else {
        rememberAsyncImagePainter(model = Constant.Files_URL + user.image?.path)
    }
}