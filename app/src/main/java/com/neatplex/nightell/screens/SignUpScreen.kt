package com.neatplex.nightell.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Color
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
import com.neatplex.nightell.ui.theme.NightellTheme
import com.neatplex.nightell.util.Result
import com.neatplex.nightell.util.Validation
import com.neatplex.nightell.viewmodels.UserAuthViewModel


@Composable
fun SignUpScreen(navController: NavController, userAuthViewModel: UserAuthViewModel = hiltViewModel()) {

    val authResultState by userAuthViewModel.authResult.observeAsState()

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isEmailValid by remember { mutableStateOf(false) }
    var isUsernameValid by remember { mutableStateOf(false) }
    var isPasswordValid by remember { mutableStateOf(false) }


    NightellTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .background(MaterialTheme.colors.primary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        usernameError = ""
                        isUsernameValid = it.isNotBlank() && Validation.isValidUsername(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Username") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            usernameError = if (!isUsernameValid)
                                "Username must start with a letter, can contain letters, numbers, and underscores, and must be at least 5 characters long"
                            else
                                ""
                        }) {
                            Icon(
                                imageVector = if (isUsernameValid) Icons.Default.Check else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (isUsernameValid) Color.Green else Color.Gray
                            )
                        }
                    }
                )

                Text(text = usernameError, color = Color.Red)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = ""
                        isEmailValid = it.isNotBlank() && Validation.isValidEmail(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            emailError = if (!isEmailValid)
                                "Email should be in a standard email form."
                            else
                                ""
                        }) {
                            Icon(
                                imageVector = if (isEmailValid) Icons.Default.Check else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (isEmailValid) Color.Green else Color.Gray
                            )
                        }
                    }
                    )

                Text(text = emailError)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = ""
                        isPasswordValid = it.isNotBlank() && Validation.isValidPassword(it)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Password") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painter = painterResource(id = if (isPasswordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                                contentDescription = null
                            )
                        }
                    }
                )

                Text(text = passwordError)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {

                        val isEmailValid = Validation.isValidEmail(email)
                        val isUsernameValid = Validation.isValidUsername(username)
                        val isPasswordValid = Validation.isValidPassword(password)

                        if(isUsernameValid && isEmailValid && isPasswordValid){
                            userAuthViewModel.registerUser(username, email, password)
                        }else {
                            // Clear previous errors
                            emailError = ""
                            usernameError = ""
                            passwordError = ""
                            // Set error messages
                            if (!isEmailValid) emailError = "Please Enter a valid email!"
                            if (!isUsernameValid) usernameError =
                                "Username must start with a letter, can contain letters, numbers, and underscores, and must be at least 5 characters long"
                            if (!isPasswordValid) passwordError =
                                "Password must be at least 8 characters long"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Sign Up")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    buildAnnotatedString {
                        append("Do you have an account? ")
                        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                            append("Sign In!")
                        }
                    },
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )


                // Check the authentication result and navigate accordingly
                when (val result = authResultState) {
                    is Result.Success -> {
                        navController.navigate("home")
                    }
                    is Result.Error -> {
                        val errorMessage = result.message
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = errorMessage, color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    else -> {

                    }
                }

            }
        }
    }

}