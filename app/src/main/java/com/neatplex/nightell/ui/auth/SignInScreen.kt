package com.neatplex.nightell.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
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
import com.neatplex.nightell.ui.theme.feelFree
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
    var isGoogleSignInInProgress by remember { mutableStateOf(false) }


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
        }
    }

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
            androidx.compose.material3.Text(
                text = "Nightell",
                fontFamily = feelFree,
                fontSize = 85.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextFieldWithIcon(
                value = emailOrUsername,
                onValueChange = { emailOrUsername = it },
                placeholder = "Email Or Username",
                keyboardType = KeyboardType.Email,
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextFieldWithIcon(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                keyboardType = KeyboardType.Password,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = Icons.Default.Lock,
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(id = if (isPasswordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                            contentDescription = "visible/invisible password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (emailOrUsername.isNotEmpty() && password.isNotEmpty()) {
                        viewModel.loginUser(emailOrUsername, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.purple), // Set button background color to transparent
                )
            ) {
                Text(text = "Sign In", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomBorderedCircleButton(
                onClick = {
                    isGoogleSignInInProgress = true
                    googleSignInClient.signOut().addOnCompleteListener {
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    }
                },
                text = "Sign In with Google")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White
                        )
                    ) {
                        append("Don't you have an account? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = colorResource(id = R.color.blue_light),
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Sign Up!")
                    }
                },
                modifier = Modifier.clickable {
                    navController.navigate("signUp")
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Handle authentication result
            authResultState?.let { AuthResult(it, navController) }
        }
    }
}

@Composable
fun AuthResult(authResultState: Result<AuthResponse?>, navController: NavController) {
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(authResultState) {
        if (authResultState is Result.Error) {
            showError = true
            delay(5000) // Show error for 5 seconds
            showError = false
        }
    }

    if (showError) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            ErrorText(
                text = (authResultState as? Result.Error)?.message ?: ""
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