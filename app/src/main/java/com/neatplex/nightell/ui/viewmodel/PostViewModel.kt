package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.PostStoreResponse
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.repository.PostRepository
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {

    private val _postDeleteResult = MutableLiveData<Result<Any?>>()
    val postDeleteResult: LiveData<Result<Any?>> get() = _postDeleteResult

    private var _storePostResult = MutableLiveData<Result<PostStoreResponse?>>()
    val storePostResult: LiveData<Result<PostStoreResponse?>> get() = _storePostResult

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _userPosts = MutableLiveData<List<Post>>()
    val userPosts: LiveData<List<Post>> get() = _userPosts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private var lastPostId: Int? = null
    private var lastUserPostId: Int? = null
    private var allPosts = emptyList<Post>()
    private var allUserPosts = emptyList<Post>()

    fun loadFeed() {
        viewModelScope.launch {
            val result = postRepository.showFeed(lastPostId)

            if (result is Result.Success) {
                val newFeed = result.data?.posts ?: emptyList()
                if (newFeed.isNotEmpty()) {
                    lastPostId = newFeed.last().id
                    allPosts = (_posts.value ?: emptyList()) + newFeed
                }
                _posts.value = allPosts

            }else if (result is Result.Error) {
                _posts.value = emptyList() // Clear previous posts
            }


        }
    }

    fun loadUserPosts(userId: Int) {
        viewModelScope.launch {

            val result = postRepository.showUserPosts(userId, lastUserPostId)

            if (result is Result.Success) {
                val newFeed = result.data?.posts ?: emptyList()
                if (newFeed.isNotEmpty()) {
                    lastUserPostId = newFeed.last().id
                    allUserPosts = (_userPosts.value ?: emptyList()) + newFeed
                }
                _userPosts.value = allUserPosts

            }

        }
    }

    fun uploadPost(title: String, description: String?, audioId: Int, imageId: Int?) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = postRepository.uploadPost(title, description, audioId, imageId)
            _storePostResult.value = result
            _isLoading.value = false
        }
    }

    fun editPost(postId: Int, newTitle: String, newDescription: String) {
        viewModelScope.launch {
            val result = postRepository.editPost(newTitle, newDescription, postId)
            if (result is Result.Success) {
                _storePostResult.value = result
                loadFeed()
            } else {
                _storePostResult.value = result
            }
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            val result = postRepository.deletePost(postId)
            if (result is Result.Success) {
                _postDeleteResult.value = result
                loadFeed()
            }
            else {
                _postDeleteResult.value = result
            }
        }
    }

}