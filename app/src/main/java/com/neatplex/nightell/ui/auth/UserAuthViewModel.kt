package com.neatplex.nightell.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.repository.UserAuthRepository
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAuthViewModel @Inject constructor(private val userAuthRepository: UserAuthRepository, private val tokenManager: TokenManager) : ViewModel() {

    private val _authResult = MutableLiveData<Result<AuthResponse?>>()
    val authResult: LiveData<Result<AuthResponse?>> get() = _authResult

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            setLoadingState()
            val result = userAuthRepository.register(username, email, password)
            handleLoginResult(result)
        }
    }

    fun loginUser(emailOrUsername: String, password: String) {
        viewModelScope.launch {
            setLoadingState()
            val result = userAuthRepository.login(emailOrUsername, password)
            handleLoginResult(result)
        }
    }

    private fun setLoadingState() {
        _authResult.value = Result.Loading
    }

    private fun handleLoginResult(result: Result<AuthResponse?>) {
        if (result is Result.Success) {
            result.data?.let {
                saveInfo(it.token, it.user.id, it.user.email)
            }
            _authResult.value = result // Set the value to trigger UI update
        }
        else{
            _authResult.value = result // Set the value to trigger UI update
        }
    }

    private fun saveInfo(token: String, id: Int, email: String) {
        tokenManager.setToken(token)
        tokenManager.setId(id)
        tokenManager.setEmail(email)
    }

}