package com.neatplex.nightell.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.ui.common.PagedListLoader
import com.neatplex.nightell.ui.common.PagedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(postUseCase: PostUseCase) : ViewModel() {

    private val feedLoader = PagedListLoader(viewModelScope, Post::id, postUseCase::loadFeed)
    val feed: StateFlow<PagedState<Post>> = feedLoader.state

    init {
        feedLoader.refresh()
    }

    fun loadMore() = feedLoader.loadNext()

    fun refresh(showIndicator: Boolean = true) = feedLoader.refresh(showIndicator)
}
