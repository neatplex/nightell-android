package com.neatplex.nightell.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.domain.model.MediaFile
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.usecase.FileUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * Editing the signed-in user's profile. Each action exposes its latest result;
 * null means it hasn't run yet.
 */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val fileUseCase: FileUseCase,
    private val validator: Validator,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _nameResult = MutableStateFlow<Result<User>?>(null)
    val nameResult: StateFlow<Result<User>?> = _nameResult.asStateFlow()

    private val _bioResult = MutableStateFlow<Result<User>?>(null)
    val bioResult: StateFlow<Result<User>?> = _bioResult.asStateFlow()

    private val _usernameResult = MutableStateFlow<Result<User>?>(null)
    val usernameResult: StateFlow<Result<User>?> = _usernameResult.asStateFlow()

    private val _imageResult = MutableStateFlow<Result<User>?>(null)
    val imageResult: StateFlow<Result<User>?> = _imageResult.asStateFlow()

    private val _imageUploadResult = MutableStateFlow<Result<MediaFile>?>(null)
    val imageUploadResult: StateFlow<Result<MediaFile>?> = _imageUploadResult.asStateFlow()

    private val _deleteAccountResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteAccountResult: StateFlow<Result<Unit>?> = _deleteAccountResult.asStateFlow()

    fun isValidUsername(username: String) = validator.isValidUsername(username)

    /** Returns null if the image may be uploaded, otherwise a message for the user. */
    fun validateImage(fileName: String, sizeBytes: Long): String? =
        fileUseCase.validate(fileName, sizeBytes, FileType.IMAGE)

    fun updateName(name: String) = run(_nameResult) { userUseCase.changeProfileName(name) }

    fun updateBio(bio: String) = run(_bioResult) { userUseCase.changeProfileBio(bio) }

    fun updateUsername(username: String) =
        run(_usernameResult) { userUseCase.changeProfileUsername(username) }

    /** Uploads [file], then sets it as the profile image. */
    fun uploadProfileImage(file: File) {
        viewModelScope.launch {
            _isLoading.value = true
            val upload = fileUseCase.upload(file, FileType.IMAGE)
            _imageUploadResult.value = upload
            if (upload is Result.Success) {
                _imageResult.value = userUseCase.changeProfileImage(upload.data.id)
            }
            _isLoading.value = false
        }
    }

    fun deleteAccount() = run(_deleteAccountResult) { userUseCase.deleteAccount() }

    private fun <T> run(target: MutableStateFlow<Result<T>?>, block: suspend () -> Result<T>) {
        viewModelScope.launch {
            _isLoading.value = true
            target.value = block()
            _isLoading.value = false
        }
    }
}
