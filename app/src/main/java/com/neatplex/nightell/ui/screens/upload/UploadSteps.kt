package com.neatplex.nightell.ui.screens.upload

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.ErrorBanner
import kotlinx.coroutines.delay

/* The three steps of the new-post wizard and their small building blocks. */

@Composable
fun AudioUploadStep(
    selectedAudioName: String,
    onChooseAudio: () -> Unit,
    audioId: Int,
    onNext: () -> Unit,
) {
    StepIntro("Add your recording", "Choose the MP3 file of your story. Up to 5 MB.")
    FilePickerCard(
        iconRes = R.drawable.baseline_audio_file_48,
        title = if (audioId != 0) selectedAudioName else "Choose an audio file",
        subtitle = if (audioId != 0) "Tap to choose a different file" else "MP3 only",
        isDone = audioId != 0,
        onClick = onChooseAudio,
    )
}

@Composable
fun ImageUploadStep(
    selectedImageName: String,
    onChooseImage: () -> Unit,
    imageId: Int?,
    onNext: () -> Unit,
) {
    StepIntro("Pick a cover", "A JPG image up to 2 MB. This step is optional.")
    FilePickerCard(
        iconRes = R.drawable.baseline_image_48,
        title = if (imageId != null) selectedImageName else "Choose a cover image",
        subtitle = if (imageId != null) "Tap to choose a different image" else "JPG only",
        isDone = imageId != null,
        onClick = onChooseImage,
    )
}

@Composable
fun TitleAndCaptionStep(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    titleError: Boolean,
) {
    StepIntro("Name your story", "A short title and an optional caption for listeners.")
    OutlinedTextField(
        value = title,
        onValueChange = { if (it.length <= MAX_TITLE) onTitleChange(it) },
        label = { Text("Title") },
        singleLine = true,
        isError = titleError,
        supportingText = {
            Text(if (titleError) "Title can't be empty" else "${title.length} / $MAX_TITLE")
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.size(8.dp))
    OutlinedTextField(
        value = description,
        onValueChange = { if (it.length <= MAX_CAPTION) onDescriptionChange(it) },
        label = { Text("Caption") },
        supportingText = { Text("${description.length} / $MAX_CAPTION") },
        minLines = 4,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun StepIntro(title: String, subtitle: String) {
    Text(title, style = MaterialTheme.typography.headlineSmall)
    Text(
        subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 6.dp, bottom = 24.dp),
    )
}

/** A large tappable area for choosing a file, with a tick once uploaded. */
@Composable
private fun FilePickerCard(
    iconRes: Int,
    title: String,
    subtitle: String,
    isDone: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = if (isDone) colors.primaryContainer else colors.surfaceContainer,
        border = BorderStroke(1.dp, if (isDone) colors.primary else colors.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painterResource(iconRes),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(56.dp),
            )
            Spacer(Modifier.size(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isDone) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = "Uploaded", tint = colors.primary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

/** Shows [errorMessage] and clears it after a few seconds. */
@Composable
fun ShowErrorMessage(errorMessage: String, onClearError: () -> Unit) {
    if (errorMessage.isEmpty()) return
    ErrorBanner(errorMessage, modifier = Modifier.padding(top = 16.dp))
    LaunchedEffect(errorMessage) {
        delay(5000)
        onClearError()
    }
}

private const val MAX_TITLE = 30
private const val MAX_CAPTION = 250
