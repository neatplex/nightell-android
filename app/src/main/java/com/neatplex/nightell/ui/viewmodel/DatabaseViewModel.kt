package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.PostEntity
import com.neatplex.nightell.domain.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DatabaseViewModel @Inject constructor(private val repository: DatabaseRepository) : ViewModel() {

    fun savePost(post: PostEntity) {
        viewModelScope.launch {
            repository.insertPost(post)
        }
    }

    fun unsavePost(post: PostEntity) {
        viewModelScope.launch {
            repository.deletePost(post)
        }
    }

    fun getPostById(id: Int, onResult: (PostEntity?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getPostById(id))
        }
    }

    fun getAllPosts(callback: (List<PostEntity>) -> Unit) {
        viewModelScope.launch {
            val posts = repository.getAllPosts()
            callback(posts)
        }
    }
}