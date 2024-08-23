package com.neatplex.nightell.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.UserResponse
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.usecase.FollowUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val followUseCase: FollowUseCase,
    private val userUseCase: UserUseCase,
    private val postUseCase: PostUseCase
) : ViewModel() {

    private val _usersList = MutableLiveData<List<User>>(emptyList())
    val usersList: LiveData<List<User>>
        get() = _usersList

    var canLoadMoreFollowers = true // Default to true for initial load
    var canLoadMoreFollowings = true // Default to true for initial load


    private val _followResult = MutableLiveData<Result<Unit>>()
    val followResult: LiveData<Result<Unit>> get() = _followResult

    private val _unfollowResult = MutableLiveData<Result<Unit>>()
    val unfollowResult: LiveData<Result<Unit>> get() = _unfollowResult

    private val _postList = MutableLiveData<List<Post>>(emptyList())
    val postList: LiveData<List<Post>> get() = _postList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showUserInfoResult = MutableLiveData<Result<UserResponse>>()
    val showUserInfoResult : LiveData<Result<UserResponse>> get() = _showUserInfoResult

    var canLoadMore = true // Default to true for initial load

    fun getUserInfo(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _showUserInfoResult.value = userUseCase.getUserProfile(userId)
            _isLoading.value = false
        }
    }

    fun fetchUserFollowers(userId: Int, lastId: Int?, count: Int?) {
        if (!canLoadMoreFollowers) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = followUseCase.followers(userId, lastId, count)
            if (result is Result.Success) {
                val users = result.data ?: emptyList()
                if (users.size < 10) {
                    canLoadMoreFollowers = false
                }
                _usersList.value = _usersList.value.orEmpty() + users
            }  else {
                _usersList.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun fetchUserFollowings(userId: Int, lastId: Int?, count: Int?) {
        if (!canLoadMoreFollowings) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = followUseCase.followings(userId, lastId, count)
            if (result is Result.Success) {
                val users = result.data ?: emptyList()
                if (users.size < 10) {
                    canLoadMoreFollowings = false
                }
                _usersList.value = _usersList.value.orEmpty() + users
            }  else {
                _usersList.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun followUser(userId: Int){
        viewModelScope.launch {
            _isLoading.value = true
            _followResult.value = followUseCase.follow(userId)
            _isLoading.value = false
        }
    }

    fun unfollowUser(userId: Int){
        viewModelScope.launch {
            _isLoading.value = true
            _unfollowResult.value = followUseCase.unfollow(userId)
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