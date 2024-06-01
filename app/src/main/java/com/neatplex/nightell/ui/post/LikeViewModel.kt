package com.neatplex.nightell.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.Likes
import com.neatplex.nightell.data.dto.StoreLike
import com.neatplex.nightell.domain.repository.LikeRepository
import javax.inject.Inject
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class LikeViewModel @Inject constructor(private val likeRepository: LikeRepository) : ViewModel(){

    private val _likeResult = MutableLiveData<Result<StoreLike?>>()
    val likeResult: LiveData<Result<StoreLike?>> get() = _likeResult

    private val _unlikeResult = MutableLiveData<Result<Any?>>()
    val unlikeResult: LiveData<Result<Any?>> get() = _unlikeResult

    private val _showLikesResult = MutableLiveData<Result<Likes?>>()
    val showLikesResult : LiveData<Result<Likes?>> get() = _showLikesResult

    fun like(postId: Int){
        viewModelScope.launch {
            val result = likeRepository.like(postId)
            _likeResult.value = result
        }
    }

    fun showLikes(postId: Int){
        viewModelScope.launch {
            val result = likeRepository.showLikes(postId)
            _showLikesResult.value = result
        }
    }

    fun deleteLike(likeId : Int){
        viewModelScope.launch {
            val result = likeRepository.deleteLike(likeId)
            _unlikeResult.value = result
        }
    }
}