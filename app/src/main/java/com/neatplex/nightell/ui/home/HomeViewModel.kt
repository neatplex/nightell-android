package com.neatplex.nightell.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postUseCase: PostUseCase, private val userUseCase: UserUseCase
) : ViewModel() {

    private val _feed = MutableLiveData<List<Post>>(emptyList()) // Initialize with an empty list
    val feed: LiveData<List<Post>> get() = _feed
    var canLoadMore = true // Default to true for initial load

    private val _profileData = MutableLiveData<Result<Profile?>>()
    val profileData: LiveData<Result<Profile?>>
        get() = _profileData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    fun loadFeed(lastPostId: Int?) {
        if (!canLoadMore) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = postUseCase.loadFeed(lastPostId)
            if (result is Result.Success) {
                val posts = result.data ?: emptyList()
                if (posts.size < 10) {
                    canLoadMore = false
                }
                _feed.value = _feed.value.orEmpty() + posts
            } else {
                _feed.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun refreshFeed() {
        canLoadMore = true // Allow loading more on refresh
        _feed.value = emptyList()
        loadFeed(null)
    }


        fun fetchProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = userUseCase.profile()
            _profileData.value = result
            _isLoading.value = false
        }
    }

}