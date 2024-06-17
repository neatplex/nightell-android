package com.neatplex.nightell.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.theme.MyVerticalGradiant
import com.neatplex.nightell.utils.Validation.isValidEmail
import com.neatplex.nightell.utils.Validation.isValidPassword
import com.neatplex.nightell.utils.Validation.isValidUsername


@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authResultState by authViewModel.authResult.observeAsState()
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize().background(brush = MyVerticalGradiant())) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                NightellLogo()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Username TextField
            TextFieldWithValidation(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                errorText = getUserNameErrorMessage(username),
                leadingIcon = Icons.Default.AccountBox,
                isValid = username.isEmpty() || isValidUsername(username)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email TextField
            TextFieldWithValidation(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                errorText = if (email.isNotEmpty() && !isValidEmail(email)) "Invalid email format" else "",
                leadingIcon = Icons.Default.Email,
                isValid = email.isEmpty() || isValidEmail(email)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password TextField
            TextFieldWithValidation(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                errorText = if (password.isNotEmpty() && !isValidPassword(password)) "Password must be at least 8 characters long" else "",
                leadingIcon = Icons.Default.Lock,
                isValid = password.isEmpty() || isValidPassword(password),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(id = if (isPasswordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up Button
            Button(
                onClick = {
                    if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
                        isValidUsername(username) && isValidEmail(email) && isValidPassword(password)
                    ) {
                        authViewModel.registerUser(username, email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = androidx.compose.material.ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.purple_light).copy(alpha = 0.5f), // Set button background color to transparent
                )
            ) {
                Text(text = "Sign Up", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Sign In Text
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White
                            )
                        ) {
                            append("Do you have an account? ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = colorResource(id = R.color.blue_light),
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Sign In!")
                        }
                    },
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Handle authentication result
            authResultState?.let { AuthResult(it, navController) }

        }
    }
}

@Composable
fun TextFieldWithValidation(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorText: String,
    leadingIcon: ImageVector,
    isValid: Boolean,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val purpleErrorColor = colorResource(id = R.color.purple_light)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
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
            cursorColor = Color.Black,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,// Change text color if needed
            focusedBorderColor = Color.White, // Change border color when focused
            unfocusedBorderColor = Color.White,
            errorBorderColor = purpleErrorColor.copy(alpha = 0.5f), // Change border color when not focused
            errorLabelColor = purpleErrorColor, // Change border color when not focused
            errorTrailingIconColor = purpleErrorColor, // Change border color when not focused
            errorCursorColor = purpleErrorColor, // Change border color when not focused
            backgroundColor = Color.White.copy(alpha = 0.5f) // Set background color with 50% opacity
        )
    )
    if (value.isNotEmpty() && !isValid) { // Only show error text when the field is not empty and not valid
        Text(
            text = errorText,
            color = purpleErrorColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

fun getUserNameErrorMessage(username: String): String {
    return when {
        username.isEmpty() -> "Username cannot be empty"
        !username.first().isLetter() -> "Username must start with a letter"
        !Regex("^[a-z_][a-z0-9_]*$").matches(username) -> "Username can only contain lowercase letters, numbers, and underscores"
        username.length < 5 -> "Username must be at least 5 characters long"
        else -> ""
    }
}