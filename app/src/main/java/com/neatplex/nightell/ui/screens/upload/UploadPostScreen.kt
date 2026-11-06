package com.neatplex.nightell.ui.screens.upload

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.neatplex.nightell.ui.viewmodel.SessionViewModel
import kotlinx.coroutines.delay
import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.ui.util.activityViewModelStoreOwner
import com.neatplex.nightell.ui.util.getFileNameAndUri
import com.neatplex.nightell.ui.util.getFileSize
import com.neatplex.nightell.ui.util.uriToFile
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.navigation.Routes

@Composable
fun AddPostScreen(
    navController: NavController,
    uploadViewModel: UploadViewModel = hiltViewModel(activityViewModelStoreOwner()),
) {
    val uiState by uploadViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Validates a picked file, copies it into the cache and uploads it.
    fun onFilePicked(uri: Uri, type: FileType) {
        val (fileName, _) = getFileNameAndUri(context, uri)
        val name = fileName.orEmpty()
        val error = uploadViewModel.validate(name, getFileSize(context, uri), type)
        val file = if (error == null) uriToFile(context, uri) else null
        if (file != null) {
            uploadViewModel.uploadFile(file, name, type)
        } else {
            uploadViewModel.showError(error ?: "Could not read the selected file")
        }
    }

    val chooseAudioLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { onFilePicked(it, FileType.AUDIO) }
        }

    val chooseImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { onFilePicked(it, FileType.IMAGE) }
        }

    LaunchedEffect(uiState.isPublished) {
        if (uiState.isPublished) {
            uploadViewModel.onPublishedHandled()
            resetState(navController)
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
                when (uiState.step) {
                    1 -> AudioUploadStep(
                        selectedAudioName = uiState.audio?.name.orEmpty(),
                        chooseAudioLauncher = chooseAudioLauncher,
                        audioId = uiState.audio?.id ?: 0,
                        onNext = uploadViewModel::nextStep,
                    )

                    2 -> ImageUploadStep(
                        selectedImageName = uiState.image?.name.orEmpty(),
                        chooseImageLauncher = chooseImageLauncher,
                        imageId = uiState.image?.id,
                        onNext = uploadViewModel::nextStep,
                    )

                    3 -> TitleAndCaptionStep(
                        title = uiState.title,
                        description = uiState.description,
                        onTitleChange = uploadViewModel::setTitle,
                        onDescriptionChange = uploadViewModel::setDescription,
                        onSubmit = { uploadViewModel.publish(::sanitizeDescription) },
                    )
                }
            }

            Row {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ShowLoadingIndicator(isLoading = uiState.isLoading)

                    ShowErrorMessage(errorMessage = uiState.error.orEmpty()) {
                        uploadViewModel.clearError()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    uiState.audio?.let {
                        UploadedFileRow(
                            iconId = R.drawable.baseline_audio_file_48,
                            fileName = it.name
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    uiState.image?.let {
                        UploadedFileRow(
                            iconId = R.drawable.baseline_image_48,
                            fileName = it.name
                        )
                    }
                }
            }
        }
    }
}

private fun resetState(navController: NavController) {
    navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
    navController.navigate(Routes.ADD_POST) {
        popUpTo(Routes.ADD_POST) { inclusive = true }
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
