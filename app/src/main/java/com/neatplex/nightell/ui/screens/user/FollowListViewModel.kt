package com.neatplex.nightell.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.usecase.FollowUseCase
import com.neatplex.nightell.ui.common.PagedListLoader
import com.neatplex.nightell.ui.common.PagedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class FollowListType { FOLLOWERS, FOLLOWINGS }

/** A user's followers or followings. */
@HiltViewModel
class FollowListViewModel @Inject constructor(
    private val followUseCase: FollowUseCase,
) : ViewModel() {

    private var userId: Int? = null
    private var type = FollowListType.FOLLOWERS

    private val loader = PagedListLoader(viewModelScope, User::id) { lastId ->
        val id = checkNotNull(userId)
        when (type) {
            FollowListType.FOLLOWERS -> followUseCase.followers(id, lastId, PAGE_SIZE)
            FollowListType.FOLLOWINGS -> followUseCase.followings(id, lastId, PAGE_SIZE)
        }
    }
    val users: StateFlow<PagedState<User>> = loader.state

    /** Starts loading the list. Calling it again with the same arguments does nothing. */
    fun start(userId: Int, type: FollowListType) {
        if (this.userId == userId && this.type == type) return
        this.userId = userId
        this.type = type
        loader.refresh()
    }

    fun loadMore() = loader.loadNext()

    private companion object {
        const val PAGE_SIZE = 20
    }
}
