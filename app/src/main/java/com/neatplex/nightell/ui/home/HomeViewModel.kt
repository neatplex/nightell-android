package com.neatplex.nightell.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.ShowProfileResponse
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.ProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postUseCase: PostUseCase, private val profileUseCase: ProfileUseCase
) : ViewModel() {

    private val _feed = MutableLiveData<List<Post>?>()
    val feed: LiveData<List<Post>?> get() = _feed

    private val _profileData = MutableLiveData<Result<ShowProfileResponse?>>()
    val profileData: LiveData<Result<ShowProfileResponse?>>
        get() = _profileData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    fun loadFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = postUseCase.loadFeed()
            if (result is Result.Success) {
                _feed.value = result.data
            } else {
                _feed.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun refreshFeed() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val result = postUseCase.refreshFeed()
            if (result is Result.Success) {
                _feed.value = result.data
            } else {
                _feed.value = emptyList()
            }
            _isRefreshing.value = false
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _profileData.value = Result.Loading
            val result = profileUseCase.profile()
            _profileData.value = result
        }
    }

}