package com.neatplex.nightell.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User

class SharedViewModel : ViewModel() {

    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post> = _post

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user


    fun setPost(post: Post) {
        _post.value = post
    }

    fun setUser(user: User) {
        _user.value = user
    }

}