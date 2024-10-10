package com.neatplex.nightell.ui.screens.auth

import android.app.Activity
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.neatplex.nightell.ui.component.widget.CustomBorderedCircleButton
import com.neatplex.nightell.ui.component.widget.CustomCircularProgressIndicator
import com.neatplex.nightell.ui.component.widget.ErrorText
import com.neatplex.nightell.ui.component.widget.OutlinedTextFieldWithIcon
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.ui.theme.myLinearGradiant
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.navigation.MainDestinations

@Composable
fun SignInScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {

    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val authResultState by viewModel.authResult.observeAsState()
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(false)

    val oneTapSignInClient = remember {
        Identity.getSignInClient(context)
    }

    val signInRequest = remember {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                val credential = oneTapSignInClient.getSignInCredentialFromIntent(intent)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    viewModel.signInWithGoogle(idToken)
                } else {
                    // Handle the case where ID token is null
                }
            }
        } else {
            // Handle the case where the user closed the Google Sign-In launcher without completing sign-in
        }
    }

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
            oneTapSignInClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    try {
                        googleSignInLauncher.launch(
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle sign-in failure
                    e.printStackTrace()
                }
        },
        onSignUpClick = {
            navController.navigate(MainDestinations.SignUp.route)
        },
        iSignInInProgress = isLoading
    )

    // Handle authentication result
    authResultState?.let { AuthResult(it, navController, isLoading) }
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = myLinearGradiant())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Header()

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextFieldWithIcon(
                value = emailOrUsername,
                onValueChange = onEmailOrUsernameChange,
                placeholder = stringResource(R.string.email_or_username),
                keyboardType = KeyboardType.Email,
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextFieldWithIcon(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = stringResource(R.string.password),
                keyboardType = KeyboardType.Password,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = Icons.Default.Lock,
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibilityChange(!isPasswordVisible) }) {
                        Icon(
                            painter = painterResource(id = if (isPasswordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                            contentDescription = "visible/invisible password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthPinkButton(onSignInClick, stringResource(R.string.sign_in))

            Spacer(modifier = Modifier.height(24.dp))

            // Add a line with "or" in the middle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.White)
                Text(
                    text = stringResource(R.string.or),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.White,
                    fontSize = 16.sp
                )
                Divider(modifier = Modifier.weight(1f), color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomBorderedCircleButton(
                onClick = onGoogleSignInClick,
                text = stringResource(R.string.sign_in_with_google)
            )

            Spacer(modifier = Modifier.height(24.dp))

            CenteredTextWithClickablePart(
                normalText = stringResource(R.string.prefer_sign_in_up_without_password),
                clickableText = stringResource(R.string.continue_with_otp),
                onClick = { navController.navigate(MainDestinations.Otp.route) },
            )
        }

        if (iSignInInProgress) {
            CustomCircularProgressIndicator()
        }
    }
}

@Composable
fun AuthResult(
    authResultState: Result<AuthResponse?>,
    navController: NavController,
    isLoading: Boolean
) {
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (isLoading) {
        showError = false
    }

    when (authResultState) {
        is Result.Failure -> {
            showError = true
            errorMessage = when (authResultState.code) {
                401 -> stringResource(R.string.your_username_or_password_is_incorrect)
                422 -> authResultState.message
                else -> stringResource(R.string.something_went_wrong_please_try_again)
            }
        }

        is Result.Success -> {
            authResultState.data?.let {
                navController.popBackStack()
                navController.navigate(MainDestinations.Main.route)
            }
        }
    }

    if (showError) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            ErrorText(
                text = errorMessage
            )
        }
    }
}
