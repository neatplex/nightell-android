package com.neatplex.nightell.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.AuthSession
import com.neatplex.nightell.domain.usecase.AuthUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Why signing in failed, so the screen can pick the right wording.
 *
 * @property isUserFacing true when [message] is already written for the user
 * and should be shown as is (e.g. a Google sign-in problem on the device).
 */
data class AuthError(val message: String, val code: Int?, val isUserFacing: Boolean = false)

data class AuthUiState(
    val isLoading: Boolean = false,
    val authError: AuthError? = null,
    /** True when sending the one-time code failed. */
    val otpSendFailed: Boolean = false,
    /** Seconds left before the user may request another code. */
    val otpSecondsLeft: Int = 0,
)

sealed interface AuthEvent {
    /** The user is signed in. */
    data class SignedIn(val session: AuthSession) : AuthEvent

    /** A one-time code was sent by email. */
    data object OtpSent : AuthEvent
}

/** Sign in, sign up and email one-time-password flows. */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val validator: Validator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events: Flow<AuthEvent> = _events.receiveAsFlow()

    private var timerJob: Job? = null

    fun registerUser(username: String, email: String, password: String) =
        authenticate { authUseCase.register(username, email, password) }

    fun loginUser(emailOrUsername: String, password: String) =
        authenticate { authUseCase.login(emailOrUsername, password) }

    fun signInWithGoogle(idToken: String) = authenticate { authUseCase.signInWithGoogle(idToken) }

    fun verifyOtp(email: String, otp: String) = authenticate { authUseCase.verifyOtp(email, otp) }

    /** Shows a problem that happened on the device before reaching our server. */
    fun onGoogleSignInFailed(message: String) {
        _uiState.update { it.copy(isLoading = false, authError = AuthError(message, code = null, isUserFacing = true)) }
    }

    fun sendOtp(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, otpSendFailed = false) }
            when (val result = authUseCase.sendOtp(email)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(AuthEvent.OtpSent)
                    startTimer(result.data)
                }
                is Result.Failure -> _uiState.update { it.copy(isLoading = false, otpSendFailed = true) }
            }
        }
    }

    fun isValidEmail(email: String) = validator.isValidEmail(email)

    fun isValidPassword(password: String) = validator.isValidPassword(password)

    fun isValidUsername(username: String) = validator.isValidUsername(username)

    private fun authenticate(block: suspend () -> Result<AuthSession>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }
            when (val result = block()) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(AuthEvent.SignedIn(result.data))
                }
                is Result.Failure -> _uiState.update {
                    it.copy(isLoading = false, authError = AuthError(result.message, result.code))
                }
            }
        }
    }

    private fun startTimer(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (left in seconds downTo 0) {
                _uiState.update { it.copy(otpSecondsLeft = left) }
                delay(1_000L)
            }
        }
    }
}
