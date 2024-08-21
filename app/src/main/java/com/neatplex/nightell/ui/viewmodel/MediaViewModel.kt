package com.neatplex.nightell.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaMetadata
import com.neatplex.nightell.service.PlayerEvent
import com.neatplex.nightell.service.MediaServiceHandler
import com.neatplex.nightell.service.ServiceManager
import com.neatplex.nightell.service.SimpleMediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class MediaViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mediaServiceHandler: MediaServiceHandler,
    private val serviceManager: ServiceManager
) : ViewModel() {

    // State variables using SavedStateHandle for persistence
    var duration by savedStateHandle.saveable { mutableStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableStateOf(0f) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var initial = false
    val isServiceRunning: StateFlow<Boolean> = serviceManager.isServiceRunning

    var currentPostId by savedStateHandle.saveable { mutableStateOf("") }
    private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        // Collect media state updates
        viewModelScope.launch {
            mediaServiceHandler.simpleMediaState.collect { mediaState ->
                handleMediaState(mediaState)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            mediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
    }

    fun onUIEvent(uiEvent: UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            UIEvent.Backward -> mediaServiceHandler.onPlayerEvent(PlayerEvent.Backward)
            UIEvent.Forward -> mediaServiceHandler.onPlayerEvent(PlayerEvent.Forward)
            UIEvent.PlayPause -> mediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
            is UIEvent.UpdateProgress -> {
                progress = uiEvent.newProgress
                mediaServiceHandler.onPlayerEvent(PlayerEvent.UpdateProgress(uiEvent.newProgress))
            }
        }
    }

    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) - minutes * 60)
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun calculateProgressValues(currentProgress: Long) {
        progress = if (duration > 0) (currentProgress.toFloat() / duration) else 0f
        progressString = formatDuration(currentProgress)
    }

    fun loadData(audioPath: String, imagePath: String, title: String, postId: String) {
        initial = true
        currentPostId = postId
        val mediaItem = androidx.media3.common.MediaItem.Builder()
            .setUri(audioPath)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
                    .setArtworkUri(Uri.parse(imagePath))
                    .setAlbumTitle(title)
                    .setDisplayTitle("Nightell")
                    .build()
            ).build()

        mediaServiceHandler.addMediaItem(mediaItem)
    }

    fun startMediaService() {
        serviceManager.startMediaService()
    }

    private fun handleMediaState(mediaState: SimpleMediaState) {
        when (mediaState) {
            is SimpleMediaState.Buffering -> calculateProgressValues(mediaState.progress)
            is SimpleMediaState.Initial -> _uiState.value = UIState.Initial
            is SimpleMediaState.Playing -> isPlaying = mediaState.isPlaying
            is SimpleMediaState.Progress -> calculateProgressValues(mediaState.progress)
            is SimpleMediaState.Ready -> {
                duration = mediaState.duration
                _uiState.value = UIState.Ready
            }
            is SimpleMediaState.Completed -> {
                isPlaying = false
                _uiState.value = UIState.Initial
            }
        }
    }
}

// Sealed class for UI events
sealed class UIEvent {
    object PlayPause : UIEvent()
    object Backward : UIEvent()
    object Forward : UIEvent()
    data class UpdateProgress(val newProgress: Float) : UIEvent()
}

// Sealed class for UI states
sealed class UIState {
    object Initial : UIState()
    object Ready : UIState()
}

