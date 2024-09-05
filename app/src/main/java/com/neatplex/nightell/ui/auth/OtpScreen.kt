package com.neatplex.nightell.ui.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.neatplex.nightell.component.CustomBorderedCircleButton
import com.neatplex.nightell.component.CustomCircularProgressIndicator
import com.neatplex.nightell.component.OtpTextField
import com.neatplex.nightell.component.TextFieldWithValidation
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.navigation.MainDestinations
import com.neatplex.nightell.ui.profile.ProfileViewModel
import com.neatplex.nightell.ui.theme.myLinearGradiant

@Composable
fun OtpVerificationScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    var currentStep by rememberSaveable { mutableStateOf(1) }
    var email by rememberSaveable { mutableStateOf("") }
    var otp by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = myLinearGradiant())
    ) {
        when (currentStep) {
            1 -> Step1(
                authViewModel = authViewModel,
                email = email,
                onEmailChange = { email = it },
                onNext = { currentStep = 2 }
            )

            2 -> Step2(
                authViewModel = authViewModel,
                email = email,
                otp = otp,
                onOtpChange = { otp = it },
                onNext = { currentStep = 3 },
                onComplete = {
                    navController.navigate(MainDestinations.Main.route)
                }
            )

            3 -> Step3(
                authViewModel = authViewModel,
                onComplete = {
                    navController.navigate(MainDestinations.Main.route)
                }
            )
        }
    }
}

@Composable
fun Step1(
    authViewModel: AuthViewModel,
    email: String,
    onEmailChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val isEmailValid = authViewModel.isValidEmail(email)
    val getOtpResult by authViewModel.getOtpResult.observeAsState()
    val isLoading by authViewModel.isLoading.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.enter_a_valid_email_to_get_verification_code),
            textAlign = TextAlign.Start,
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextFieldWithValidation(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Email",
            errorText = if (email.isNotEmpty() && !isEmailValid) stringResource(id = R.string.invalid_email_format) else "",
            leadingIcon = Icons.Default.Email,
            isValid = email.isEmpty() || isEmailValid,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthPinkButton(
            onClick = {
                if (isEmailValid) {
                    authViewModel.sendOtp(email)
                }
            },
            text = stringResource(R.string.send_code)
        )

        getOtpResult?.let {
            when (it) {
                is Result.Success -> {
                    onNext()
                }

                is Result.Failure -> {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        stringResource(id = R.string.something_went_wrong_please_try_again),
                        color = colorResource(id = R.color.purple_light)
                    )
                }
            }
        }
    }

    if (isLoading) {
        CustomCircularProgressIndicator()
    }
}

@Composable
fun Step2(
    authViewModel: AuthViewModel,
    email: String,
    otp: String,
    onOtpChange: (String) -> Unit,
    onNext: () -> Unit,
    onComplete: () -> Unit
) {
    val isLoading by authViewModel.isLoading.observeAsState(false)
    val timeLeft by authViewModel.timeLeft.observeAsState(0)
    val authResult by authViewModel.authResult.observeAsState()
    val errorMessage = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Enter OTP sent to $email!",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Reset otpClearedOnFailure when user starts typing again
        OtpTextField(
            otpText = otp,
            onOtpTextChange = { otpValue, isComplete ->
                onOtpChange(otpValue)
                if (isComplete) {
                    authViewModel.verifyOtp(email, otpValue)
                }
            },
            otpCount = 6,
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (timeLeft > 0) {
            Text("Resend code in $timeLeft seconds", color = Color.White)
        } else {
            AuthPinkButton(
                onClick = {
                    authViewModel.sendOtp(email)
                },
                text = stringResource(R.string.resend_code)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        errorMessage.value?.let {
            Text(
                text = it,
                color = colorResource(id = R.color.purple_light)
            )
        }

        authResult?.let {
            when (it) {
                is Result.Success -> {
                    if(it.data!!.user.username == email) {
                        onNext() // Move to step 3 if email matches
                    } else {
                        onComplete()
                    }
                }

                is Result.Failure -> {
                    Spacer(modifier = Modifier.height(24.dp))
                    errorMessage.value = stringResource(id = R.string.the_verification_code_is_invalid_or_has_expired)
                }
            }
        }
    }

    if (isLoading) {
        CustomCircularProgressIndicator()
    }
}

@Composable
fun Step3(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    val usernameUpdatedData by profileViewModel.usernameUpdatedData.observeAsState()
    val isLoading by profileViewModel.isLoading.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "You can update your username now or later!",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(32.dp))

        TextFieldWithValidation(
            value = username,
            onValueChange = { username = it },
            placeholder = stringResource(R.string.username),
            errorText = getUserNameErrorMessage(username),
            leadingIcon = Icons.Default.AccountBox,
            isValid = username.isNotEmpty() && authViewModel.isValidUsername(username),
            readOnly = false
        )
        Spacer(modifier = Modifier.height(32.dp))

        AuthPinkButton(
            onClick = {
                if (username.isNotEmpty() && authViewModel.isValidUsername(username)) {
                    // Perform final action
                    profileViewModel.updateUsernameOfUser(username)
                }
            },
            text = stringResource(R.string.save_and_login)
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomBorderedCircleButton(
            onClick = onComplete,
            text = stringResource(R.string.skip_for_now)
        )

        usernameUpdatedData.let {
            when (it) {
                is Result.Success -> {
                    onComplete()
                }

                is Result.Failure -> {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Error: ${it.message}", color = colorResource(id = R.color.purple_light))
                }

                else -> {}
            }
        }
    }

    if (isLoading) {
        CustomCircularProgressIndicator()
    }
}
