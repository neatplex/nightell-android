package com.neatplex.nightell.ui.screens.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.neatplex.nightell.ui.common.ObserveAsEvents
import com.neatplex.nightell.ui.component.common.AppTopBar
import com.neatplex.nightell.ui.component.widget.CustomSimpleButton
import com.neatplex.nightell.ui.component.widget.ErrorBanner

/** Edit a story's title and caption. Closes once the changes are saved. */
@Composable
fun EditPostScreen(
    navController: NavController,
    postId: Int,
    postTitle: String,
    postDescription: String
) {
    var editedTitle by remember { mutableStateOf(postTitle) }
    var editedDescription by remember { mutableStateOf(postDescription.trim()) }
    var titleError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val editPostViewModel: EditPostViewModel = hiltViewModel()
    val uiState by editPostViewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(editPostViewModel.events) { event ->
        when (event) {
            EditPostEvent.Saved -> {
                navController.previousBackStackEntry?.savedStateHandle?.set("postChanged", true)
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Edit story", onBack = { navController.popBackStack() }) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            OutlinedTextField(
                value = editedTitle,
                onValueChange = {
                    titleError = false
                    if (it.length <= MAX_TITLE) editedTitle = it
                },
                label = { Text("Title") },
                singleLine = true,
                isError = titleError,
                supportingText = { Text(if (titleError) "Title can't be empty" else "${editedTitle.length} / $MAX_TITLE") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = editedDescription,
                onValueChange = { if (it.length <= MAX_CAPTION) editedDescription = it },
                label = { Text("Caption") },
                supportingText = { Text("${editedDescription.length} / $MAX_CAPTION") },
                minLines = 5,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            CustomSimpleButton(
                text = "Save changes",
                isLoading = uiState.isSaving,
                onClick = {
                    editedDescription = sanitizeDescription(editedDescription)
                    if (editedTitle.isBlank()) {
                        titleError = true
                        focusRequester.requestFocus()
                    } else if (editedTitle != postTitle || editedDescription != postDescription.trim()) {
                        // Stay on screen until the save finishes, so leaving doesn't cancel it.
                        editPostViewModel.updatePost(postId, editedTitle, editedDescription)
                    } else {
                        navController.popBackStack()
                    }
                },
            )
            uiState.error?.let { ErrorBanner(it, modifier = Modifier.padding(top = 16.dp)) }
        }
    }
}

private const val MAX_TITLE = 30
private const val MAX_CAPTION = 250

// Function to sanitize the description
fun sanitizeDescription(description: String): String {
    // Replace more than 3 consecutive newlines with 3 newlines
    val withoutExtraNewlines = description.replace(Regex("\n{4,}"), "\n\n\n")
    // Trim any trailing newlines
    return withoutExtraNewlines.trimEnd()
}