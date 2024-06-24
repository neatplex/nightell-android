package com.neatplex.nightell.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.ShowProfileResponse
import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.repository.FollowRepository
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.ProfileUseCase
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val followRepository: FollowRepository, private val profileUseCase: ProfileUseCase, private val postUseCase: PostUseCase
) : ViewModel() {

    private val _usersList = MutableLiveData<Result<Users>>()
    val usersList: LiveData<Result<Users>>
        get() = _usersList

    private val _followResult = MutableLiveData<Result<Unit>>()
    val followResult: LiveData<Result<Unit>> get() = _followResult

    private val _unfollowResult = MutableLiveData<Result<Unit>>()
    val unfollowResult: LiveData<Result<Unit>> get() = _unfollowResult

    private val _posts = MutableLiveData<List<Post>?>()
    val posts: LiveData<List<Post>?> get() = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showUserInfoResult = MutableLiveData<Result<ShowProfileResponse?>>()
    val showUserInfoResult : LiveData<Result<ShowProfileResponse?>> get() = _showUserInfoResult

    fun getUserInfo(userId: Int) {
        viewModelScope.launch {
            _showUserInfoResult.value = Result.Loading
            val result = profileUseCase.showUserProfile(userId)
            _showUserInfoResult.value = result
        }
    }

    fun fetchUserFollowers(userId: Int) {
        viewModelScope.launch {
            val result = followRepository.followers(userId)
            _usersList.value = result
        }
    }

    fun fetchUserFollowings(userId: Int) {
        viewModelScope.launch {
            val result = followRepository.followings(userId)
            _usersList.value = result
        }
    }

    fun followUser(userId: Int, friendId: Int){
        viewModelScope.launch {
            val result = followRepository.follow(userId, friendId)
            _followResult.value = result
        }
    }

    fun unfollowUser(userId: Int, friendId: Int){
        viewModelScope.launch {
            val result = followRepository.unfollow(userId, friendId)
            _unfollowResult.value = result
        }
    }

    fun loadUserPosts(userId : Int){
        viewModelScope.launch {
            _isLoading.value = true
            val result = postUseCase.loadUserPosts(userId,null)
            if (result is Result.Success) {
                _posts.value = result.data
            } else {
                _posts.value = emptyList()
            }
            _isLoading.value = false
        }
    }

}