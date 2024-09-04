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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.TextFieldWithValidation
import com.neatplex.nightell.navigation.MainDestinations
import com.neatplex.nightell.ui.theme.feelFree
import com.neatplex.nightell.ui.theme.myLinearGradiant

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
    val isLoading by authViewModel.isLoading.observeAsState(false)


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
    authResultState?.let { AuthResult(it, navController, isLoading) }
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = myLinearGradiant())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()
            Spacer(modifier = Modifier.height(32.dp))

            TextFieldWithValidation(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = stringResource(R.string.username),
                errorText = getUserNameErrorMessage(username),
                leadingIcon = Icons.Default.AccountBox,
                isValid = username.isEmpty() || authViewModel.isValidUsername(username),
                readOnly = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextFieldWithValidation(
                value = email,
                onValueChange = onEmailChange,
                placeholder = "Email",
                errorText = if (email.isNotEmpty() && !authViewModel.isValidEmail(email)) stringResource(
                    R.string.invalid_email_format
                ) else "",
                leadingIcon = Icons.Default.Email,
                isValid = email.isEmpty() || authViewModel.isValidEmail(email),
                readOnly = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextFieldWithValidation(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = stringResource(R.string.password),
                errorText = if (password.isNotEmpty() && !authViewModel.isValidPassword(password)) "Password must be at least 8 characters long" else "",
                leadingIcon = Icons.Default.Lock,
                isValid = password.isEmpty() || authViewModel.isValidPassword(password),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibilityChange(!isPasswordVisible) }) {
                        Icon(
                            painter = painterResource(id = if (isPasswordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                            contentDescription = "visible/invisible password"
                        )
                    }
                },
                readOnly = false
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthPinkButton(onSignUpClick, stringResource(R.string.sign_up))

            Spacer(modifier = Modifier.height(16.dp))

            CenteredTextWithClickablePart(
                normalText = stringResource(R.string.do_you_have_an_account),
                clickableText = stringResource(R.string.sign_in),
                onClick = onSignInClick,
                vertAlignment = Alignment.Bottom
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (iSignUpInProgress) {
            CustomCircularProgressIndicator()
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.nightell),
            fontFamily = feelFree,
            fontSize = 85.sp,
            color = Color.White
        )
    }
}

@Composable
fun AuthPinkButton(onClick: () -> Unit, text: String) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.purple)
        )
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}

fun isInputValid(
    username: String,
    email: String,
    password: String,
    authViewModel: AuthViewModel
): Boolean {
    return username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
            authViewModel.isValidUsername(username) && authViewModel.isValidEmail(email) && authViewModel.isValidPassword(
        password
    )
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

@Composable
fun CenteredTextWithClickablePart(
    normalText: String,
    clickableText: String,
    onClick: () -> Unit,
    vertAlignment: Alignment.Vertical
) {
    Row() {
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.White)) {
                    append(normalText)
                }
                withStyle(
                    style = SpanStyle(
                        color = colorResource(id = R.color.blue_light),
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(clickableText)
                }
            },
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}