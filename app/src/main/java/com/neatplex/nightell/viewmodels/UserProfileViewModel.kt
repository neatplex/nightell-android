package com.neatplex.nightell.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.dto.ShowProfileResponse
import com.neatplex.nightell.dto.UserUpdated
import com.neatplex.nightell.dto.Users
import com.neatplex.nightell.repository.UserProfileRepository
import com.neatplex.nightell.util.Result
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
            when (result) {
                is Result.Success -> {
                    _profileData.value = result
                }
                is Result.Error -> {
                    if(result.code in 400..499){
                        // TODO
                    }
                    _profileData.value = result
                }
                is Result.Loading -> {
                }

                else -> {}
            }
        }
    }

    fun changeProfileName(name: String){
        viewModelScope.launch {
            val result = userProfileRepository.changeProfileName(name)
            when (result) {
                is Result.Success -> {
                    _userUpdatedData.value = result
                }
                is Result.Error -> {
                    if(result.code in 400..499){
                        // TODO
                    }
                    _userUpdatedData.value = result
                }
                is Result.Loading -> {
                }

                else -> {}
            }
        }
    }

    fun updateBioOfUser(bio: String){
        viewModelScope.launch {
            val result = userProfileRepository.changeProfileBio(bio)
            when (result) {
                is Result.Success -> {
                    _userUpdatedData.value = result
                }
                is Result.Error -> {
                    if(result.code in 400..499){
                        // TODO
                    }
                    _userUpdatedData.value = result
                }
                is Result.Loading -> {
                }

                else -> {}
            }
        }
    }

    fun updateUsernameOfUser(username: String){
        viewModelScope.launch {
            val result = userProfileRepository.changeProfileUsername(username)
            when (result) {
                is Result.Success -> {
                    _userUpdatedData.value = result
                }
                is Result.Error -> {
                    if(result.code in 400..499){
                        // TODO
                    }
                    _userUpdatedData.value = result
                }
                is Result.Loading -> {
                }

                else -> {}
            }
        }
    }

    fun fetchUserFollowers(userId: Int) {
        viewModelScope.launch {
            val result = userProfileRepository.followers(userId)
            when (result) {
                is Result.Success -> {
                    _userData.value = result
                }
                is Result.Error -> {
                    if(result.code in 400..499){
                        // TODO
                    }
                    _userData.value = result
                }
                is Result.Loading -> {
                }

                else -> {}
            }
        }
    }

    fun fetchUserFollowings(userId: Int) {
        viewModelScope.launch {
            val result = userProfileRepository.followings(userId)
            when (result) {
                is Result.Success -> {
                    _userData.value = result
                }
                is Result.Error -> {
                    if(result.code in 400..499){
                        // TODO
                    }
                    _userData.value = result
                }
                is Result.Loading -> {
                }

                else -> {}
            }
        }
    }
}