package com.neatplex.nightell.ui.screens.auth

import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.TextFieldWithValidation
import com.neatplex.nightell.ui.navigation.MainDestinations
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val isLoading = uiState.isLoading


    SignUpContent(
        authViewModel = authViewModel,
        email = email,
        username = username,
        password = password,
        isPasswordVisible = isPasswordVisible,
        onEmailChange = { email = it },
        onUsernameChange = { username = it },
        onPasswordChange = { password = it },
        onPasswordVisibilityChange = { isPasswordVisible = it },
        onSignUpClick = {
            if (isInputValid(username, email, password, authViewModel)) {
                authViewModel.registerUser(username, email, password)
            }
        },
        onSignInClick = {
            navController.navigate(MainDestinations.SignIn.route) {
                popUpTo(0) { inclusive = true }
            }
        },
        iSignUpInProgress = isLoading
    )
    // Handle authentication result
    AuthResult(authViewModel, uiState, navController)
}

@Composable
fun SignUpContent(
    authViewModel: AuthViewModel,
    email: String,
    username: String,
    password: String,
    isPasswordVisible: Boolean,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    iSignUpInProgress: Boolean
) {
    AuthBackground {
        Header(subtitle = "Create an account to share your stories")
        AuthGap(40)

        TextFieldWithValidation(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = stringResource(R.string.username),
            errorText = getUserNameErrorMessage(username),
            leadingIcon = Icons.Rounded.AccountCircle,
            isValid = username.isEmpty() || authViewModel.isValidUsername(username),
            readOnly = false
        )
        AuthGap(12)
        TextFieldWithValidation(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Email",
            errorText = stringResource(R.string.invalid_email_format),
            leadingIcon = Icons.Rounded.Email,
            isValid = email.isEmpty() || authViewModel.isValidEmail(email),
            readOnly = false
        )
        AuthGap(12)
        TextFieldWithValidation(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.password),
            errorText = "Password must be at least 8 characters long",
            leadingIcon = Icons.Rounded.Lock,
            isValid = password.isEmpty() || authViewModel.isValidPassword(password),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { PasswordToggle(isPasswordVisible, onPasswordVisibilityChange) },
            readOnly = false
        )
        AuthGap(28)
        AuthPinkButton(onSignUpClick, stringResource(R.string.sign_up), isLoading = iSignUpInProgress)
        AuthGap(16)
        CenteredTextWithClickablePart(
            normalText = stringResource(R.string.do_you_have_an_account),
            clickableText = stringResource(R.string.sign_in),
            onClick = onSignInClick,
        )
    }
}
