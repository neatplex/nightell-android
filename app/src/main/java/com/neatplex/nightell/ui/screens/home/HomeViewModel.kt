package com.neatplex.nightell.ui.screens.home

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
    private var lastPostId: Int? = null


    private val _profileData = MutableLiveData<Result<Profile?>>()
    val profileData: LiveData<Result<Profile?>>
        get() = _profileData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    fun loadFeed() {
        if (!canLoadMore) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = postUseCase.loadFeed(lastPostId)
            if (result is Result.Success) {
                val posts = result.data ?: emptyList()
                if (lastPostId == null) {
                    _feed.value = posts
                } else {
                    _feed.value = _feed.value.orEmpty() + posts
                }
                // Update lastPostId if there are posts
                if (posts.isNotEmpty()) {
                    lastPostId = posts.last().id
                }
                if (posts.size < 10) {
                    canLoadMore = false
                }
            } else {
                _feed.value = emptyList() // In case of error, clear the feed
            }
            _isLoading.value = false
            _isRefreshing.value = false
        }
    }

    fun refreshFeed() {
        canLoadMore = true
        lastPostId = null
        _feed.value = emptyList()
        _isRefreshing.value = true
        loadFeed() // Load the feed from the start
    }

    fun fetchProfile() {
        viewModelScope.launch {
            val result = userUseCase.profile()
            _profileData.value = result
        }
    }
}
