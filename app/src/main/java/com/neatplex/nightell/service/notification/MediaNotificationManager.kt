package com.neatplex.nightell.service.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.neatplex.nightell.R
import dagger.hilt.android.UnstableApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val player: ExoPlayer
) {

    private var notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    companion object {
        private const val NOTIFICATION_ID = 200
        private const val NOTIFICATION_CHANNEL_NAME = "notification channel 1"
        private const val NOTIFICATION_CHANNEL_ID = "notification channel id 1"
    }

    init {
        createNotificationChannel()
    }

    @UnstableApi
    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ) {
        buildNotification(mediaSession)
        startForegroundNotification(mediaSessionService)
    }

    @OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun buildNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setMediaDescriptionAdapter(
                MediaNotificationAdapter(
                    context = context,
                    pendingIntent = mediaSession.sessionActivity
                )
            )
            .setSmallIconResourceId(R.drawable.baseline_audio_file_48)
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.sessionCompatToken)
                it.setUseFastForwardActionInCompactView(true)
                it.setUseRewindActionInCompactView(true)
                it.setUseNextActionInCompactView(false)
                it.setPriority(NotificationCompat.PRIORITY_LOW)
                it.setPlayer(player)
            }
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundNotification(mediaSessionService: MediaSessionService) {
        val notification =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Media Playback")
                    .setContentText("Playing media")
                    .setSmallIcon(R.drawable.baseline_audio_file_48)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build()
            } else {
                NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Media Playback")
                    .setContentText("Playing media")
                    .setSmallIcon(R.drawable.baseline_audio_file_48)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build()
            }

        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media playback notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}

