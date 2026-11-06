package com.neatplex.nightell.ui.screens.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.repository.SessionRepository
import com.neatplex.nightell.domain.usecase.CommentUseCase
import com.neatplex.nightell.domain.usecase.LikeUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.SavedPostUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.ui.common.PagedListLoader
import com.neatplex.nightell.ui.common.PagedState
import com.neatplex.nightell.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostUiState(
    val post: Post? = null,
    val currentUserId: Int? = null,
    val isLoading: Boolean = true,
    /** Message to show when the post couldn't be loaded. */
    val loadError: String? = null,
    val isLiked: Boolean = false,
    val likeId: Int? = null,
    val likesCount: Int = 0,
    val isSaved: Boolean = false,
    val isSendingComment: Boolean = false,
    /** True once the post has been deleted; the screen should close. */
    val isDeleted: Boolean = false,
) {
    val isOwnPost: Boolean get() = post != null && post.user.id == currentUserId
}

/** A single post with its likes, comments and bookmark state. The post id comes from the route. */
@HiltViewModel
class PostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    sessionRepository: SessionRepository,
    private val postUseCase: PostUseCase,
    private val likeUseCase: LikeUseCase,
    private val commentUseCase: CommentUseCase,
    private val savedPostUseCase: SavedPostUseCase,
) : ViewModel() {

    private val postId: Int = checkNotNull(savedStateHandle[Routes.ARG_POST_ID])

    private val _uiState = MutableStateFlow(PostUiState(currentUserId = sessionRepository.currentUserId()))
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    private val commentLoader = PagedListLoader(viewModelScope, Comment::id) { lastId ->
        commentUseCase.getPostComments(postId, lastId)
    }
    val comments: StateFlow<PagedState<Comment>> = commentLoader.state

    init {
        loadPost()
        loadLikes()
        commentLoader.refresh()
        viewModelScope.launch {
            val saved = savedPostUseCase.isSaved(postId)
            _uiState.update { it.copy(isSaved = saved) }
        }
    }

    fun loadPost() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            when (val result = postUseCase.getPostDetail(postId)) {
                is Result.Success -> _uiState.update { it.copy(post = result.data, isLoading = false) }
                is Result.Failure -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadError = if (result.code == 404) "Post not found" else "Something went wrong",
                    )
                }
            }
        }
    }

    private fun loadLikes() {
        viewModelScope.launch {
            val result = likeUseCase.getLikes(postId)
            if (result is Result.Success) {
                val myLike = result.data.find { it.userId == _uiState.value.currentUserId }
                _uiState.update {
                    it.copy(likesCount = result.data.size, isLiked = myLike != null, likeId = myLike?.id)
                }
            }
        }
    }

    /** Likes or unlikes. The UI updates at once and rolls back if the request fails. */
    fun toggleLike() {
        val state = _uiState.value
        if (state.isLiked) {
            val likeId = state.likeId ?: return // the like request hasn't returned yet
            _uiState.update { it.copy(isLiked = false, likeId = null, likesCount = it.likesCount - 1) }
            viewModelScope.launch {
                if (likeUseCase.unlike(likeId) is Result.Failure) {
                    _uiState.update { it.copy(isLiked = true, likeId = likeId, likesCount = it.likesCount + 1) }
                }
            }
        } else {
            _uiState.update { it.copy(isLiked = true, likesCount = it.likesCount + 1) }
            viewModelScope.launch {
                when (val result = likeUseCase.like(postId)) {
                    is Result.Success -> _uiState.update { it.copy(likeId = result.data.id) }
                    is Result.Failure -> _uiState.update {
                        it.copy(isLiked = false, likesCount = it.likesCount - 1)
                    }
                }
            }
        }
    }

    fun toggleSaved() {
        val post = _uiState.value.post ?: return
        val save = !_uiState.value.isSaved
        _uiState.update { it.copy(isSaved = save) }
        viewModelScope.launch {
            if (save) savedPostUseCase.save(post) else savedPostUseCase.remove(post.id)
        }
    }

    fun loadMoreComments() = commentLoader.loadNext()

    fun sendComment(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingComment = true) }
            val result = commentUseCase.postComment(postId, text)
            if (result is Result.Success) commentLoader.updateItems { listOf(result.data) + it }
            _uiState.update { it.copy(isSendingComment = false) }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            if (commentUseCase.deleteComment(commentId) is Result.Success) {
                commentLoader.updateItems { comments -> comments.filterNot { it.id == commentId } }
            }
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            if (postUseCase.deletePost(postId) is Result.Success) {
                _uiState.update { it.copy(isDeleted = true) }
            }
        }
    }
}
