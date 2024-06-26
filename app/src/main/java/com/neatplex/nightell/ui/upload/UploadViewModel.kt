package com.neatplex.nightell.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.data.dto.PostStoreResponse
import com.neatplex.nightell.domain.repository.FileRepository
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(private val postUseCase: PostUseCase, private val fileRepository: FileRepository) : ViewModel() {

    private var _storePostResult = MutableLiveData<Result<PostStoreResponse>>()
    val storePostResult: LiveData<Result<PostStoreResponse>> get() = _storePostResult

    private val _uploadState = MutableLiveData<Result<FileUploadResponse>>()
    val uploadState: LiveData<Result<FileUploadResponse>> get() = _uploadState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun uploadFile(file: File, extension: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = fileRepository.uploadFile(file, extension)
            _uploadState.value = result
            _isLoading.value = false
        }
    }

    fun uploadPost(title: String, description: String?, audioId: Int, imageId: Int?) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = postUseCase.uploadPost(title, description, audioId, imageId)
            _storePostResult.value = result
            _isLoading.value = false
        }
    }
}