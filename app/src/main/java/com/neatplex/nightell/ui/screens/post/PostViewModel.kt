package com.neatplex.nightell.ui.screens.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.CommentDetailResponse
import com.neatplex.nightell.data.dto.Comments
import com.neatplex.nightell.data.dto.Likes
import com.neatplex.nightell.data.dto.PostDetailResponse
import com.neatplex.nightell.data.dto.StoreLike
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.domain.usecase.CommentUseCase
import com.neatplex.nightell.domain.usecase.LikeUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postUseCase: PostUseCase,
    private val likeUseCase: LikeUseCase,
    private val commentUseCase: CommentUseCase
) : ViewModel() {

    private val _postDeleteResult = MutableLiveData<Result<Unit>>()
    val postDeleteResult: LiveData<Result<Unit>> get() = _postDeleteResult

    private val _deleteCommentResult = MutableLiveData<Result<Int>>()
    val deleteCommentResult: LiveData<Result<Int>> get() = _deleteCommentResult

    private val _getCommentsResult = MutableLiveData<List<Comment>>()
    val getCommentsResult: LiveData<List<Comment>> get() = _getCommentsResult

    private val _sendCommentResult = MutableLiveData<Result<CommentDetailResponse>>()
    val sendCommentResult: LiveData<Result<CommentDetailResponse>> get() = _sendCommentResult

    private val _postDetailResult = MutableLiveData<Result<PostDetailResponse>>()
    val postDetailResult: LiveData<Result<PostDetailResponse>> get() = _postDetailResult

    private val _postUpdateResult = MutableLiveData<Result<PostDetailResponse>>()
    val postUpdateResult: LiveData<Result<PostDetailResponse>> get() = _postUpdateResult

    private val _likeResult = MutableLiveData<Result<StoreLike>>()
    val likeResult: LiveData<Result<StoreLike>> get() = _likeResult

    private val _unlikeResult = MutableLiveData<Result<Unit>>()
    val unlikeResult: LiveData<Result<Unit>> get() = _unlikeResult

    private val _showLikesResult = MutableLiveData<Result<Likes>>()
    val showLikesResult: LiveData<Result<Likes>> get() = _showLikesResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isCommentLoading = MutableLiveData<Boolean>()
    val isCommentLoading: LiveData<Boolean> get() = _isCommentLoading

    private val _isFetching = MutableLiveData<Boolean>()
    val isFetching: LiveData<Boolean> get() = _isFetching

    private var lastCommentId: Int? = null
    var canLoadMore = true

    fun updatePost(postId: Int, newTitle: String, newDescription: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _postUpdateResult.value = postUseCase.editPost(postId, newTitle, newDescription)
            _isLoading.value = false
        }
    }

    fun getPostDetail(postId: Int) {
        viewModelScope.launch {
            _isFetching.value = true
            _postDetailResult.value = postUseCase.getPostDetail(postId)
            _isFetching.value = false
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _postDeleteResult.value = postUseCase.deletePost(postId)
            _isLoading.value = false
        }
    }

    fun like(postId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = likeUseCase.like(postId)
            _likeResult.value = result
            _isLoading.value = false
        }
    }

    fun showLikes(postId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = likeUseCase.showLikes(postId)
            _showLikesResult.value = result
            _isLoading.value = false
        }
    }

    fun deleteLike(likeId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = likeUseCase.deleteLike(likeId)
            _unlikeResult.value = result
            _isLoading.value = false
        }
    }

    fun getComments(postId: Int, lastCommentId: Int? = null) {
        if (!canLoadMore) return
        viewModelScope.launch {
            _isCommentLoading.value = true
            val result = commentUseCase.getPostComments(postId, lastCommentId)
            if (result is Result.Success) {
                val comments = result.data ?: emptyList()
                _getCommentsResult.value = comments
                if (comments.size < 10) {
                    canLoadMore = false
                }
            } else {
                _getCommentsResult.value = emptyList() // In case of error, clear the comments
            }
            _isCommentLoading.value = false
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = commentUseCase.deleteComment(commentId)
            if (result is Result.Success) {
                _deleteCommentResult.value = Result.Success(commentId)
            } else {
                _deleteCommentResult.value = result as Result<Int>
            }
            _isLoading.value = false
        }
    }

    fun sendComment(postId: Int, comment: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _sendCommentResult.value = commentUseCase.postComment(postId, comment)
            _isLoading.value = false
        }
    }
}
