package com.neatplex.nightell.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class AudioPlaybackService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = AudioPlaybackBinder()

    inner class AudioPlaybackBinder : Binder() {
        fun getService(): AudioPlaybackService = this@AudioPlaybackService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    // Method to start playback
    fun startPlayback(audioPath: String) {
        mediaPlayer.apply {
            reset()
            setDataSource(audioPath)
            prepare()
            start()
        }
    }

    // Method to pause playback
    fun pausePlayback() {
        mediaPlayer.pause()
    }

    // Method to resume playback
    fun resumePlayback() {
        mediaPlayer.start()
    }

    // Method to stop playback
    fun stopPlayback() {
        mediaPlayer.stop()
    }

    // Method to get current playback position
    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    // Method to get total duration of the audio
    fun getTotalDuration(): Int {
        return mediaPlayer.duration
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}
