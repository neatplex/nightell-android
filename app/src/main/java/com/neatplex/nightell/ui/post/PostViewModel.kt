package com.neatplex.nightell.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.PostStoreResponse
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.repository.PostRepositoryImpl
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postUseCase: PostUseCase
) : ViewModel() {

    private val _postDeleteResult = MutableLiveData<Result<Any?>>()
    val postDeleteResult: LiveData<Result<Any?>> get() = _postDeleteResult

    private var _storePostResult = MutableLiveData<Result<PostStoreResponse?>>()
    val storePostResult: LiveData<Result<PostStoreResponse?>> get() = _storePostResult


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun editPost(postId: Int, newTitle: String, newDescription: String) {
        viewModelScope.launch {
            _storePostResult.value = Result.Loading
            _storePostResult.value = postUseCase.editPost(postId, newTitle, newDescription)
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            _postDeleteResult.value = Result.Loading
            _postDeleteResult.value = postUseCase.deletePost(postId)
        }
    }

}