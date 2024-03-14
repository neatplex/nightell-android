package com.neatplex.nightell.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.dto.FileUploadResponse
import com.neatplex.nightell.repository.FileRepository
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import com.neatplex.nightell.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class FileViewModel @Inject constructor(private val fileRepository: FileRepository) : ViewModel() {

    private val _uploadState = MutableLiveData<Result<FileUploadResponse>>()
    val uploadState: LiveData<Result<FileUploadResponse>> get() = _uploadState

    fun uploadFile(file: File, extension: String) {
        viewModelScope.launch {
            _uploadState.value = Result.Loading

            val result = fileRepository.uploadFile(file, extension)
            when (result) {
                is Result.Success -> {
                    _uploadState.value = result
                }
                is Result.Error -> {
                    if(result.code in 400..499){
                        // TODO
                    }
                    _uploadState.value = result
                }
                is Result.Loading -> {
                }
            }
        }
    }

}