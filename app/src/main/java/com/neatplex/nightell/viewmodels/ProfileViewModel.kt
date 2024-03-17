package com.neatplex.nightell.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.dto.ShowProfileResponse
import com.neatplex.nightell.repository.ProfileRepository
import com.neatplex.nightell.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel() {

    private val _showUserInfoResult = MutableLiveData<Result<ShowProfileResponse?>>()
    val showUserInfoResult : LiveData<Result<ShowProfileResponse?>> get() = _showUserInfoResult

    private val _followResult = MutableLiveData<Result<Any?>>()
    val followResult: LiveData<Result<Any?>> get() = _followResult

    fun getUserInfo(userId: Int) {
        viewModelScope.launch {
            _showUserInfoResult.value = Result.Loading
            val result = profileRepository.showUserProfile(userId)
            _showUserInfoResult.value = result
        }
    }

    fun followUser(userId: Int, friendId: Int){
        viewModelScope.launch {
            val result = profileRepository.follow(userId, friendId)
            _followResult.value = result
        }
    }

    fun unfollowUser(userId: Int, friendId: Int){
        viewModelScope.launch {
            val result = profileRepository.unfollow(userId, friendId)
            _followResult.value = result
        }
    }
}