package com.neatplex.nightell.ui.screens.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditPostUiState(
    val isSaving: Boolean = false,
    val error: String? = null,
)

sealed interface EditPostEvent {
    /** The changes are saved; the screen should close and refresh the post. */
    data object Saved : EditPostEvent
}

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val postUseCase: PostUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPostUiState())
    val uiState: StateFlow<EditPostUiState> = _uiState.asStateFlow()

    private val _events = Channel<EditPostEvent>(Channel.BUFFERED)
    val events: Flow<EditPostEvent> = _events.receiveAsFlow()

    fun updatePost(postId: Int, title: String, description: String) {
        viewModelScope.launch {
            _uiState.value = EditPostUiState(isSaving = true)
            when (val result = postUseCase.editPost(postId, title, description)) {
                is Result.Success -> {
                    _uiState.value = EditPostUiState()
                    _events.send(EditPostEvent.Saved)
                }
                is Result.Failure -> _uiState.value = EditPostUiState(error = result.message)
            }
        }
    }
}
