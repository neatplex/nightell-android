package com.neatplex.nightell.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.model.Post
import com.neatplex.nightell.repository.SearchRepository
import com.neatplex.nightell.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository : SearchRepository) : ViewModel() {

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
            val result = searchRepository.search(q)

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