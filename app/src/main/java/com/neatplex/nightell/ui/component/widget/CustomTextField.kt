package com.neatplex.nightell.ui.component.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.ui.theme.OnSky
import com.neatplex.nightell.ui.theme.OnSkyMuted

/** A rounded search bar with a clear button. */
@Composable
fun CustomSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search stories and people",
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

/** A text field on the night-sky backdrop of the sign-in screens. */
@Composable
fun OutlinedTextFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    SkyTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        keyboardType = keyboardType,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
    )
}

/** A sky text field that shows [errorText] below it while the value is invalid. */
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
    readOnly: Boolean,
) {
    SkyTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        keyboardType = KeyboardType.Text,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        errorText = errorText.takeIf { value.isNotEmpty() && !isValid && it.isNotEmpty() },
    )
}

@Composable
private fun SkyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation,
    trailingIcon: (@Composable () -> Unit)?,
    readOnly: Boolean = false,
    errorText: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= MAX_LENGTH) onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        singleLine = true,
        readOnly = readOnly,
        isError = errorText != null,
        supportingText = errorText?.let { { Text(it) } },
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = OnSky,
            unfocusedTextColor = OnSky,
            focusedContainerColor = OnSky.copy(alpha = 0.10f),
            unfocusedContainerColor = OnSky.copy(alpha = 0.06f),
            errorContainerColor = OnSky.copy(alpha = 0.06f),
            cursorColor = OnSky,
            focusedBorderColor = OnSky,
            unfocusedBorderColor = OnSkyMuted.copy(alpha = 0.35f),
            focusedPlaceholderColor = OnSkyMuted,
            unfocusedPlaceholderColor = OnSkyMuted,
            focusedLeadingIconColor = OnSky,
            unfocusedLeadingIconColor = OnSkyMuted,
            focusedTrailingIconColor = OnSky,
            unfocusedTrailingIconColor = OnSkyMuted,
            errorBorderColor = SkyError,
            errorLeadingIconColor = SkyError,
            errorTextColor = OnSky,
            errorSupportingTextColor = SkyError,
            errorCursorColor = SkyError,
        ),
    )
}

/**
 * A profile field with its own save button. The button appears once the value
 * changes, and turns into a spinner while saving.
 */
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
    isLoading: Boolean,
    isSuccess: Boolean,
) {
    val showError = value.isNotEmpty() && !isValid && errorText.isNotEmpty()
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= length) onValueChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (singleLine) 56.dp else height.dp),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = imeAction),
        visualTransformation = visualTransformation,
        isError = showError,
        supportingText = {
            if (showError) Text(errorText) else if (!singleLine) Text("${value.length} / $length")
        },
        singleLine = singleLine,
        shape = MaterialTheme.shapes.medium,
        trailingIcon = {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                isSuccess -> Icon(Icons.Rounded.Check, contentDescription = "Saved", tint = MaterialTheme.colorScheme.tertiary)
                isChanged && isValid -> IconButton(onClick = onSaveClicked) {
                    Icon(Icons.Rounded.Check, contentDescription = "Save $label", tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
    )
}

private const val MAX_LENGTH = 75

/** Error colour readable on the night sky in both light and dark mode. */
private val SkyError = Color(0xFFFFA4B4)
