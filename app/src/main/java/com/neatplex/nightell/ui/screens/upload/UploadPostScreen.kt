package com.neatplex.nightell.ui.screens.upload

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.CustomBorderedButton
import com.neatplex.nightell.ui.component.widget.CustomLinkButton
import com.neatplex.nightell.ui.component.widget.CustomSimpleButton
import com.neatplex.nightell.ui.screens.post.sanitizeDescription
import com.neatplex.nightell.ui.viewmodel.SharedViewModel
import com.neatplex.nightell.utils.FileHandler
import com.neatplex.nightell.utils.Result
import kotlinx.coroutines.delay

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AddPostScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    uploadViewModel: UploadViewModel = hiltViewModel(),
) {
    // Restore the current step from the SharedViewModel
    var currentStep by rememberSaveable {
        mutableStateOf(
            sharedViewModel.currentUploadStep.value ?: 1
        )
    }

    val uploadFileResults by uploadViewModel.uploadState.observeAsState()
    val uploadPostResult by uploadViewModel.storePostResult.observeAsState()
    val uploadPostIsLoading by uploadViewModel.isLoading.observeAsState(false)
    val uploadFileIsLoading by uploadViewModel.isLoading.observeAsState(false)

    // Use stored file names and IDs from SharedViewModel, or fallback to defaults
    var selectedAudioName by remember {
        mutableStateOf(
            sharedViewModel.audioFileState.value?.fileName ?: ""
        )
    }
    var selectedImageName by remember {
        mutableStateOf(
            sharedViewModel.imageFileState.value?.fileName ?: ""
        )
    }
    var audioId by remember { mutableStateOf(sharedViewModel.audioFileState.value?.fileId ?: 0) }
    var imageId by remember { mutableStateOf(sharedViewModel.imageFileState.value?.fileId) }

    var title by rememberSaveable { mutableStateOf(sharedViewModel.postTitle.value ?: "") }
    var description by rememberSaveable {
        mutableStateOf(
            sharedViewModel.postDescription.value ?: ""
        )
    }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val fileHandler = remember { FileHandler(context, uploadViewModel) }

    val chooseAudioLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { uri ->
                fileHandler.handleAudioFile(uri,
                    onSuccess = { fileName ->
                        selectedAudioName = fileName
                    },
                    onError = { message -> errorMessage = message }
                )
            }
        }

    val chooseImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { uri ->
                fileHandler.handleImageFile(uri,
                    onSuccess = { fileName ->
                        selectedImageName = fileName
                    },
                    onError = { message -> errorMessage = message }
                )
            }
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(30.dp)
        ) {
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(Color.White, shape = RoundedCornerShape(24.dp))
            ) {
                when (currentStep) {
                    1 -> AudioUploadStep(
                        selectedAudioName = selectedAudioName,
                        chooseAudioLauncher = chooseAudioLauncher,
                        audioId = audioId,
                        onNext = {
                            if (audioId != 0) {
                                currentStep = 2
                                sharedViewModel.setCurrentStep(currentStep)
                            }
                        }
                    )

                    2 -> ImageUploadStep(
                        selectedImageName = selectedImageName,
                        chooseImageLauncher = chooseImageLauncher,
                        imageId = imageId,
                        onNext = {
                            currentStep = 3
                            sharedViewModel.setCurrentStep(currentStep)
                        }
                    )

                    3 -> TitleAndCaptionStep(
                        title = title,
                        description = description,
                        onTitleChange = {
                            title = it
                            sharedViewModel.setPostTitle(it)  // Persist title
                        },
                        onDescriptionChange = {
                            description = it
                            sharedViewModel.setPostDescription(it)  // Persist description
                        },
                        onSubmit = {
                            if (audioId != 0) {
                                if (description != "") description =
                                    sanitizeDescription(description)
                                uploadViewModel.uploadPost(title, description, audioId, imageId)
                            }
                        }
                    )
                }

                // Handle upload results
                when (val result = uploadFileResults) {
                    is Result.Success -> {
                        result.data?.let {
                            if (it.file.extension.equals("MP3", ignoreCase = true)) {
                                audioId = it.file.id
                                sharedViewModel.setAudioFileState(audioId, selectedAudioName)
                            } else if (it.file.extension.equals("JPG", ignoreCase = true)) {
                                imageId = it.file.id
                                sharedViewModel.setImageFileState(imageId, selectedImageName)
                            }
                        }
                    }

                    is Result.Failure -> {
                        errorMessage = result.message
                    }

                    else -> {}
                }

                uploadPostResult?.let { result ->
                    when (result) {
                        is Result.Success -> {
                            currentStep = 1
                            sharedViewModel.setCurrentStep(currentStep)
                            sharedViewModel.resetPostData()
                            resetState(navController)
                        }

                        is Result.Failure -> {
                            errorMessage = result.message
                        }
                    }
                }
            }

            Row {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ShowLoadingIndicator(isLoading = uploadPostIsLoading || uploadFileIsLoading)

                    ShowErrorMessage(errorMessage = errorMessage) {
                        errorMessage = ""
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (audioId != 0) {
                        UploadedFileRow(
                            iconId = R.drawable.baseline_audio_file_48,
                            fileName = selectedAudioName
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (imageId != null) {
                        UploadedFileRow(
                            iconId = R.drawable.baseline_image_48,
                            fileName = selectedImageName
                        )
                    }
                }
            }
        }
    }
}

private fun resetState(navController: NavController) {
    navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
    navController.navigate("addPost") {
        popUpTo("addPost") { inclusive = true }
    }
}

@Composable
fun AudioUploadStep(
    selectedAudioName: String,
    chooseAudioLauncher: ActivityResultLauncher<String>,
    audioId: Int,
    onNext: () -> Unit
) {
    val uploadImage = rememberAsyncImagePainter(model = R.drawable.upload)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            modifier = Modifier
                .size(100.dp),
            painter = uploadImage,
            contentDescription = "Choose Audio File"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Choose mp3 audio file", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Text(selectedAudioName)

        Spacer(modifier = Modifier.height(16.dp))

        CustomBorderedButton(
            onClick = { chooseAudioLauncher.launch("audio/*") },
            text = "Select file"
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (audioId != 0) {
            CustomLinkButton(
                onClick = onNext,
                text = "Next",
                color = colorResource(id = R.color.night)
            )
        } else {
            CustomLinkButton(onClick = onNext, text = "Next", color = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ImageUploadStep(
    selectedImageName: String,
    chooseImageLauncher: ActivityResultLauncher<String>,
    imageId: Int?,
    onNext: () -> Unit
) {
    val uploadImage = rememberAsyncImagePainter(model = R.drawable.upload)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            modifier = Modifier
                .size(100.dp),
            painter = uploadImage,
            contentDescription = stringResource(R.string.choose_image_file)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.choose_jpeg_image_file),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(selectedImageName)

        Spacer(modifier = Modifier.height(16.dp))

        CustomBorderedButton(
            onClick = { chooseImageLauncher.launch("image/*") },
            text = "Select file"
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (imageId != null) {
            CustomLinkButton(
                onClick = onNext,
                text = "Next",
                color = colorResource(id = R.color.night)
            )
        } else {
            CustomLinkButton(
                onClick = onNext,
                text = "Skip",
                color = colorResource(id = R.color.night)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleAndCaptionStep(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var titleError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(30.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.material.OutlinedTextField(
            value = title.take(30), // Limit to 30 characters
            onValueChange = { newValue ->
                titleError = false
                if (newValue.length <= 30) onTitleChange(newValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text("Title") },
            colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White.copy(0.3f),
                textColor = Color.Black,
                focusedIndicatorColor = if (titleError) colorResource(id = R.color.purple_light) else colorResource(
                    id = R.color.night
                ), // Pink bottom border if error
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = colorResource(id = R.color.night),
                errorCursorColor = Color.Red,
                errorIndicatorColor = Color.Red // Pink for error state
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            isError = titleError
        )

        if (titleError) {
            Text(
                text = stringResource(R.string.title_can_t_be_empty),
                color = Color.Red,
                fontSize = 12.sp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                if (it.length <= 250) onDescriptionChange(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            label = {
                Text("Caption", color = Color.Black) // Changing label color
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.night),
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            CustomSimpleButton(onClick = {
                if (title.isEmpty()) {
                    titleError = true
                    focusRequester.requestFocus() // Focus on title field if empty
                } else {
                    titleError = false
                    onSubmit()
                }
            }, text = "Upload Post")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ShowLoadingIndicator(isLoading: Boolean) {
    if (isLoading) {
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = colorResource(id = R.color.night),
        )
    }
}

@Composable
fun ShowErrorMessage(errorMessage: String, onClearError: () -> Unit) {
    if (errorMessage.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            errorMessage,
            color = colorResource(id = R.color.purple_light),
            textAlign = TextAlign.Center
        )
        LaunchedEffect(key1 = errorMessage) {
            delay(5000)
            onClearError()
        }
    }
}

@Composable
fun UploadedFileRow(iconId: Int, fileName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(32.dp)
                .padding(end = 8.dp),
            tint = colorResource(id = R.color.night),
            painter = painterResource(id = iconId),
            contentDescription = "Uploaded File"
        )
        Text(text = fileName, fontWeight = FontWeight.SemiBold)
    }
}
