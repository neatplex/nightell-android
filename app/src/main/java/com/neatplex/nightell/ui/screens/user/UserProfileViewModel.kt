package com.neatplex.nightell.ui.screens.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.UserProfile
import com.neatplex.nightell.domain.usecase.FollowUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
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

data class UserProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

/** Another user's profile page. The user id comes from the navigation route. */
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userUseCase: UserUseCase,
    private val followUseCase: FollowUseCase,
    postUseCase: PostUseCase,
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[Routes.ARG_USER_ID])

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val postLoader = PagedListLoader(viewModelScope, Post::id) { lastId ->
        postUseCase.loadUserPosts(userId, lastId)
    }
    val posts: StateFlow<PagedState<Post>> = postLoader.state

    init {
        loadProfile()
        postLoader.refresh()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            _uiState.value = when (val result = userUseCase.getUserProfile(userId)) {
                is Result.Success -> UserProfileUiState(profile = result.data, isLoading = false)
                is Result.Failure -> _uiState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun loadMorePosts() = postLoader.loadNext()

    /** Follows or unfollows. The UI updates at once and rolls back if the request fails. */
    fun toggleFollow() {
        val profile = _uiState.value.profile ?: return
        val follow = !profile.followedByMe
        setFollowed(follow)
        viewModelScope.launch {
            val result = if (follow) followUseCase.follow(userId) else followUseCase.unfollow(userId)
            if (result is Result.Failure) {
                setFollowed(!follow)
                _uiState.update { it.copy(error = result.message) }
            }
        }
    }

    private fun setFollowed(followed: Boolean) {
        _uiState.update { state ->
            val profile = state.profile ?: return@update state
            if (profile.followedByMe == followed) return@update state
            state.copy(
                profile = profile.copy(
                    followedByMe = followed,
                    followersCount = profile.followersCount + if (followed) 1 else -1,
                )
            )
        }
    }
}
