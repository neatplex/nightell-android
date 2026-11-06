package com.neatplex.nightell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.repository.AudioPlayer
import com.neatplex.nightell.domain.repository.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/** What the player UI shows. */
data class PlayerUiState(
    val currentPostId: Int? = null,
    val durationMs: Long = 0L,
    val positionMs: Long = 0L,
    val isPlaying: Boolean = false,
    /** Whether the mini player should be shown. */
    val isActive: Boolean = false,
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

/** Activity-scoped bridge between the player UI and the domain [AudioPlayer]. */
@HiltViewModel
class MediaViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
) : ViewModel() {

    val playerState: StateFlow<PlayerUiState> = audioPlayer.state
        .map(::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, toUiState(audioPlayer.state.value))

    fun play(post: Post) = audioPlayer.play(post)

    fun onUIEvent(event: UIEvent) {
        when (event) {
            UIEvent.Backward -> audioPlayer.seekBack()
            UIEvent.Forward -> audioPlayer.seekForward()
            UIEvent.PlayPause -> audioPlayer.togglePlayPause()
            is UIEvent.UpdateProgress -> audioPlayer.seekTo(event.newProgress)
        }
    }

    /** Stops playback and the background service, e.g. when the app is closed. */
    fun stop() = audioPlayer.stop()

    private fun toUiState(state: PlaybackState) = PlayerUiState(
        currentPostId = state.currentPostId,
        durationMs = state.durationMs,
        positionMs = state.positionMs,
        isPlaying = state.isPlaying,
        isActive = state.isActive,
    )
}
