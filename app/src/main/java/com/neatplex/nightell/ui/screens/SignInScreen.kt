package com.neatplex.nightell.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import com.neatplex.nightell.ui.viewmodel.UserAuthViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.navigation.Screens
import com.neatplex.nightell.ui.theme.MyVerticalGradiant
import com.neatplex.nightell.utils.Result
import kotlinx.coroutines.delay


@Composable
fun SignInScreen(navController: NavController, viewModel: UserAuthViewModel = hiltViewModel()) {
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val authResultState by viewModel.authResult.observeAsState()

    Box(modifier = Modifier.fillMaxSize().background(brush = MyVerticalGradiant())) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NightellLogo()

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextFieldWithIcon(
                value = emailOrUsername,
                onValueChange = { emailOrUsername = it },
                label = "Email Or Username",
                keyboardType = KeyboardType.Email,
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextFieldWithIcon(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                keyboardType = KeyboardType.Password,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = Icons.Default.Lock,
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

            Button(
                onClick = {
                    if (emailOrUsername.isNotEmpty() && password.isNotEmpty()) {
                        viewModel.loginUser(emailOrUsername, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = androidx.compose.material.ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.purple_light).copy(alpha = 0.5f), // Set button background color to transparent
                )
            ) {
                Text(text = "Sign In", color = Color.White)
            }

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
fun NightellLogo() {
    Image(
        painter = painterResource(id = R.drawable.nightell_white),
        contentDescription = null,
        modifier = Modifier.size(150.dp)
    )
}

@Composable
private fun OutlinedTextFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = Color.Black,
            textColor = Color.Black,
            unfocusedLabelColor = Color.White,
            focusedLabelColor = Color.White,// Change text color if needed
            focusedBorderColor = Color.White, // Change border color when focused
            unfocusedBorderColor = Color.White, // Change border color when not focused
            backgroundColor = Color.White.copy(alpha = 0.5f) // Set background color with 50% opacity
        )
    )
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
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (authResultState as? Result.Error)?.message ?: "",
                color = Color.Red,
                fontSize = 16.sp
            )
        }
    }

    when (val result = authResultState) {
        is Result.Success -> {
            result.data?.let {
                // Successful login, navigate to HomeScreen
                navController.navigate(Screens.Home.route)
            }
        }

        else -> {}
    }
}
