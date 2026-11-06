package com.neatplex.nightell.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.Profile
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.ui.common.PagedListLoader
import com.neatplex.nightell.ui.common.PagedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
)

/** The signed-in user's own profile page. */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    postUseCase: PostUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val postLoader = PagedListLoader(viewModelScope, Post::id) { lastId ->
        val userId = _uiState.value.profile?.user?.id
            ?: return@PagedListLoader Result.Failure("Profile not loaded")
        postUseCase.loadUserPosts(userId, lastId)
    }
    val posts: StateFlow<PagedState<Post>> = postLoader.state

    init {
        refresh(showIndicator = false)
    }

    /** Reloads the profile, then the user's posts. */
    fun refresh(showIndicator: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isRefreshing = showIndicator, error = null) }
            when (val result = userUseCase.profile()) {
                is Result.Success -> {
                    _uiState.value = ProfileUiState(profile = result.data, isLoading = false)
                    postLoader.refresh()
                }
                is Result.Failure -> _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, error = result.message)
                }
            }
        }
    }

    fun loadMorePosts() = postLoader.loadNext()
}
