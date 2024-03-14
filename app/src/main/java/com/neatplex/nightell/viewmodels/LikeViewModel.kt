package com.neatplex.nightell.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.dto.Likes
import com.neatplex.nightell.dto.StoreLike
import com.neatplex.nightell.repository.LikeRepository
import javax.inject.Inject
import com.neatplex.nightell.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class LikeViewModel @Inject constructor(private val likeRepository: LikeRepository) : ViewModel(){

    private val _likeResult = MutableLiveData<Result<Any?>>()
    val likeResult: LiveData<Result<Any?>> get() = _likeResult

    private val _showLikesResult = MutableLiveData<Result<Likes?>>()
    val showLikesResult : LiveData<Result<Likes?>> get() = _showLikesResult

    fun like(postId: Int){
        viewModelScope.launch {

            val result = likeRepository.like(postId)
            when (result) {
                is Result.Success -> {
                    _likeResult.value = result
                }

                is Result.Error -> {
                    if (result.code in 400..499) {
                        // TODO
                    }
                    _likeResult.value = result
                }

                is Result.Loading -> {
                }
            }
        }
    }

    fun showLikes(postId: Int){
        viewModelScope.launch {
            _showLikesResult.value = Result.Loading
            val result = likeRepository.showLikes(postId)
            when (result) {
                is Result.Success -> {
                    _showLikesResult.value = result
                }
                is Result.Error -> {
                    if(result.code in 400..499){
                        // TODO
                    }
                    _showLikesResult.value = result
                }
                is Result.Loading -> {
                }
            }
        }
    }

    fun deleteLike(likeId : Int){
        viewModelScope.launch {

            val result = likeRepository.deleteLike(likeId)
            when (result) {
                is Result.Success -> {
                    _likeResult.value = result
                }
                is Result.Error -> {
                    if(result.code in 400..499){
                        // TODO
                    }
                    _likeResult.value = result
                }
                is Result.Loading -> {
                }
            }
        }
    }
}