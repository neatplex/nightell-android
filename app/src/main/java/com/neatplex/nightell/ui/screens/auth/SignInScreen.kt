package com.neatplex.nightell.ui.screens.auth

import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.neatplex.nightell.ui.component.widget.CustomBorderedCircleButton
import com.neatplex.nightell.ui.component.widget.ErrorText
import com.neatplex.nightell.ui.component.widget.OutlinedTextFieldWithIcon
import com.neatplex.nightell.ui.navigation.MainDestinations
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neatplex.nightell.ui.common.ObserveAsEvents
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {

    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isLoading = uiState.isLoading

    val scope = rememberCoroutineScope()

    SignInContent(
        navController = navController,
        emailOrUsername = emailOrUsername,
        password = password,
        isPasswordVisible = isPasswordVisible,
        onEmailOrUsernameChange = { emailOrUsername = it },
        onPasswordChange = { password = it },
        onPasswordVisibilityChange = { isPasswordVisible = it },
        onSignInClick = {
            if (emailOrUsername.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(emailOrUsername, password)
            }
        },
        onGoogleSignInClick = {
            scope.launch {
                when (val result = requestGoogleIdToken(context)) {
                    is GoogleSignInResult.Success -> viewModel.signInWithGoogle(result.idToken)
                    is GoogleSignInResult.Failure -> viewModel.onGoogleSignInFailed(result.message)
                    GoogleSignInResult.Cancelled -> Unit
                }
            }
        },
        onSignUpClick = {
            navController.navigate(MainDestinations.SignUp.route)
        },
        iSignInInProgress = isLoading
    )

    // Handle authentication result
    AuthResult(viewModel, uiState, navController)
}

@Composable
fun SignInContent(
    navController: NavController,
    emailOrUsername: String,
    password: String,
    isPasswordVisible: Boolean,
    onEmailOrUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onSignInClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    iSignInInProgress: Boolean
) {
    AuthBackground {
        Header()
        AuthGap(40)

        OutlinedTextFieldWithIcon(
            value = emailOrUsername,
            onValueChange = onEmailOrUsernameChange,
            placeholder = stringResource(R.string.email_or_username),
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Rounded.Person
        )
        AuthGap(12)
        OutlinedTextFieldWithIcon(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.password),
            keyboardType = KeyboardType.Password,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = Icons.Rounded.Lock,
            trailingIcon = { PasswordToggle(isPasswordVisible, onPasswordVisibilityChange) }
        )
        AuthGap(24)
        AuthPinkButton(onSignInClick, stringResource(R.string.sign_in), isLoading = iSignInInProgress)
        AuthGap(24)
        SkyDivider(stringResource(R.string.or))
        AuthGap(24)
        CustomBorderedCircleButton(
            onClick = onGoogleSignInClick,
            text = stringResource(R.string.sign_in_with_google),
        )
        AuthGap(12)
        CustomBorderedCircleButton(
            onClick = { navController.navigate(MainDestinations.Otp.route) },
            text = stringResource(R.string.continue_with_otp),
        )
        AuthGap(24)
        CenteredTextWithClickablePart(
            normalText = "New to Nightell?",
            clickableText = stringResource(R.string.sign_up),
            onClick = onSignUpClick,
        )
    }
}

/** Show/hide button for a password field. */
@Composable
fun PasswordToggle(isVisible: Boolean, onToggle: (Boolean) -> Unit) {
    IconButton(onClick = { onToggle(!isVisible) }) {
        Icon(
            painter = painterResource(if (isVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
            contentDescription = if (isVisible) "Hide password" else "Show password",
        )
    }
}

/** Navigates on successful sign-in and shows the error after a failed one. */
@Composable
fun AuthResult(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    navController: NavController,
) {
    ObserveAsEvents(viewModel.events) { event ->
        if (event is AuthEvent.SignedIn) {
            navController.navigate(MainDestinations.Main.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val error = uiState.authError
    val errorMessage = when {
        error == null -> null
        error.isUserFacing -> error.message
        error.code == 401 -> stringResource(R.string.your_username_or_password_is_incorrect)
        error.code == 422 -> error.message
        else -> stringResource(R.string.something_went_wrong_please_try_again)
    }
    val isLoading = uiState.isLoading

    if (errorMessage != null && !isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            ErrorText(
                text = errorMessage
            )
        }
    }
}
