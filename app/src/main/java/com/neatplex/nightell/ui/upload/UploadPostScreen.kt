package com.neatplex.nightell.ui.upload

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomBorderedButton
import com.neatplex.nightell.component.CustomLinkButton
import com.neatplex.nightell.component.CustomSimpleButton
import com.neatplex.nightell.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AddPostScreen(
    tokenState: String?,
    navController: NavController,
    uploadViewModel: UploadViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    var currentStep by rememberSaveable { mutableStateOf(1) }

    val uploadFileResults by uploadViewModel.uploadState.observeAsState()
    val uploadPostResult by uploadViewModel.storePostResult.observeAsState()
    val uploadPostIsLoading by uploadViewModel.isLoading.observeAsState(false)
    val uploadFileIsLoading by uploadViewModel.isLoading.observeAsState(false)

    var selectedAudio by remember { mutableStateOf<Uri?>(null) }
    var selectedAudioName by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var selectedImageName by remember { mutableStateOf("") }

    var title by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var audioId by rememberSaveable { mutableStateOf(0) }
    var imageId by rememberSaveable { mutableStateOf<Int?>(null) }

    val context = LocalContext.current

    val chooseAudioLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { audioUri ->
                val (fileName, mainUri) = getFileNameAndUri(context, audioUri)
                selectedAudio = mainUri
                val fileExtension = fileName?.substringAfterLast('.', "")
                val file = uriToFile(context, selectedAudio!!)
                if (fileExtension.equals("mp3", ignoreCase = true)) {
                    selectedAudioName = fileName!!
                    uploadViewModel.uploadFile(file!!, "MP3")
                } else {
                    errorMessage = "Only .mp3 type is allowed for post audio!"
                }
            }
        }

    val chooseImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { imageUri ->
                val (fileName, mainUri) = getFileNameAndUri(context, imageUri)
                selectedImage = mainUri
                val fileExtension = fileName?.substringAfterLast('.', "")
                val file = uriToFile(context, selectedImage!!)
                if (fileExtension.equals("jpg", ignoreCase = true) ||
                    fileExtension.equals("jpeg", ignoreCase = true)
                ) {
                    selectedImageName = fileName!!
                    uploadViewModel.uploadFile(file!!, "JPG")
                } else {
                    errorMessage = "Only .jpg type is allowed for post image!"
                }
            }
        }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth()
                .weight(1f)
                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(24.dp))
        ) {
            when (currentStep) {
                1 -> AudioUploadStep(
                    selectedAudioName = selectedAudioName,
                    chooseAudioLauncher = chooseAudioLauncher,
                    audioId = audioId,
                    onNext = { if (audioId != 0) currentStep++ }
                )

                2 -> ImageUploadStep(
                    selectedImageName = selectedImageName,
                    chooseImageLauncher = chooseImageLauncher,
                    imageId = imageId,
                    onNext = { currentStep++ }
                )

                3 -> TitleAndCaptionStep(
                    title = title,
                    description = description,
                    onTitleChange = { title = it },
                    onDescriptionChange = { description = it },
                    onSubmit = {
                        if (selectedAudio != null && title.isNotEmpty()) {
                            uploadViewModel.uploadPost(title, description, audioId, imageId)
                        } else {
                            errorMessage = "Title is required!"
                        }
                    }
                )


            }

            // Observe audio and image upload results
            when (val result = uploadFileResults) {
                is Result.Success -> {
                    result.data?.let {
                        if (it.file.extension.equals("MP3", ignoreCase = true)) {
                            audioId = it.file.id
                        } else if (it.file.extension.equals("JPG", ignoreCase = true)) {
                            imageId = it.file.id
                        }
                    }
                }

                is Result.Error -> {
                    errorMessage = result.message
                }

                else -> {}
            }

            if (uploadPostIsLoading || uploadFileIsLoading) {
                LinearProgressIndicator(
                    color = MaterialTheme.colors.onPrimary
                )
            }

            uploadPostResult?.let { result ->
                when (result) {
                    is Result.Success -> {
                        // Clear the state variables
                        selectedAudio = null
                        selectedAudioName = ""
                        selectedImage = null
                        selectedImageName = ""
                        title = ""
                        description = ""
                        audioId = 0
                        imageId = null
                        errorMessage = ""

                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }

                    is Result.Error -> {
                        errorMessage = result.message
                    }

                    Result.Loading -> {
                        LinearProgressIndicator(
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(errorMessage, color = colorResource(id = R.color.purple_light))
                    if (errorMessage.length > 5) {
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(5000)
                            errorMessage = ""
                        }
                    }
                }
                if (audioId != 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp),
                            tint = colorResource(id = R.color.night),
                            painter = painterResource(id = R.drawable.baseline_audio_file_48),
                            contentDescription = "Choose Audio File"
                        )
                        Text(text = selectedAudioName)
                    }
                }
                if (imageId != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp),
                            tint = colorResource(id = R.color.night),
                            painter = painterResource(id = R.drawable.baseline_image_48),
                            contentDescription = "Choose Image File"
                        )
                        Text(text = selectedImageName)
                    }
                }
            }
        }
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
                .size(80.dp),
            painter = uploadImage,
            contentDescription = "Choose Image File"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Choose jpeg image file", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Text(selectedImageName)

        Spacer(modifier = Modifier.height(16.dp))

        CustomBorderedButton(
            onClick = { chooseImageLauncher.launch("image/*") },
            text = "Select file"
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (imageId != 0) {
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
    }
}

@Composable
fun TitleAndCaptionStep(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(30.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            TextField(
                value = title,
                onValueChange = {
                    if (it.length <= 25) onTitleChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Title", color = Color.Black) // Changing label color
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White.copy(0.5f), // Changing background color
                    textColor = Color.Black, // Changing text color
                    focusedBorderColor = colorResource(id = R.color.night),
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            TextField(
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
                    backgroundColor = Color.White.copy(0.5f), // Changing background color
                    textColor = Color.Black, // Changing text color
                    focusedBorderColor = colorResource(id = R.color.night),
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            CustomSimpleButton(onClick = onSubmit, text = "Upload Post")
        }
    }
}

fun getFileNameAndUri(context: Context, uri: Uri): Pair<String?, Uri?> {
    var fileName: String? = null
    var mainUri: Uri? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            fileName = cursor.getString(nameIndex)
            mainUri = uri
        }
    }
    return Pair(fileName, mainUri)
}

fun uriToFile(context: Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("temp_file", null, context.cacheDir)
    tempFile.deleteOnExit()
    inputStream?.use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    }
    return tempFile
}