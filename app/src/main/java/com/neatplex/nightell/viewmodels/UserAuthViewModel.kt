package com.neatplex.nightell.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.dto.AuthResponse
import com.neatplex.nightell.repository.UserAuthRepository
import com.neatplex.nightell.util.Result
import com.neatplex.nightell.util.TokenManager
import com.neatplex.nightell.util.Validation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAuthViewModel @Inject constructor(private val userAuthRepository: UserAuthRepository, private val tokenManager: TokenManager) : ViewModel() {

    private val _authResult = MutableLiveData<Result<AuthResponse?>>()
    val authResult: LiveData<Result<AuthResponse?>> get() = _authResult

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = Result.Loading

            val result = userAuthRepository.register(username, email, password)

            handleLoginResult(result)
        }
    }

    fun loginUser(emailOrUsername: String, password: String) {
        viewModelScope.launch {
            _authResult.value = Result.Loading

            // Check whether the input is a valid email or a username
            val isEmail = Validation.isValidEmail(emailOrUsername)
            val isUsername = Validation.isValidUsername(emailOrUsername)

            if (isEmail) {
                // If it's a valid email, call the loginWithEmail API
                val result = userAuthRepository.loginWithEmail(emailOrUsername, password)
                handleLoginResult(result)

            } else if (isUsername) {
                // If it's a valid username, call the loginWithUsername API
                val result = userAuthRepository.loginWithUsername(emailOrUsername, password)
                handleLoginResult(result)

            } else {
                // If it's neither a valid email nor a valid username, handle accordingly
                _authResult.value = Result.Error("Invalid email or username", 400)
            }
        }
    }

    private fun handleLoginResult(result: Result<AuthResponse?>) {
        when (result) {
            is Result.Success -> {
                result.data?.let {
                    saveInfo(it.token, it.user.id, it.user.email)
                    _authResult.value = result // Set the value to trigger UI update
                }
            }
            is Result.Error -> {
                    _authResult.value = result // Set the value to trigger UI update
            }
            else -> {
                // Handle loading or other states
                _authResult.value = result // Set the value to trigger UI update
            }
        }
    }

    private fun saveInfo(token: String, id: Int, email: String) {
        tokenManager.setToken(token)
        tokenManager.setId(id)
        tokenManager.setEmail(email)
    }

}