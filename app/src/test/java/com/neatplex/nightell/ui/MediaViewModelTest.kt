package com.neatplex.nightell.ui

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.repository.PlaybackState
import com.neatplex.nightell.testutil.FakeAudioPlayer
import com.neatplex.nightell.testutil.MainDispatcherRule
import com.neatplex.nightell.testutil.post
import com.neatplex.nightell.ui.viewmodel.MediaViewModel
import com.neatplex.nightell.ui.viewmodel.UIEvent
import com.neatplex.nightell.ui.viewmodel.formatDuration
import org.junit.Rule
import org.junit.Test

class MediaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val player = FakeAudioPlayer()
    private val viewModel by lazy { MediaViewModel(player) }

    @Test
    fun `ui events go to the audio player`() {
        viewModel.play(post(3))
        viewModel.onUIEvent(UIEvent.PlayPause)
        viewModel.onUIEvent(UIEvent.UpdateProgress(0.5f))

        assertThat(player.calls).containsExactly("play:3", "toggle", "seek:0.5").inOrder()
    }

    @Test
    fun `player state is turned into display values`() {
        player.emit(PlaybackState(currentPostId = 3, durationMs = 120_000, positionMs = 30_000, isPlaying = true))

        val state = viewModel.playerState.value
        assertThat(state.progress).isEqualTo(0.25f)
        assertThat(state.positionText).isEqualTo("00:30")
        assertThat(state.durationText).isEqualTo("02:00")
    }

    @Test
    fun `formats durations as minutes and seconds`() {
        assertThat(formatDuration(0)).isEqualTo("00:00")
        assertThat(formatDuration(61_000)).isEqualTo("01:01")
    }
}
