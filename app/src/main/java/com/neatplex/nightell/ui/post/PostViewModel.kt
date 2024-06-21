package com.neatplex.nightell.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.Likes
import com.neatplex.nightell.data.dto.PostStoreResponse
import com.neatplex.nightell.data.dto.StoreLike
import com.neatplex.nightell.domain.repository.LikeRepository
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postUseCase: PostUseCase, private val likeRepository: LikeRepository
) : ViewModel() {

    private val _postDeleteResult = MutableLiveData<Result<Unit>>()
    val postDeleteResult: LiveData<Result<Unit>> get() = _postDeleteResult
    private var _storePostResult = MutableLiveData<Result<PostStoreResponse?>>()
    val storePostResult: LiveData<Result<PostStoreResponse?>> get() = _storePostResult

    private val _likeResult = MutableLiveData<Result<StoreLike?>>()
    val likeResult: LiveData<Result<StoreLike?>> get() = _likeResult

    private val _unlikeResult = MutableLiveData<Result<Unit>>()
    val unlikeResult: LiveData<Result<Unit>> get() = _unlikeResult

    private val _showLikesResult = MutableLiveData<Result<Likes?>>()
    val showLikesResult : LiveData<Result<Likes?>> get() = _showLikesResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun editPost(postId: Int, newTitle: String, newDescription: String) {
        viewModelScope.launch {
            _storePostResult.value = Result.Loading
            _storePostResult.value = postUseCase.editPost(postId, newTitle, newDescription)
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            _postDeleteResult.value = Result.Loading
            _postDeleteResult.value = postUseCase.deletePost(postId)
        }
    }

    fun like(postId: Int){
        viewModelScope.launch {
            val result = likeRepository.like(postId)
            _likeResult.value = result
        }
    }

    fun showLikes(postId: Int){
        viewModelScope.launch {
            val result = likeRepository.showLikes(postId)
            _showLikesResult.value = result
        }
    }

    fun deleteLike(likeId : Int){
        viewModelScope.launch {
            val result = likeRepository.deleteLike(likeId)
            _unlikeResult.value = result
        }
    }

}