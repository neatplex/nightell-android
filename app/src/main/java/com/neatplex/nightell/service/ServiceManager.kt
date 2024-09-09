package com.neatplex.nightell.service

import android.content.Context
import android.content.Intent
import android.os.Build
import com.neatplex.nightell.service.media.MediaService
import com.neatplex.nightell.service.notification.MediaNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ServiceManager @Inject constructor(private val context: Context,
                                         private val mediaNotificationManager: MediaNotificationManager
) {

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> get() = _isServiceRunning

    fun startMediaService() {
        if (!_isServiceRunning.value) {
            val intent = Intent(context, MediaService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            _isServiceRunning.value = true
        }
    }

    fun stopMediaService() {
        if (_isServiceRunning.value) {
            context.stopService(Intent(context, MediaService::class.java))
            _isServiceRunning.value = false
            mediaNotificationManager.cancelNotification()
        }
    }
}