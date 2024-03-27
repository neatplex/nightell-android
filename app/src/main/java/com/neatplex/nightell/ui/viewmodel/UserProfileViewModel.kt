package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.ShowProfileResponse
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.repository.UserProfileRepository
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(private val userProfileRepository: UserProfileRepository) : ViewModel() {

    private val _profileData = MutableLiveData<Result<ShowProfileResponse?>>()
    val profileData: LiveData<Result<ShowProfileResponse?>>
        get() = _profileData

    private val _userData = MutableLiveData<Result<Users?>>()
    val userData: LiveData<Result<Users?>>
        get() = _userData

    private val _userUpdatedData = MutableLiveData<Result<UserUpdated?>>()
    val userUpdatedData: LiveData<Result<UserUpdated?>>
        get() = _userUpdatedData


    fun fetchProfile() {
        viewModelScope.launch {
            _profileData.value = Result.Loading
            val result = userProfileRepository.profile()
            _profileData.value = result
        }
    }

    fun changeProfileName(name: String){
        viewModelScope.launch {
            val result = userProfileRepository.changeProfileName(name)
            _userUpdatedData.value = result
        }
    }

    fun updateBioOfUser(bio: String){
        viewModelScope.launch {
            val result = userProfileRepository.changeProfileBio(bio)
            _userUpdatedData.value = result
        }
    }

    fun updateUsernameOfUser(username: String){
        viewModelScope.launch {
            val result = userProfileRepository.changeProfileUsername(username)
            _userUpdatedData.value = result
        }
    }

    fun fetchUserFollowers(userId: Int) {
        viewModelScope.launch {
            val result = userProfileRepository.followers(userId)
            _userData.value = result
        }
    }

    fun fetchUserFollowings(userId: Int) {
        viewModelScope.launch {
            val result = userProfileRepository.followings(userId)
            _userData.value = result
        }
    }
}