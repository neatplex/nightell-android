package com.neatplex.nightell.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.utils.IValidation
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val postUseCase: PostUseCase,
    private val validation: IValidation
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


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    fun fetchProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = userUseCase.profile()
            _profileData.value = result
            _isLoading.value = false
        }
    }

    fun updateProfileName(name: String){
        viewModelScope.launch {
            _isLoading.value = true
            val result = userUseCase.changeProfileName(name)
            _userUpdatedData.value = result
            _isLoading.value = false
        }
    }

    fun refreshProfile(userId : Int) {
        canLoadMore = true
        _posts.value = emptyList() }

    fun updateBioOfUser(bio: String){
        viewModelScope.launch {
            _isLoading.value = true
            val result = userUseCase.changeProfileBio(bio)
            _userUpdatedData.value = result
            _isLoading.value = false
        }
    }

    fun updateUsernameOfUser(username: String){
        viewModelScope.launch {
            _isLoading.value = true
            val result = userUseCase.changeProfileUsername(username)
            _userUpdatedData.value = result
            _isLoading.value = false
        }
    }

    fun loadPosts(userId : Int, lastPostId: Int?){
        if (!canLoadMore) return

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
            _isLoading.value = true
            _accountDeleteResult.value = userUseCase.deleteAccount()
            _isLoading.value = false
        }
    }

    fun isValidUsername(username: String): Boolean {
        return validation.isValidUsername(username)
    }
}