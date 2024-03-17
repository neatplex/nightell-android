package com.neatplex.nightell.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.dto.PostStoreResponse
import com.neatplex.nightell.model.Post
import com.neatplex.nightell.repository.PostRepository
import com.neatplex.nightell.util.Result
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private var lastId: Int? = null

    fun loadMorePosts() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = postRepository.showFeed(lastId)

            if (result is Result.Success) {
                val newFeed = result.data!!.posts
                if(newFeed.isNotEmpty()){
                    val allPosts = (_posts.value ?: emptyList()) + newFeed
                    _posts.value = allPosts
                    lastId = newFeed.lastOrNull()?.id
                }
            } else {
                _posts.value = result as List<Post>
            }

            _isLoading.value = false
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
                loadMorePosts()
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
                loadMorePosts()
            }
            else {
                _postDeleteResult.value = result
            }
        }
    }


    fun loadUserPosts(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = postRepository.showUserPosts(userId, lastId)

            if (result is Result.Success) {
                val newPosts = result.data?.posts ?: emptyList()
                if (newPosts.isNotEmpty()) {
                    val allPosts = (_posts.value ?: emptyList()) + newPosts
                    _posts.value = allPosts
                    lastId = newPosts.lastOrNull()?.id
                }
            } else {
                _posts.value = result as List<Post>
            }

            _isLoading.value = false
        }
    }

}