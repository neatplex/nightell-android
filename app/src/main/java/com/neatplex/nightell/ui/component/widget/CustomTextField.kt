package com.neatplex.nightell.ui.component.widget

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neatplex.nightell.R

@Composable
fun CustomSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(value)) }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .height(50.dp)
            .fillMaxWidth()
            .background(color = Color.LightGray.copy(alpha = 0.3f)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search, // Use Icons.Default.Search for the search icon
                    contentDescription = "Search Icon",
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (text.text.isEmpty()) {
                    Text(
                        text = "Search Title....",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                        onValueChange(newText.text)
                    },
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Search text field" }
                )
            }
        }
    }
}

@Composable
fun OutlinedTextFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= 75) {
                onValueChange(it)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = Color.White,
            placeholderColor = Color.White.copy(alpha = 0.8f),
            textColor = Color.White,
            leadingIconColor = Color.White.copy(alpha = 0.6f),
            trailingIconColor = Color.White.copy(alpha = 0.6f),
            focusedBorderColor = Color.White, // Change border color when focused
            unfocusedBorderColor = Color.White, // Change border color when not focused
            backgroundColor = Color.Black.copy(alpha = 0.1f) // Set background color with 50% opacity
        ),
        singleLine = true
    )
}

@Composable
fun TextFieldWithValidation(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorText: String,
    leadingIcon: ImageVector,
    isValid: Boolean,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
    readOnly: Boolean
) {
    val purpleErrorColor = colorResource(id = R.color.purple_light)

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= 75) {
                onValueChange(it)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        visualTransformation = visualTransformation,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        trailingIcon = trailingIcon,
        isError = !isValid && value.isNotEmpty(), // Display error when the field is not empty and not valid
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = Color.White,
            placeholderColor = Color.White.copy(alpha = 0.8f),
            textColor = Color.White,
            leadingIconColor = Color.White.copy(alpha = 0.6f),
            trailingIconColor = Color.White.copy(alpha = 0.6f),
            focusedBorderColor = Color.White, // Change border color when focused
            unfocusedBorderColor = Color.White,
            errorBorderColor = purpleErrorColor,
            errorTrailingIconColor = purpleErrorColor,// Change border color when not focused
            errorCursorColor = purpleErrorColor, // Change border color when not focused
            backgroundColor = Color.Black.copy(alpha = 0.1f) // Set background color with 50% opacity
        ),
        singleLine = true,
        readOnly = readOnly
    )

    if (value.isNotEmpty() && !isValid) { // Only show error text when the field is not empty and not valid
        Text(
            text = errorText,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun EditProfileTextFieldWithValidation(
    value: String,
    onValueChange: (String) -> Unit,
    isChanged: Boolean,
    onSaveClicked: () -> Unit,
    label: String,
    errorText: String,
    length: Int,
    isValid: Boolean,
    singleLine: Boolean,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    imeAction: ImeAction,
    height: Int,
    isLoading: Boolean,          // New state for loading
    isSuccess: Boolean           // New state for success
) {
    val purpleErrorColor = colorResource(id = R.color.purple_light)
    val greenSuccessColor = Color(0xFF4CAF50) // Green color for success

    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= length) {
                onValueChange(it)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp),
        label = { androidx.compose.material3.Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        visualTransformation = visualTransformation,
        isError = !isValid && value.isNotEmpty(), // Display error when the field is not empty and not valid
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Gray, // Change text color if needed
            focusedBorderColor = Color.Black, // Change border color when focused
            unfocusedBorderColor = Color.Gray,
            errorBorderColor = purpleErrorColor.copy(alpha = 0.5f), // Change border color when not focused
            errorLabelColor = purpleErrorColor, // Change border color when not focused
            errorTrailingIconColor = purpleErrorColor, // Change border color when not focused
            errorCursorColor = purpleErrorColor, // Change border color when not focused
        ),
        singleLine = singleLine,
        trailingIcon = {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
                isSuccess -> {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Success",
                        tint = greenSuccessColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                isChanged && isValid -> {
                    IconButton(
                        onClick = onSaveClicked,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Save",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    )
    if (value.isNotEmpty() && !isValid) {
        androidx.compose.material3.Text(
            text = errorText,
            color = purpleErrorColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun TitleInputField(
    title: String,
    onTitleChange: (String) -> Unit,
    titleError: Boolean,
    focusRequester: FocusRequester
) {
    Column {
        OutlinedTextField(
            value = title.take(30), // Limit to 30 characters
            onValueChange = { newValue ->
                onTitleChange(newValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text("Title") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.White.copy(0.3f),
                textColor = Color.Black,
                focusedBorderColor = if (titleError) colorResource(id = R.color.purple_light) else colorResource(
                    id = R.color.night
                ), // Pink bottom border if error
                unfocusedBorderColor = Color.Gray,
                cursorColor = colorResource(id = R.color.night),
                errorCursorColor = Color.Red,
                errorBorderColor = Color.Red,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            isError = titleError
        )

        if (titleError) {
            Text(
                text = "Title can't be empty",
                color = Color.Red,
                fontSize = 12.sp
            )
        }
    }
}
