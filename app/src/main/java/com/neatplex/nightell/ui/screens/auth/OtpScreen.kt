package com.neatplex.nightell.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.common.ObserveAsEvents
import com.neatplex.nightell.ui.component.widget.CustomBorderedCircleButton
import com.neatplex.nightell.ui.component.widget.ErrorText
import com.neatplex.nightell.ui.component.widget.OtpTextField
import com.neatplex.nightell.ui.component.widget.TextFieldWithValidation
import com.neatplex.nightell.ui.navigation.MainDestinations
import com.neatplex.nightell.ui.screens.profile.EditProfileEvent
import com.neatplex.nightell.ui.screens.profile.EditProfileViewModel
import com.neatplex.nightell.ui.theme.OnSky
import com.neatplex.nightell.ui.theme.OnSkyMuted

/** Passwordless sign-in: email → one-time code → (new accounts) choose a username. */
@Composable
fun OtpVerificationScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    var currentStep by rememberSaveable { mutableIntStateOf(1) }
    var email by rememberSaveable { mutableStateOf("") }
    var otp by rememberSaveable { mutableStateOf("") }

    AuthBackground(
        overlay = {
            if (currentStep < 3) {
                IconButton(
                    onClick = {
                        if (currentStep == 2) {
                            otp = ""
                            currentStep = 1
                        } else {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.statusBarsPadding(),
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = OnSky)
                }
            }
        },
    ) {
        StepDots(current = currentStep, total = 3)
        AuthGap(24)
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "otp-step",
        ) { step ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (step) {
                    1 -> EmailStep(authViewModel, email, onEmailChange = { email = it }, onNext = { currentStep = 2 })
                    2 -> CodeStep(
                        authViewModel = authViewModel,
                        email = email,
                        otp = otp,
                        onOtpChange = { otp = it },
                        onNext = { currentStep = 3 },
                        onComplete = { navigateToMain(navController) },
                    )
                    else -> UsernameStep(authViewModel, onComplete = { navigateToMain(navController) })
                }
            }
        }
    }
}

@Composable
private fun EmailStep(
    authViewModel: AuthViewModel,
    email: String,
    onEmailChange: (String) -> Unit,
    onNext: () -> Unit,
) {
    val isEmailValid = authViewModel.isValidEmail(email)
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(authViewModel.events) { event ->
        if (event is AuthEvent.OtpSent) onNext()
    }

    StepTitle("Sign in with a code", stringResource(R.string.enter_a_valid_email_to_get_verification_code))
    AuthGap(28)
    TextFieldWithValidation(
        value = email,
        onValueChange = onEmailChange,
        placeholder = "Email",
        errorText = stringResource(R.string.invalid_email_format),
        leadingIcon = Icons.Rounded.Email,
        isValid = email.isEmpty() || isEmailValid,
        readOnly = false,
    )
    AuthGap(20)
    AuthPinkButton(
        onClick = { if (isEmailValid) authViewModel.sendOtp(email) },
        text = stringResource(R.string.send_code),
        isLoading = uiState.isLoading,
    )
    if (uiState.otpSendFailed) {
        AuthGap(20)
        ErrorText(stringResource(R.string.something_went_wrong_please_try_again))
    }
}

@Composable
private fun CodeStep(
    authViewModel: AuthViewModel,
    email: String,
    otp: String,
    onOtpChange: (String) -> Unit,
    onNext: () -> Unit,
    onComplete: () -> Unit,
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(authViewModel.events) { event ->
        if (event is AuthEvent.SignedIn) {
            // New accounts get their email as username; offer to change it.
            if (event.session.user.username == email) onNext() else onComplete()
        }
    }

    StepTitle("Check your email", "We sent a 6-digit code to $email")
    AuthGap(28)
    OtpTextField(
        otpText = otp,
        onOtpTextChange = { value, isComplete ->
            onOtpChange(value)
            if (isComplete) authViewModel.verifyOtp(email, value)
        },
    )
    AuthGap(20)
    if (uiState.isLoading) {
        Text("Checking…", color = OnSkyMuted, style = MaterialTheme.typography.bodyMedium)
    } else if (uiState.otpSecondsLeft > 0) {
        Text(
            "You can request a new code in ${uiState.otpSecondsLeft}s",
            color = OnSkyMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
    } else {
        TextButton(onClick = { authViewModel.sendOtp(email) }) {
            Text(stringResource(R.string.resend_code), color = OnSky)
        }
    }
    if (uiState.authError != null && !uiState.isLoading) {
        AuthGap(16)
        ErrorText(stringResource(R.string.the_verification_code_is_invalid_or_has_expired))
    }
}

@Composable
private fun UsernameStep(
    authViewModel: AuthViewModel,
    profileViewModel: EditProfileViewModel = hiltViewModel(),
    onComplete: () -> Unit,
) {
    var username by rememberSaveable { mutableStateOf("") }
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val isValid = username.isNotEmpty() && authViewModel.isValidUsername(username)

    ObserveAsEvents(profileViewModel.events) { event ->
        if (event is EditProfileEvent.Saved) onComplete()
    }

    StepTitle("Pick a username", "This is how listeners will find you. You can change it later.")
    AuthGap(28)
    TextFieldWithValidation(
        value = username,
        onValueChange = { username = it },
        placeholder = stringResource(R.string.username),
        errorText = getUserNameErrorMessage(username),
        leadingIcon = Icons.Rounded.AccountCircle,
        isValid = username.isEmpty() || isValid,
        readOnly = false,
    )
    AuthGap(20)
    AuthPinkButton(
        onClick = { if (isValid) profileViewModel.updateUsername(username) },
        text = stringResource(R.string.save_and_login),
        isLoading = uiState.isBusy,
    )
    AuthGap(12)
    CustomBorderedCircleButton(onClick = onComplete, text = stringResource(R.string.skip_for_now))
    uiState.error?.let {
        AuthGap(16)
        ErrorText(it)
    }
}

@Composable
private fun StepTitle(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = OnSky, textAlign = TextAlign.Center)
        AuthGapInline()
        Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = OnSkyMuted, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AuthGapInline() = Box(Modifier.height(8.dp))

@Composable
private fun StepDots(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f))
        repeat(total) { index ->
            val active = index + 1 == current
            Box(
                Modifier
                    .height(8.dp)
                    .width(if (active) 24.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (index + 1 <= current) OnSky else OnSkyMuted.copy(alpha = 0.35f)),
            )
        }
        Box(Modifier.weight(1f))
    }
}

private fun navigateToMain(navController: NavController) {
    navController.navigate(MainDestinations.Main.route) {
        popUpTo(0) { inclusive = true }
    }
}
