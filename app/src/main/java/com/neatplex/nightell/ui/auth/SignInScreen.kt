package com.neatplex.nightell.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.neatplex.nightell.component.CustomBorderedCircleButton
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.ErrorText
import com.neatplex.nightell.component.OutlinedTextFieldWithIcon
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.navigation.Screens
import com.neatplex.nightell.ui.theme.myLinearGradiant
import com.neatplex.nightell.utils.Result
import kotlinx.coroutines.delay

@Composable
fun SignInScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {

    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val authResultState by viewModel.authResult.observeAsState()
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(false)

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task, viewModel)
        } else {
            // Handle the case where the user closed the Google Sign-In launcher without completing sign-in
            // Optionally, you can show an error message or handle this case as needed
        }
    }

    SignInContent(
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
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        },
        onSignUpClick = {
            navController.navigate("SignUp")
        },
        isGoogleSignInInProgress = isLoading,
        authResultState = authResultState,
        navController = navController
    )
    // Handle authentication result
    authResultState?.let { AuthResult(it, navController) }
}

@Composable
fun SignInContent(
    emailOrUsername: String,
    password: String,
    isPasswordVisible: Boolean,
    onEmailOrUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onSignInClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    isGoogleSignInInProgress: Boolean,
    authResultState: Result<AuthResponse?>?,
    navController: NavController
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(brush = myLinearGradiant())) {

        if (isGoogleSignInInProgress) {
            CustomCircularProgressIndicator()
        }

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
                placeholder = "Email Or Username",
                keyboardType = KeyboardType.Email,
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextFieldWithIcon(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "Password",
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

            Spacer(modifier = Modifier.height(32.dp))

            SignUpButton(onSignInClick, "Sign In")

            Spacer(modifier = Modifier.height(16.dp))

            CustomBorderedCircleButton(
                onClick = onGoogleSignInClick,
                text = "Sign In with Google"
            )

            Spacer(modifier = Modifier.height(16.dp))

            CenteredTextWithClickablePart(
                normalText = "Don't you have an account? ",
                clickableText = "Sign Up!",
                onClick = onSignUpClick
            )
        }
    }
}

@Composable
fun AuthResult(authResultState: Result<AuthResponse?>, navController: NavController) {
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(authResultState) {
        if (authResultState is Result.Failure) {
            showError = true
            delay(5000) // Show error for 5 seconds
            showError = false
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
                text = (authResultState as? Result.Failure)?.message ?: ""
            )
        }
    }

    when (val result = authResultState) {
        is Result.Success -> {
            result.data?.let {
                navController.navigate(Screens.Home.route)
            }
        }

        else -> {}
    }
}

private fun handleSignInResult(
    completedTask: Task<GoogleSignInAccount>,
    viewModel: AuthViewModel
) {
    try {
        val account = completedTask.getResult(ApiException::class.java)
        account?.idToken?.let { idToken ->
            // Send the ID token to your backend via your ViewModel
            viewModel.signInWithGoogle(idToken)
        }
    } catch (e: ApiException) {
        // Handle sign-in failure
        e.printStackTrace() // Log the exception for debugging purposes
        // Optionally, you can display an error message to the user here
    }
}