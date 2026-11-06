package com.neatplex.nightell.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.domain.model.LocalFile
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.usecase.FileUseCase
import com.neatplex.nightell.domain.usecase.UserUseCase
import com.neatplex.nightell.domain.util.Result
import com.neatplex.nightell.domain.util.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** A profile field that can be saved on its own. */
enum class ProfileField { NAME, BIO, USERNAME, IMAGE }

data class EditProfileUiState(
    /** Fields with a save in flight. */
    val saving: Set<ProfileField> = emptySet(),
    val isDeletingAccount: Boolean = false,
    /** The last error to show, or null. */
    val error: String? = null,
) {
    val isBusy: Boolean get() = saving.isNotEmpty() || isDeletingAccount
}

sealed interface EditProfileEvent {
    /** A field was saved; [user] is the updated profile. */
    data class Saved(val field: ProfileField, val user: User) : EditProfileEvent

    /** Saving [field] failed; the screen should restore the field's old value. */
    data class SaveFailed(val field: ProfileField) : EditProfileEvent
}

/** Editing the signed-in user's profile, one field at a time. */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val fileUseCase: FileUseCase,
    private val validator: Validator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _events = Channel<EditProfileEvent>(Channel.BUFFERED)
    val events: Flow<EditProfileEvent> = _events.receiveAsFlow()

    fun isValidUsername(username: String) = validator.isValidUsername(username)

    fun updateName(name: String) = save(ProfileField.NAME) { userUseCase.changeProfileName(name) }

    fun updateBio(bio: String) = save(ProfileField.BIO) { userUseCase.changeProfileBio(bio) }

    fun updateUsername(username: String) =
        save(ProfileField.USERNAME) { userUseCase.changeProfileUsername(username) }

    /**
     * Checks the chosen image, copies it off the main thread with [copy], uploads
     * it, then sets it as the profile image.
     */
    fun onImageChosen(fileName: String, sizeBytes: Long, copy: () -> LocalFile?) {
        fileUseCase.validate(fileName, sizeBytes, FileType.IMAGE)?.let { message ->
            _uiState.update { it.copy(error = message) }
            return
        }
        save(ProfileField.IMAGE) {
            val file = withContext(Dispatchers.IO) { copy() }
                ?: return@save Result.Failure("Could not read the selected image")
            when (val upload = fileUseCase.upload(file, FileType.IMAGE)) {
                is Result.Success -> userUseCase.changeProfileImage(upload.data.id)
                is Result.Failure -> upload
            }
        }
    }

    /**
     * Deletes the account. On success the use case clears the session, and the
     * app returns to sign-in from there.
     */
    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, error = null) }
            val result = userUseCase.deleteAccount()
            if (result is Result.Failure) {
                _uiState.update { it.copy(isDeletingAccount = false, error = result.message) }
            }
        }
    }

    private fun save(field: ProfileField, block: suspend () -> Result<User>) {
        viewModelScope.launch {
            _uiState.update { it.copy(saving = it.saving + field, error = null) }
            when (val result = block()) {
                is Result.Success -> _events.send(EditProfileEvent.Saved(field, result.data))
                is Result.Failure -> {
                    _uiState.update { it.copy(error = result.message) }
                    _events.send(EditProfileEvent.SaveFailed(field))
                }
            }
            _uiState.update { it.copy(saving = it.saving - field) }
        }
    }
}
