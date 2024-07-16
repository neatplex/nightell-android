package com.neatplex.nightell.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.domain.usecase.AuthUseCase
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.Validation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val validation: Validation
) : ViewModel() {

    private val _authResult = MutableLiveData<Result<AuthResponse>>()
    val authResult: LiveData<Result<AuthResponse>> get() = _authResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authUseCase.register(username, email, password)) {
                is Result.Success -> {
                    result.data?.let {
                        _authResult.value = Result.Success(it)
                    }
                }
                is Result.Failure -> {
                    _authResult.value = result
                }
            }
            _isLoading.value = false
        }
    }

    fun loginUser(emailOrUsername: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authUseCase.login(emailOrUsername, password)) {
                is Result.Success -> {
                    result.data?.let {
                        _authResult.value = Result.Success(it)
                    }
                }
                is Result.Failure -> {
                    _authResult.value = result
                }
            }
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = authUseCase.signInWithGoogle(idToken)) {
                    is Result.Success -> {
                        result.data?.let {
                            _authResult.value = Result.Success(it)
                        }
                    }
                    is Result.Failure -> {
                        _authResult.value = result
                    }
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _authResult.value = Result.Failure(e.localizedMessage ?: "An error occurred", e)
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