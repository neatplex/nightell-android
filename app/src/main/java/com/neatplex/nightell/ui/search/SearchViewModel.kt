package com.neatplex.nightell.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val postUseCase: PostUseCase
            ) :
    ViewModel() {

    private val _posts = MutableLiveData<List<Post>?>()
    val posts: LiveData<List<Post>?> get() = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    var canLoadMore = true // Default to true for initial load

    fun search(query: String, lastPostId: Int?, isSame: Boolean) {
        if(isSame && !canLoadMore) return
        //if (!canLoadMore || _isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = postUseCase.search(query, lastPostId)
            if (result is Result.Success) {
                val posts = result.data ?: emptyList()
                if (posts.size < 10) {
                    canLoadMore = false
                }
                if(!isSame){
                    canLoadMore = true
                    _posts.value = emptyList()
                }
                _posts.value = _posts.value.orEmpty() + posts
            } else {
                _posts.value = emptyList()
            }
            _isLoading.value = false
        }
    }
}
