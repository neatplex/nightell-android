package com.neatplex.nightell.services

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

class AudioServiceConnection : ServiceConnection {
    private var _service: AudioPlaybackService? = null
    val service: AudioPlaybackService
        get() = _service ?: throw IllegalStateException("Service not bound or still initializing")

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as? AudioPlaybackService.AudioPlaybackBinder
            ?: throw IllegalStateException("Unexpected service type: $service")

        _service = binder.getService()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        _service = null
    }
}