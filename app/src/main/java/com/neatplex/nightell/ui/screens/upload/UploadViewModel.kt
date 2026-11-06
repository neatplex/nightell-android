package com.neatplex.nightell.ui.screens.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.domain.usecase.FileUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class UploadedFile(val id: Int, val name: String)

/** The new-post wizard: audio, then optional image, then title and caption. */
data class UploadUiState(
    val step: Int = 1,
    val audio: UploadedFile? = null,
    val image: UploadedFile? = null,
    val title: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    /** True once the post is published; the screen resets and notifies the feed. */
    val isPublished: Boolean = false,
)

/**
 * Holds the draft for the new-post wizard. Scoped to the activity, so the draft
 * survives switching tabs.
 */
@HiltViewModel
class UploadViewModel @Inject constructor(
    private val postUseCase: PostUseCase,
    private val fileUseCase: FileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    /** Returns null if the file may be uploaded, otherwise a message for the user. */
    fun validate(fileName: String, sizeBytes: Long, type: FileType): String? =
        fileUseCase.validate(fileName, sizeBytes, type)

    fun uploadFile(file: File, fileName: String, type: FileType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = fileUseCase.upload(file, type)) {
                is Result.Success -> _uiState.update {
                    val uploaded = UploadedFile(result.data.id, fileName)
                    when (type) {
                        FileType.AUDIO -> it.copy(audio = uploaded, isLoading = false)
                        FileType.IMAGE -> it.copy(image = uploaded, isLoading = false)
                    }
                }
                is Result.Failure -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun nextStep() {
        _uiState.update { state ->
            when {
                state.step == 1 && state.audio == null -> state
                state.step < LAST_STEP -> state.copy(step = state.step + 1)
                else -> state
            }
        }
    }

    fun setTitle(title: String) = _uiState.update { it.copy(title = title) }

    fun setDescription(description: String) = _uiState.update { it.copy(description = description) }

    fun showError(message: String) = _uiState.update { it.copy(error = message) }

    fun clearError() = _uiState.update { it.copy(error = null) }

    fun publish(sanitize: (String) -> String) {
        val state = _uiState.value
        val audio = state.audio ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val description = state.description.takeIf { it.isNotEmpty() }?.let(sanitize)
            when (val result = postUseCase.uploadPost(state.title, description, audio.id, state.image?.id)) {
                is Result.Success -> _uiState.value = UploadUiState(isPublished = true)
                is Result.Failure -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    /** Call after reacting to [UploadUiState.isPublished]. */
    fun onPublishedHandled() = _uiState.update { it.copy(isPublished = false) }

    private companion object {
        const val LAST_STEP = 3
    }
}
