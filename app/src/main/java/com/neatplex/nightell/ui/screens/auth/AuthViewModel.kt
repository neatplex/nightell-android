package com.neatplex.nightell.ui.screens.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.OtpResponseTtl
import com.neatplex.nightell.domain.usecase.AuthUseCase
import com.neatplex.nightell.utils.IValidation
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val validation: IValidation
) : ViewModel() {

    private val _authResult = MutableLiveData<Result<AuthResponse>>()
    val authResult: LiveData<Result<AuthResponse>> get() = _authResult

    private val _getOtpResult = MutableLiveData<Result<OtpResponseTtl>>()
    val getOtpResult: LiveData<Result<OtpResponseTtl>> get() = _getOtpResult

    private val _timeLeft = MutableLiveData<Int>()
    val timeLeft: LiveData<Int> get() = _timeLeft

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authResult.value = authUseCase.register(username, email, password)
            _isLoading.value = false
        }
    }

    fun loginUser(emailOrUsername: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authResult.value = authUseCase.login(emailOrUsername, password)
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authUseCase.signInWithGoogle(idToken)
                _authResult.value = result
            } catch (e: Exception) {
                _authResult.value = Result.Failure(e.localizedMessage ?: "An error occurred", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendOtp(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authUseCase.sendOtp(email)
            _getOtpResult.value = result
            _isLoading.value = false

            if (result is Result.Success) {
                startTimer(result.data!!.ttl)
            }
        }
    }

    private fun startTimer(ttl: Int) {
        _timeLeft.value = ttl
        viewModelScope.launch {
            for (i in ttl downTo 0) {
                _timeLeft.value = i
                delay(1000L)
            }
        }
    }

    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authUseCase.verifyOtp(email, otp)
            _authResult.value = result
            _isLoading.value = false
        }
    }

    fun isValidEmail(email: String): Boolean {
        return validation.isValidEmail(email)
    }

    fun isValidPassword(password: String): Boolean {
        return validation.isValidPassword(password)
    }

    fun isValidUsername(username: String): Boolean {
        return validation.isValidUsername(username)
    }
}
