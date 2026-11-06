package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.domain.model.Post
import kotlinx.coroutines.flow.StateFlow

/** What the audio player is doing right now. */
data class PlaybackState(
    /** The post whose audio is loaded, or null if nothing has been played. */
    val currentPostId: Int? = null,
    /** Title and cover of the loaded post, for the player UI. */
    val title: String? = null,
    val imageUrl: String? = null,
    val durationMs: Long = 0L,
    val positionMs: Long = 0L,
    val isPlaying: Boolean = false,
    /** True while background playback (and its notification) is active. */
    val isActive: Boolean = false,
)

/**
 * Plays a post's audio in the background. The domain only knows this contract;
 * the ExoPlayer/media-service details live behind it.
 */
interface AudioPlayer {
    val state: StateFlow<PlaybackState>

    /** Loads [post]'s audio and starts playing it. */
    fun play(post: Post)
    fun togglePlayPause()
    fun seekBack()
    fun seekForward()

    /** Seeks to a position given as a fraction (0..1) of the duration. */
    fun seekTo(fraction: Float)

    /** Stops playback and the background service. */
    fun stop()
}
