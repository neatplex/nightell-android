package com.neatplex.nightell.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.neatplex.nightell.utils.Result
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.OtpTextField
import com.neatplex.nightell.component.TextFieldWithValidation
import com.neatplex.nightell.navigation.MainDestinations
import com.neatplex.nightell.ui.theme.myLinearGradiant

@Composable
fun OtpVerificationScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {

    val otpResult by authViewModel.getOtpResult.observeAsState()
    val timeLeft by authViewModel.timeLeft.observeAsState()
    val authResultState by authViewModel.authResult.observeAsState()
    val errorMessage by authViewModel.errorMessage.observeAsState()

    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessageState by remember { mutableStateOf("") }
    val isLoading by authViewModel.isLoading.observeAsState(false)

    // Set readOnly to true if otpResult is a success
    val isEmailReadOnly = otpResult is Result.Success

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = myLinearGradiant())
    ) {

        if (isLoading) {
            CustomCircularProgressIndicator()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextFieldWithValidation(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email",
                errorText = if (email.isNotEmpty() && !authViewModel.isValidEmail(email)) stringResource(
                    R.string.invalid_email_format
                ) else "",
                leadingIcon = Icons.Default.Email,
                isValid = email.isEmpty() || authViewModel.isValidEmail(email),
                readOnly = isEmailReadOnly
            )

            if (otpResult is Result.Success) {
                Spacer(modifier = Modifier.height(16.dp))

                timeLeft?.let {
                    Text("$it seconds", color = Color.White, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Use the OtpTextField for OTP input
                OtpTextField(
                    otpText = otp,
                    onOtpTextChange = { otpValue, isComplete ->
                        otp = otpValue
                        if (isComplete) {
                            authViewModel.verifyOtp(email, otpValue)
                        }
                    },
                    otpCount = 6
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (timeLeft == 0) {
                Text(
                    stringResource(R.string.time_s_up_request_a_new_code),
                    color = colorResource(id = R.color.blue_light)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AuthPinkButton(
                onClick = {
                    if (email.isNotEmpty() && authViewModel.isValidEmail(email)) {
                        if (timeLeft == 0 || timeLeft == null) {
                            authViewModel.sendOtp(email)
                        }
                    }
                },
                text = stringResource(R.string.send_code)
            )

            if (showError && errorMessageState.isNotEmpty()) {
                Text(
                    text = errorMessageState,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            errorMessage?.let {
                errorMessageState = it
                showError = true
            }
        }
    }

    LaunchedEffect(authResultState) {
        if (authResultState is Result.Success) {
            navController.popBackStack()
            navController.navigate(MainDestinations.Main.route)
        } else if (authResultState is Result.Failure) {
            errorMessageState = when ((authResultState as Result.Failure).code) {
                401 -> "Invalid Code"
                else -> "Something went wrong, please try again"
            }
            showError = true
            otp = ""  // Clear the OTP input on failure
        }
    }
}

