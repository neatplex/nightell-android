package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.repository.SearchRepository
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) :
    ViewModel() {

    private var lastPostId: Int? = null
    private var allSearchPosts = emptyList<Post>()


//    fun search(q: String){
//
//        viewModelScope.launch {
//
//            val result = searchRepository.search(q, lastPostId)
//
//            if (result is Result.Success) {
//                val newFeed = result.data?.posts ?: emptyList()
//                if (newFeed.isNotEmpty()) {
//                    lastPostId = newFeed.last().id
//                    allSearchPosts = (allSearchPosts + newFeed).distinctBy { it.id }
//                }
//            }
//
//            _searchResult.value = allSearchPosts
//        }
//    }
//}

    private val _searchResult = MutableLiveData<List<Post>>()
    val searchResult: LiveData<List<Post>> get() = _searchResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    init {
        search("")
    }

    fun search(q: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = searchRepository.search(q, lastPostId)

            if (result is Result.Success) {
                val posts = result.data?.posts
                _searchResult.value = posts!!
            } else {
                _searchResult.value = result as List<Post>
            }

            _isLoading.value = false
        }
    }
}
