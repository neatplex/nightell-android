package com.neatplex.nightell.service

import android.content.Context
import android.content.Intent
import android.os.Build
import javax.inject.Inject

class ServiceManager @Inject constructor(private val context: Context) {

    private var isServiceRunning = false

    fun startMediaService() {
        if (!isServiceRunning) {
            val intent = Intent(context, MediaService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            isServiceRunning = true
        }
    }

    fun stopMediaService() {
        if (isServiceRunning) {
            context.stopService(Intent(context, MediaService::class.java))
            isServiceRunning = false
        }
    }
}