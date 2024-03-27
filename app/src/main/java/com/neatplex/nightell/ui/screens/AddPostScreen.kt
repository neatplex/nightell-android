package com.neatplex.nightell.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neatplex.nightell.ui.viewmodel.FileViewModel
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.ui.viewmodel.PostViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun AddPostScreen(fileViewModel: FileViewModel = hiltViewModel(), postViewModel : PostViewModel = hiltViewModel()) {

    val uploadFileResults by fileViewModel.uploadState.observeAsState()
    val uploadPostResult by postViewModel.storePostResult.observeAsState()

    var selectedAudio by remember { mutableStateOf<Uri?>(null) }
    var selectedAudioName by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var selectedImageName by remember { mutableStateOf("") }

    var title by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var audioMessage by rememberSaveable { mutableStateOf("") }
    var imageMessage by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var audioId by rememberSaveable { mutableStateOf(0) }
    var imageId by rememberSaveable { mutableStateOf<Int?>(null) }


    val context = LocalContext.current



    val chooseAudioLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { audioUri ->
                audioMessage = ""
                val (fileName, mainUri) = getFileNameAndUri(context, audioUri)
                selectedAudio = mainUri
                var fileExtension = selectedAudioName?.substringAfterLast('.', "")
                val file = uriToFile(context, selectedAudio!!)
                if(fileExtension.equals("mp3", ignoreCase = true)){
                    selectedAudioName = fileName!!
                    fileViewModel.uploadFile(file!!, "MP3")
                }
                else{
                    audioMessage = "Only MP3 audio files are allowed"
                }
            }
        }

    val chooseImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { imageUri ->
                imageMessage = ""
                val (fileName, mainUri) = getFileNameAndUri(context, imageUri)
                selectedImage = mainUri
                var fileExtension = selectedImageName?.substringAfterLast('.', "")
                val file = uriToFile(context, selectedImage!!)
                if(fileExtension.equals("jpg", ignoreCase = true)){
                    selectedImageName = fileName!!
                    fileViewModel.uploadFile(file!!, "JPG")
                }
                else{
                    imageMessage = "Only JPG Image files are allowed"
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Add your Compose UI elements here

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    chooseAudioLauncher.launch("audio/*")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Choose MP3 Audio")
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(audioMessage, color = Color.Red)
        Text(text = selectedAudioName)

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    chooseImageLauncher.launch("image/*")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Choose JPG Image")
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(imageMessage, color = Color.Red)
        Text(text = selectedImageName)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = title,
            onValueChange = {
                title = it
            },
            label = { Text("Title") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = {
                description = it
            },
            label = { Text("Description") }
        )

        // Observe audio upload results
        when (val result = uploadFileResults) {
            is Result.Success -> {
                result.data?.let {
                    if (it.file.extension == "MP3") {
                        audioId = it.file.id

                    }else if(it.file.extension == "JPG"){
                        imageId = it.file.id

                    }
                }
            }
            is Result.Error -> {
                if (result.code == 401){
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Token expired!", color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))

                }else if(result.code == 500){
                    Toast.makeText(
                        LocalContext.current,
                        "Server Internal error, Try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }else if(result.code == 413){
                    Toast.makeText(
                        LocalContext.current,
                        "File is too large",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            else -> {}
        }

        Button(onClick = {
            if(selectedAudio != null && title.isNotEmpty()){
                postViewModel.uploadPost(title, description, audioId, imageId)
            }else{
                errorMessage = "Audio file and Title are required!"
            }
        }) {
            Text("Upload Story" , color = Color.Red)
        }


        Text(errorMessage)

        uploadPostResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    val postUploadResponse = result.data
                    Toast.makeText(
                        LocalContext.current,
                        "Uploaded ${postUploadResponse!!.post.description}",
                        Toast.LENGTH_SHORT
                    ).show()

                    Text(postUploadResponse.post.title)

                    // Clear the state variables
                    selectedAudio = null
                    selectedAudioName = ""
                    selectedImage = null
                    selectedImageName = ""
                    title = ""
                    description = ""
                    audioId = 0
                    imageId = null

                }

                is Result.Error -> {
                    val errorMessage = result.message
                    // Handle error
                }

                Result.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun showToast(s: String, context: Context) {
    Toast.makeText(
        context,
        s,
        Toast.LENGTH_LONG
    ).show()
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