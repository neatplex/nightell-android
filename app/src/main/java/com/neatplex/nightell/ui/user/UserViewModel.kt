package com.neatplex.nightell.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.utils.Result
import com.neatplex.nightell.domain.usecase.PostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val postUseCase: PostUseCase) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>?>()
    val posts: LiveData<List<Post>?> get() = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadPosts(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = postUseCase.loadUserPosts(userId)
            if (result is Result.Success) {
                _posts.value = result.data
            } else {
                _posts.value = emptyList()
            }
            _isLoading.value = false
        }
    }
}