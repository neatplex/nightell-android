package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post> = _post

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _audioFileState = MutableLiveData<FileState>()
    val audioFileState: LiveData<FileState> = _audioFileState

    private val _imageFileState = MutableLiveData<FileState>()
    val imageFileState: LiveData<FileState> = _imageFileState

    private val _postTitle = MutableLiveData<String>()
    val postTitle: LiveData<String> = _postTitle

    private val _postDescription = MutableLiveData<String>()
    val postDescription: LiveData<String> = _postDescription

    private val _currentUploadStep = MutableLiveData<Int>()
    val currentUploadStep: LiveData<Int> = _currentUploadStep

    fun setCurrentStep(step: Int) {
        _currentUploadStep.value = step
    }

    fun setAudioFileState(fileId: Int, fileName: String) {
        _audioFileState.value = FileState(fileId, fileName)
    }

    fun setImageFileState(fileId: Int?, fileName: String) {
        _imageFileState.value = FileState(fileId, fileName)
    }

    fun setPostTitle(title: String) {
        _postTitle.value = title
    }

    fun setPostDescription(description: String) {
        _postDescription.value = description
    }

    fun resetPostData() {
        _postTitle.value = ""
        _postDescription.value = ""
        _audioFileState.value = FileState(0, "")
        _imageFileState.value = FileState(null, "")
    }

    val tokenState: StateFlow<String?> = tokenManager.tokenState

    fun setPost(post: Post) {
        _post.value = post
    }

    fun setUser(user: User) {
        _user.value = user
    }

    fun deleteToken() {
        tokenManager.deleteToken()
    }
}

data class FileState(
    val fileId: Int?,
    val fileName: String
)
