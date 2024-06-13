package com.neatplex.nightell.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.domain.usecase.UserAuthUseCase
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAuthViewModel @Inject constructor(
    private val authUseCase: UserAuthUseCase
) : ViewModel() {

    private val _authResult = MutableLiveData<Result<AuthResponse?>>()
    val authResult: LiveData<Result<AuthResponse?>> get() = _authResult

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = Result.Loading
            _authResult.value = authUseCase.register(username, email, password)
        }
    }

    fun loginUser(emailOrUsername: String, password: String) {
        viewModelScope.launch {
            _authResult.value = Result.Loading
            _authResult.value = authUseCase.login(emailOrUsername, password)
        }
    }
}