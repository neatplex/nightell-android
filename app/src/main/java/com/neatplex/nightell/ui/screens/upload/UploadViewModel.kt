package com.neatplex.nightell.ui.screens.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.domain.model.LocalFile
import com.neatplex.nightell.domain.usecase.FileUseCase
import com.neatplex.nightell.domain.usecase.PostUseCase
import com.neatplex.nightell.domain.util.Result
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
)

/** One-off things the screen should react to once. */
sealed interface UploadEvent {
    /** The post is live; the screen resets and tells the feed to refresh. */
    data object Published : UploadEvent
}

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

    private val _events = Channel<UploadEvent>(Channel.BUFFERED)
    val events: Flow<UploadEvent> = _events.receiveAsFlow()

    /**
     * Checks a chosen file against the upload rules, then copies it off the main
     * thread with [copy] and uploads it.
     */
    fun onFileChosen(fileName: String, sizeBytes: Long, type: FileType, copy: () -> LocalFile?) {
        fileUseCase.validate(fileName, sizeBytes, type)?.let { message ->
            showError(message)
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val file = withContext(Dispatchers.IO) { copy() }
            if (file == null) {
                _uiState.update { it.copy(isLoading = false, error = "Could not read the selected file") }
                return@launch
            }
            when (val result = fileUseCase.upload(file, type)) {
                is Result.Success -> _uiState.update {
                    val uploaded = UploadedFile(result.data.id, file.name)
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
                is Result.Success -> {
                    _uiState.value = UploadUiState()
                    _events.send(UploadEvent.Published)
                }
                is Result.Failure -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    private companion object {
        const val LAST_STEP = 3
    }
}
