package com.neatplex.nightell.ui.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val postUseCase: PostUseCase,
    private val userUseCase: UserUseCase
            ) :
    ViewModel() {

    private val _posts = MutableLiveData<List<Post>?>()
    val posts: LiveData<List<Post>?> get() = _posts

    private val _users = MutableLiveData<List<User>?>()
    val users: LiveData<List<User>?> get() = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    var canLoadMorePost = true
    var canLoadMoreUser = true

    fun searchPost(query: String, lastPostId: Int?, isSame: Boolean) {
        if(isSame && !canLoadMorePost) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = postUseCase.search(query, lastPostId)
            if (result is Result.Success) {
                val posts = result.data ?: emptyList()
                if (posts.size < 10) {
                    canLoadMorePost = false
                }
                if(!isSame){
                    canLoadMorePost = true
                    _posts.value = emptyList()
                }
                _posts.value = _posts.value.orEmpty() + posts
            } else {
                _posts.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun searchUser(query: String, lastUserId: Int?, isSame: Boolean) {
        if(isSame && !canLoadMoreUser) return
        viewModelScope.launch {
            _isLoading.value = true
            val result = userUseCase.searchUser(query, lastUserId)
            if (result is Result.Success) {
                val users = result.data ?: emptyList()
                if (users.size < 10) {
                    canLoadMoreUser = false
                }
                if(!isSame){
                    canLoadMorePost = true
                    _users.value = emptyList()
                }
                _users.value = _users.value.orEmpty() + users
            } else {
                _users.value = emptyList()
            }
            _isLoading.value = false
        }
    }
}
