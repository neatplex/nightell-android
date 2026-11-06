package com.neatplex.nightell.ui.screens.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val postUseCase: PostUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Result of the last save; null until the user saves. */
    private val _updateResult = MutableStateFlow<Result<Post>?>(null)
    val updateResult: StateFlow<Result<Post>?> = _updateResult.asStateFlow()

    fun updatePost(postId: Int, title: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateResult.value = postUseCase.editPost(postId, title, description)
            _isLoading.value = false
        }
    }
}
