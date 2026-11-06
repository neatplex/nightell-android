package com.neatplex.nightell.service.media

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.repository.AudioPlayer
import com.neatplex.nightell.domain.repository.PlaybackState
import com.neatplex.nightell.service.ServiceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/** [AudioPlayer] backed by ExoPlayer and the foreground [MediaService]. */
@Singleton
class ExoAudioPlayer @Inject constructor(
    private val handler: MediaServiceHandler,
    private val serviceManager: ServiceManager,
) : AudioPlayer {

    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    // A singleton, so this scope lives as long as the app.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {
        scope.launch { handler.simpleMediaState.collect(::onMediaState) }
        scope.launch {
            serviceManager.isServiceRunning.collect { running ->
                _state.update { it.copy(isActive = running) }
            }
        }
    }

    override fun play(post: Post) {
        serviceManager.startMediaService()
        val metadata = MediaMetadata.Builder()
            .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
            .setAlbumTitle(post.title)
            .setDisplayTitle("Nightell")
            .apply { post.image?.url?.let { setArtworkUri(Uri.parse(it)) } }
            .build()
        val item = MediaItem.Builder().setUri(post.audio.url).setMediaMetadata(metadata).build()

        _state.update { PlaybackState(currentPostId = post.id, isActive = it.isActive) }
        handler.addMediaItem(item)
        handler.onPlayerEvent(PlayerEvent.PlayPause)
    }

    override fun togglePlayPause() = handler.onPlayerEvent(PlayerEvent.PlayPause)

    override fun seekBack() = handler.onPlayerEvent(PlayerEvent.Backward)

    override fun seekForward() = handler.onPlayerEvent(PlayerEvent.Forward)

    override fun seekTo(fraction: Float) {
        _state.update { it.copy(positionMs = (it.durationMs * fraction).toLong()) }
        handler.onPlayerEvent(PlayerEvent.UpdateProgress(fraction))
    }

    override fun stop() {
        handler.onPlayerEvent(PlayerEvent.Stop)
        serviceManager.stopMediaService()
    }

    private fun onMediaState(mediaState: SimpleMediaState) {
        _state.update {
            when (mediaState) {
                SimpleMediaState.Initial -> it
                is SimpleMediaState.Ready -> it.copy(durationMs = mediaState.duration)
                is SimpleMediaState.Buffering -> it.copy(positionMs = mediaState.progress)
                is SimpleMediaState.Progress -> it.copy(positionMs = mediaState.progress)
                is SimpleMediaState.Playing -> it.copy(isPlaying = mediaState.isPlaying)
                SimpleMediaState.Completed -> it.copy(isPlaying = false, positionMs = 0L)
            }
        }
    }
}
