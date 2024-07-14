package com.neatplex.nightell.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.ProfileUseCase
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.utils.Validation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val postUseCase: PostUseCase,
    private val validation: Validation
) : ViewModel() {

    private val _profileData = MutableLiveData<Result<Profile>>()
    val profileData: LiveData<Result<Profile>> get() = _profileData

    private val _accountDeleteResult = MutableLiveData<Result<Unit>>()
    val accountDeleteResult: LiveData<Result<Unit>> get() = _accountDeleteResult

    private val _userUpdatedData = MutableLiveData<Result<UserUpdated>>()
    val userUpdatedData: LiveData<Result<UserUpdated>>
        get() = _userUpdatedData

    private val _posts = MutableLiveData<List<Post>?>()
    val posts: LiveData<List<Post>?> get() = _posts

    var canLoadMore = true // Default to true for initial load

    private val _usersList = MutableLiveData<Result<Users?>>()
    val usersList: LiveData<Result<Users?>>
        get() = _usersList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    fun fetchProfile() {
        viewModelScope.launch {
            _profileData.value = Result.Loading
            val result = profileUseCase.profile()
            _profileData.value = result
        }
    }

    fun updateProfileName(name: String){
        viewModelScope.launch {
            val result = profileUseCase.changeProfileName(name)
            _userUpdatedData.value = result
        }
    }

    fun refreshProfile(userId : Int) {
        canLoadMore = true // Allow loading more on refresh
        _posts.value = emptyList()
        loadPosts(userId , null)
    }

    fun updateBioOfUser(bio: String){
        viewModelScope.launch {
            val result = profileUseCase.changeProfileBio(bio)
            _userUpdatedData.value = result
        }
    }

    fun updateUsernameOfUser(username: String){
        viewModelScope.launch {
            val result = profileUseCase.changeProfileUsername(username)
            _userUpdatedData.value = result
        }
    }

    fun loadPosts(userId : Int, lastPostId: Int?){
        if (!canLoadMore || _isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = postUseCase.loadUserPosts(userId, lastPostId)
            if (result is Result.Success) {
                val posts = result.data ?: emptyList()
                if (posts.size < 10) {
                    canLoadMore = false
                }
                _posts.value = _posts.value.orEmpty() + posts
            } else {
                _posts.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _accountDeleteResult.value = Result.Loading
            _accountDeleteResult.value = profileUseCase.deleteAccount()
        }
    }

    fun isValidUsername(username: String): Boolean {
        return validation.isValidUsername(username)
    }
}