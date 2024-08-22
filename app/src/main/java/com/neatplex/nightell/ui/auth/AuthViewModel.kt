package com.neatplex.nightell.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.domain.usecase.AuthUseCase
import com.neatplex.nightell.utils.IValidation
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val validation: IValidation
) : ViewModel() {

    private val _authResult = MutableLiveData<Result<AuthResponse>>()
    val authResult: LiveData<Result<AuthResponse>> get() = _authResult

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
