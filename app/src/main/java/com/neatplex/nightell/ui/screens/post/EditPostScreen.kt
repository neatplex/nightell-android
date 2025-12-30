package com.neatplex.nightell.ui.screens.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.CustomSimpleButton
import com.neatplex.nightell.utils.Result

@Composable
fun EditPostScreen(
    navController: NavController,
    postId: Int,
    postTitle: String,
    postDescription: String
) {
    var editedTitle by remember { mutableStateOf(postTitle) }
    var editedDescription by remember { mutableStateOf(postDescription) }
    var titleError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val postViewModel: PostViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Title input
        androidx.compose.material.OutlinedTextField(
            value = editedTitle.take(30), // Limit to 30 characters
            onValueChange = { newValue ->
                titleError = false
                if (newValue.length <= 30) editedTitle = newValue
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text("Title") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White.copy(0.3f),
                textColor = Color.Black,
                focusedIndicatorColor = if (titleError) colorResource(id = R.color.purple_light) else colorResource(
                    id = R.color.night
                ), // Pink bottom border if error
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = colorResource(id = R.color.night),
                errorCursorColor = Color.Red,
                errorIndicatorColor = Color.Red
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
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Caption input (description)
        androidx.compose.material.OutlinedTextField(
            value = editedDescription, // No need to restrict characters in real-time
            onValueChange = { newValue ->
                // Update the value without restriction; we will sanitize on save
                editedDescription = newValue
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            label = { Text("Caption", color = Color.Black) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.White.copy(0.3f),
                focusedBorderColor = colorResource(id = R.color.night), // Pink bottom border if error
                textColor = Color.Black,
                cursorColor = colorResource(id = R.color.night)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            maxLines = 10 // Allow multiple lines
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomSimpleButton(
            onClick = {
                // Sanitize the description before saving
                val sanitizedDescription = sanitizeDescription(editedDescription)
                editedDescription = sanitizedDescription // Apply sanitized description
                if (editedTitle.isEmpty()) {
                    titleError = true
                    focusRequester.requestFocus() // Focus on title field if empty
                } else {
                    titleError = false
                    if (editedTitle != postTitle || editedDescription != postDescription) {
                        postViewModel.updatePost(postId, editedTitle, editedDescription)
                    }

                    navController.popBackStack()

                }
            },
            text = "Save Changes"
        )
    }

    // Handle update result
    val postUpdateResult by postViewModel.postUpdateResult.observeAsState()
    postUpdateResult?.let {
        if (it is Result.Success) {
            // Close editing mode if update is successful
            navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
        }
    }
}

// Function to sanitize the description
fun sanitizeDescription(description: String): String {
    // Replace more than 3 consecutive newlines with 3 newlines
    val withoutExtraNewlines = description.replace(Regex("\n{4,}"), "\n\n\n")
    // Trim any trailing newlines
    return withoutExtraNewlines.trimEnd()
}