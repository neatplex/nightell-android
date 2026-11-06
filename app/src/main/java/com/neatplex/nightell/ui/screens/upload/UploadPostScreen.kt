package com.neatplex.nightell.ui.screens.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.screens.post.sanitizeDescription
import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.ui.util.activityViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.navigation.Routes
import com.neatplex.nightell.ui.common.ObserveAsEvents
import com.neatplex.nightell.ui.util.rememberFilePicker

@Composable
fun AddPostScreen(
    navController: NavController,
    uploadViewModel: UploadViewModel = hiltViewModel(activityViewModelStoreOwner()),
) {
    val uiState by uploadViewModel.uiState.collectAsStateWithLifecycle()

    val chooseAudio = rememberFilePicker { picked ->
        uploadViewModel.onFileChosen(picked.name, picked.sizeBytes, FileType.AUDIO, picked::toLocalFile)
    }
    val chooseImage = rememberFilePicker { picked ->
        uploadViewModel.onFileChosen(picked.name, picked.sizeBytes, FileType.IMAGE, picked::toLocalFile)
    }

    ObserveAsEvents(uploadViewModel.events) { event ->
        when (event) {
            UploadEvent.Published -> resetState(navController)
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
                        onChooseAudio = { chooseAudio("audio/*") },
                        audioId = uiState.audio?.id ?: 0,
                        onNext = uploadViewModel::nextStep,
                    )

                    2 -> ImageUploadStep(
                        selectedImageName = uiState.image?.name.orEmpty(),
                        onChooseImage = { chooseImage("image/*") },
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
                        UploadedFileRow(iconId = R.drawable.baseline_audio_file_48, fileName = it.name)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    uiState.image?.let {
                        UploadedFileRow(iconId = R.drawable.baseline_image_48, fileName = it.name)
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
