package com.neatplex.nightell.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.neatplex.nightell.service.MediaServiceHandler
import com.neatplex.nightell.service.MediaNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.UnstableApi
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaModule {

    @Provides
    @Singleton
    fun provideAudioAttributes() :AudioAttributes =
        AudioAttributes.Builder().setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(C.USAGE_MEDIA).build()

    @OptIn(androidx.media3.common.util.UnstableApi::class) @Provides
    @Singleton
    @UnstableApi
    fun providePlayer(@ApplicationContext context: Context,
                      audioAttributes: AudioAttributes): ExoPlayer =
        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(DefaultTrackSelector(context))
            .build()

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context,player: ExoPlayer) :
            MediaNotificationManager = MediaNotificationManager(context,player)


    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer) : MediaSession =
        MediaSession.Builder(context, player).build()


    @Provides
    @Singleton
    fun provideServiceHandler(player: ExoPlayer
    ): MediaServiceHandler {
        return MediaServiceHandler(player)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}