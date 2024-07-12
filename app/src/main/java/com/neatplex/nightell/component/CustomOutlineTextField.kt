package com.neatplex.nightell.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
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
            .background(color = Color.LightGray.copy(alpha = 0.5f)),
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
                        text = "Text Here....",
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
                    modifier = Modifier.fillMaxWidth()
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
    trailingIcon: (@Composable () -> Unit)? = null
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
            errorBorderColor = purpleErrorColor, // Change border color when not focused
            errorCursorColor = purpleErrorColor, // Change border color when not focused
            backgroundColor = Color.Black.copy(alpha = 0.1f) // Set background color with 50% opacity
        ),
        singleLine = true
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