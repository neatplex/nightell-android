package com.neatplex.nightell.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.domain.repository.FileRepository
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import com.neatplex.nightell.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class FileViewModel @Inject constructor(private val fileRepository: FileRepository) : ViewModel() {

    private val _uploadState = MutableLiveData<Result<FileUploadResponse?>>()
    val uploadState: LiveData<Result<FileUploadResponse?>> get() = _uploadState
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
}