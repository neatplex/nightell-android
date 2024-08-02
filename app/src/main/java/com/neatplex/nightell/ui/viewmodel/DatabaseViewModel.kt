package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.PostEntity
import com.neatplex.nightell.domain.repository.IDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DatabaseViewModel @Inject constructor(private val repository: IDatabaseRepository) : ViewModel() {

    private val _savedPosts = MutableStateFlow<List<PostEntity>>(emptyList())
    val savedPosts: StateFlow<List<PostEntity>> get() = _savedPosts

    init {
        viewModelScope.launch {
            repository.getAllPosts().collect { posts ->
                _savedPosts.value = posts
            }
        }
    }

    fun savePost(post: PostEntity) {
        viewModelScope.launch {
            repository.insertPost(post)
        }
    }

    fun unsavePost(post: PostEntity) {
        viewModelScope.launch {
            repository.deletePost(post)
            _savedPosts.value = repository.getAllPosts().first()
        }
    }

    fun getPostById(id: Int, onResult: (PostEntity?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getPostById(id))
        }
    }
}