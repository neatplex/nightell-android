package com.neatplex.nightell.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.service.ServiceManager
import com.neatplex.nightell.service.media.MediaServiceHandler
import com.neatplex.nightell.service.media.PlayerEvent
import com.neatplex.nightell.service.media.SimpleMediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/** What the player UI shows. */
data class PlayerUiState(
    /** The post whose audio is loaded, or null if nothing has been played. */
    val currentPostId: Int? = null,
    val durationMs: Long = 0L,
    val positionMs: Long = 0L,
    val isPlaying: Boolean = false,
) {
    val progress: Float get() = if (durationMs > 0) positionMs.toFloat() / durationMs else 0f
    val positionText: String get() = formatDuration(positionMs)
    val durationText: String get() = formatDuration(durationMs)
}

fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) - minutes * 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

sealed class UIEvent {
    data object PlayPause : UIEvent()
    data object Backward : UIEvent()
    data object Forward : UIEvent()
    data class UpdateProgress(val newProgress: Float) : UIEvent()
}

/** Activity-scoped bridge between the UI and the background media service. */
@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaServiceHandler: MediaServiceHandler,
    private val serviceManager: ServiceManager,
) : ViewModel() {

    private val _playerState = MutableStateFlow(PlayerUiState())
    val playerState: StateFlow<PlayerUiState> = _playerState.asStateFlow()

    val isServiceRunning: StateFlow<Boolean> = serviceManager.isServiceRunning

    init {
        viewModelScope.launch {
            mediaServiceHandler.simpleMediaState.collect(::onMediaState)
        }
    }

    /** Loads [post]'s audio into the player and starts playing it. */
    fun play(post: Post) {
        serviceManager.startMediaService()
        val metadata = MediaMetadata.Builder()
            .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
            .setAlbumTitle(post.title)
            .setDisplayTitle("Nightell")
            .apply { post.image?.url?.let { setArtworkUri(Uri.parse(it)) } }
            .build()
        val item = MediaItem.Builder().setUri(post.audio.url).setMediaMetadata(metadata).build()

        _playerState.value = PlayerUiState(currentPostId = post.id)
        mediaServiceHandler.addMediaItem(item)
        onUIEvent(UIEvent.PlayPause)
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            UIEvent.Backward -> mediaServiceHandler.onPlayerEvent(PlayerEvent.Backward)
            UIEvent.Forward -> mediaServiceHandler.onPlayerEvent(PlayerEvent.Forward)
            UIEvent.PlayPause -> mediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
            is UIEvent.UpdateProgress -> {
                _playerState.update {
                    it.copy(positionMs = (it.durationMs * event.newProgress).toLong())
                }
                mediaServiceHandler.onPlayerEvent(PlayerEvent.UpdateProgress(event.newProgress))
            }
        }
    }

    override fun onCleared() {
        mediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
        super.onCleared()
    }

    private fun onMediaState(state: SimpleMediaState) {
        _playerState.update {
            when (state) {
                SimpleMediaState.Initial -> it
                is SimpleMediaState.Ready -> it.copy(durationMs = state.duration)
                is SimpleMediaState.Buffering -> it.copy(positionMs = state.progress)
                is SimpleMediaState.Progress -> it.copy(positionMs = state.progress)
                is SimpleMediaState.Playing -> it.copy(isPlaying = state.isPlaying)
                SimpleMediaState.Completed -> it.copy(isPlaying = false, positionMs = 0L)
            }
        }
    }
}
