package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.SavedPost
import com.neatplex.nightell.domain.usecase.SavedPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Bookmarks stored on the device. */
@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val savedPostUseCase: SavedPostUseCase,
) : ViewModel() {

    val savedPosts: StateFlow<List<SavedPost>> = savedPostUseCase.observeSavedPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun isSaved(postId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(savedPostUseCase.isSaved(postId)) }
    }

    fun save(post: Post) {
        viewModelScope.launch { savedPostUseCase.save(post) }
    }

    fun remove(postId: Int) {
        viewModelScope.launch { savedPostUseCase.remove(postId) }
    }
}
