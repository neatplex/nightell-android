package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post> = _post

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    val tokenState: StateFlow<String?> = tokenManager.tokenState

    fun setPost(post: Post) {
        _post.value = post
    }

    fun setUser(user: User) {
        _user.value = user
    }

    fun deleteToken() {
        tokenManager.deleteToken()
    }
}