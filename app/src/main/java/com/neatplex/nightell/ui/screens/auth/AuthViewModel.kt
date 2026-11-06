package com.neatplex.nightell.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.AuthSession
import com.neatplex.nightell.domain.usecase.AuthUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Sign in, sign up and email one-time-password flows. */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val validator: Validator,
) : ViewModel() {

    /** Result of the last sign-in/sign-up attempt; null before the first one. */
    private val _authResult = MutableStateFlow<Result<AuthSession>?>(null)
    val authResult: StateFlow<Result<AuthSession>?> = _authResult.asStateFlow()

    /** Result of the last OTP request (its value is the code's lifetime in seconds). */
    private val _otpResult = MutableStateFlow<Result<Int>?>(null)
    val otpResult: StateFlow<Result<Int>?> = _otpResult.asStateFlow()

    /** Seconds left before the user may request another code. */
    private val _timeLeft = MutableStateFlow(0)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var timerJob: Job? = null

    fun registerUser(username: String, email: String, password: String) =
        authenticate { authUseCase.register(username, email, password) }

    fun loginUser(emailOrUsername: String, password: String) =
        authenticate { authUseCase.login(emailOrUsername, password) }

    fun signInWithGoogle(idToken: String) = authenticate { authUseCase.signInWithGoogle(idToken) }

    fun verifyOtp(email: String, otp: String) = authenticate { authUseCase.verifyOtp(email, otp) }

    fun sendOtp(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authUseCase.sendOtp(email)
            _otpResult.value = result
            _isLoading.value = false
            if (result is Result.Success) startTimer(result.data)
        }
    }

    fun isValidEmail(email: String) = validator.isValidEmail(email)

    fun isValidPassword(password: String) = validator.isValidPassword(password)

    fun isValidUsername(username: String) = validator.isValidUsername(username)

    private fun authenticate(block: suspend () -> Result<AuthSession>) {
        viewModelScope.launch {
            _isLoading.value = true
            _authResult.value = block()
            _isLoading.value = false
        }
    }

    private fun startTimer(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (left in seconds downTo 0) {
                _timeLeft.value = left
                delay(1_000L)
            }
        }
    }
}
