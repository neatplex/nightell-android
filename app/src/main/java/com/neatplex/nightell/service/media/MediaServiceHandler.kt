package com.neatplex.nightell.service.media

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Wraps the ExoPlayer and publishes its state as a flow. */
class MediaServiceHandler @Inject constructor(
    private val player: ExoPlayer,
) : Player.Listener {

    private val _simpleMediaState = MutableStateFlow<SimpleMediaState>(SimpleMediaState.Initial)
    val simpleMediaState: StateFlow<SimpleMediaState> = _simpleMediaState.asStateFlow()

    // The handler is a singleton, so its scope lives as long as the app.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var progressJob: Job? = null

    init {
        player.addListener(this)
    }

    fun addMediaItem(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun addMediaItemList(mediaItems: List<MediaItem>) {
        player.setMediaItems(mediaItems)
        player.prepare()
    }

    fun onPlayerEvent(playerEvent: PlayerEvent) {
        when (playerEvent) {
            PlayerEvent.Backward -> player.seekBack()
            PlayerEvent.Forward -> player.seekForward()
            PlayerEvent.PlayPause -> {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    if (_simpleMediaState.value is SimpleMediaState.Completed) player.seekTo(0)
                    player.play()
                }
            }
            PlayerEvent.Stop -> stopProgressUpdates()
            is PlayerEvent.UpdateProgress ->
                player.seekTo((player.duration * playerEvent.newProgress).toLong())
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING ->
                _simpleMediaState.value = SimpleMediaState.Buffering(player.currentPosition)
            Player.STATE_READY ->
                _simpleMediaState.value = SimpleMediaState.Ready(player.duration)
            Player.STATE_ENDED -> {
                stopProgressUpdates()
                _simpleMediaState.value = SimpleMediaState.Completed
                player.seekTo(0)
                player.pause()
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying)
        if (isPlaying) startProgressUpdates() else stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        if (progressJob?.isActive == true) return
        progressJob = scope.launch {
            while (isActive) {
                _simpleMediaState.value = SimpleMediaState.Progress(player.currentPosition)
                delay(PROGRESS_INTERVAL_MS)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private companion object {
        const val PROGRESS_INTERVAL_MS = 500L
    }
}

sealed class PlayerEvent {
    data object PlayPause : PlayerEvent()
    data object Backward : PlayerEvent()
    data object Forward : PlayerEvent()
    data object Stop : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}

sealed class SimpleMediaState {
    data object Initial : SimpleMediaState()
    data class Ready(val duration: Long) : SimpleMediaState()
    data class Progress(val progress: Long) : SimpleMediaState()
    data class Buffering(val progress: Long) : SimpleMediaState()
    data class Playing(val isPlaying: Boolean) : SimpleMediaState()
    data object Completed : SimpleMediaState()
}
