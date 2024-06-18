package com.neatplex.nightell.ui.upload

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomSimpleButton
import com.neatplex.nightell.ui.theme.myHorizontalGradiant
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
                if (fileExtension.equals("jpg", ignoreCase = true)) {
                    selectedImageName = fileName!!
                    uploadViewModel.uploadFile(file!!, "JPG")
                } else {
                    errorMessage = "Only .jpg type is allowed for post image!"
                }
            }
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ) {

        Column(
            modifier = Modifier
                .padding(30.dp)
                .align(alignment = Alignment.Center)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
                Surface(
                    elevation = 4.dp,
                    shape = CircleShape,
                    modifier = Modifier
                ) {

                    IconButton(
                        onClick = {
                            if (audioId != 0) {
                                chooseImageLauncher.launch("image/*")
                            } else {
                                chooseAudioLauncher.launch("audio/*")
                            }
                        },
                        modifier = Modifier
                            .size(80.dp)
                    ) {
                        val horizontalGradientBrush = myHorizontalGradiant()

                        Icon(
                            modifier = Modifier
                                .graphicsLayer(alpha = 0.99f)
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(
                                            horizontalGradientBrush,
                                            blendMode = BlendMode.SrcAtop
                                        )
                                    }
                                },
                            painter = if (audioId != 0) {
                                painterResource(id = R.drawable.baseline_image_48)
                            } else {
                                painterResource(id = R.drawable.baseline_audio_file_48)
                            },
                            contentDescription = "Choose File"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 4.dp)) {
                Text(
                    text = if (audioId == 0) {
                        "CHOOSE MP3 AUDIO FILE"
                    } else {
                        "CHOOSE JPG IMAGE FILE"
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                if(audioId != 0){
                    Text("Audio: $selectedAudioName")
                }
                if(imageId != null){
                    Text("Image: $selectedImageName")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row() {
                TextField(
                    value = title,
                    onValueChange = {
                        title = it.take(25) // Limiting input to 250 characters
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Title", color = Color.Black) // Changing label color
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White.copy(0.5f), // Changing background color
                        textColor = Color.Black, // Changing text color
                        focusedBorderColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row() {
                TextField(
                    value = description,
                    onValueChange = {
                        description = it.take(250) // Limiting input to 250 characters
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Caption", color = Color.Black) // Changing label color
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White.copy(0.5f), // Changing background color
                        textColor = Color.Black, // Changing text color
                        focusedBorderColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    )
                )
            }
            // Observe audio upload results
            when (val result = uploadFileResults) {
                is Result.Success -> {
                    result.data?.let {
                        if (it.file.extension == "MP3") {
                            audioId = it.file.id

                        } else if (it.file.extension == "JPG") {
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
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colors.surface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {

                CustomSimpleButton(
                    onClick = {
                        if (selectedAudio != null && title.isNotEmpty()) {
                            uploadViewModel.uploadPost(title, description, audioId, imageId)
                        } else {
                            errorMessage = "Audio file and Title are required!"
                        }
                    },
                    text = "Upload Post"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
                Text(errorMessage, color = Color.Red)
                if(errorMessage.length > 5){
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(5000)
                        errorMessage = ""
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
                        // Reset error message
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
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
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