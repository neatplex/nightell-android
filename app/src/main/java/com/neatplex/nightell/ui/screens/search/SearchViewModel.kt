package com.neatplex.nightell.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.ui.common.PagedListLoader
import com.neatplex.nightell.ui.common.PagedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    postUseCase: PostUseCase,
    userUseCase: UserUseCase,
) : ViewModel() {

    private var postQuery = ""
    private var userQuery = ""

    private val postLoader = PagedListLoader(viewModelScope, Post::id) { lastId ->
        postUseCase.search(postQuery, lastId)
    }
    private val userLoader = PagedListLoader(viewModelScope, User::id) { lastId ->
        userUseCase.searchUser(userQuery, lastId)
    }

    val posts: StateFlow<PagedState<Post>> = postLoader.state
    val users: StateFlow<PagedState<User>> = userLoader.state

    /** Starts a new post search; any search still running is cancelled. */
    fun searchPosts(query: String) {
        postQuery = query
        postLoader.refresh()
    }

    /** Starts a new user search; any search still running is cancelled. */
    fun searchUsers(query: String) {
        userQuery = query
        userLoader.refresh()
    }

    fun loadMorePosts() = postLoader.loadNext()

    fun loadMoreUsers() = userLoader.loadNext()
}
