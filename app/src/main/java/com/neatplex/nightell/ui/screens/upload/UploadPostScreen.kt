package com.neatplex.nightell.ui.screens.upload

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.neatplex.nightell.ui.component.common.AppTopBar
import com.neatplex.nightell.ui.component.widget.CustomSimpleButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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

    var titleError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "New story",
                onBack = if (uiState.step > 1) uploadViewModel::previousStep else null,
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    when (uiState.step) {
                        1 -> CustomSimpleButton(
                            text = "Next",
                            onClick = uploadViewModel::nextStep,
                            enabled = uiState.audio != null && !uiState.isLoading,
                        )
                        2 -> CustomSimpleButton(
                            text = if (uiState.image != null) "Next" else "Skip for now",
                            onClick = uploadViewModel::nextStep,
                            enabled = !uiState.isLoading,
                        )
                        else -> CustomSimpleButton(
                            text = "Publish story",
                            isLoading = uiState.isLoading,
                            onClick = {
                                titleError = uiState.title.isBlank()
                                if (!titleError) uploadViewModel.publish(::sanitizeDescription)
                            },
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            StepProgress(step = uiState.step, total = 3)
            Spacer(Modifier.height(24.dp))
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
                else -> TitleAndCaptionStep(
                    title = uiState.title,
                    description = uiState.description,
                    onTitleChange = {
                        titleError = false
                        uploadViewModel.setTitle(it)
                    },
                    onDescriptionChange = uploadViewModel::setDescription,
                    titleError = titleError,
                )
            }
            if (uiState.isLoading && uiState.step < 3) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
            }
            ShowErrorMessage(errorMessage = uiState.error.orEmpty()) { uploadViewModel.clearError() }
        }
    }
}

/** "Step 2 of 3" with a progress bar. */
@Composable
private fun StepProgress(step: Int, total: Int) {
    Column {
        Text(
            "Step $step of $total",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { step / total.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            strokeCap = StrokeCap.Round,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }
}

private fun resetState(navController: NavController) {
    navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
    navController.navigate(Routes.ADD_POST) {
        popUpTo(Routes.ADD_POST) { inclusive = true }
    }
}
