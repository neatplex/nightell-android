package com.neatplex.nightell.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.usecase.FollowUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.ProfileUseCase
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val followUseCase: FollowUseCase,
    private val profileUseCase: ProfileUseCase,
    private val postUseCase: PostUseCase
) : ViewModel() {

    private val _usersList = MutableLiveData<Result<Users>>()
    val usersList: LiveData<Result<Users>>
        get() = _usersList

    private val _followResult = MutableLiveData<Result<Unit>>()
    val followResult: LiveData<Result<Unit>> get() = _followResult

    private val _unfollowResult = MutableLiveData<Result<Unit>>()
    val unfollowResult: LiveData<Result<Unit>> get() = _unfollowResult

    private val _postList = MutableLiveData<List<Post>>()
    val postList: LiveData<List<Post>> get() = _postList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showUserInfoResult = MutableLiveData<Result<Profile>>()
    val showUserInfoResult : LiveData<Result<Profile>> get() = _showUserInfoResult

    var canLoadMore = true // Default to true for initial load

    fun getUserInfo(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _showUserInfoResult.value = profileUseCase.getUserProfile(userId)
            _isLoading.value = false
        }
    }

    fun fetchUserFollowers(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _usersList.value = followUseCase.followers(userId)
            _isLoading.value = false
        }
    }

    fun fetchUserFollowings(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _usersList.value = followUseCase.followings(userId)
            _isLoading.value = false
        }
    }

    fun followUser(userId: Int, friendId: Int){
        viewModelScope.launch {
            _isLoading.value = true
            _followResult.value = followUseCase.follow(userId, friendId)
            _isLoading.value = false
        }
    }

    fun unfollowUser(userId: Int, friendId: Int){
        viewModelScope.launch {
            _isLoading.value = true
            _unfollowResult.value = followUseCase.unfollow(userId, friendId)
            _isLoading.value = false
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
                _postList.value = _postList.value.orEmpty() + posts
            } else {
                _postList.value = emptyList()
            }
            _isLoading.value = false
        }
    }
}