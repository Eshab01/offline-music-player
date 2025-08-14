package com.offlinemusicplayer.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.offlinemusicplayer.MusicPlayerApplication
import com.offlinemusicplayer.R
import com.offlinemusicplayer.ui.main.MainActivity

class MusicPlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusHandler: AudioFocusHandler

    override fun onCreate() {
        super.onCreate()
        val initialNotification =
            NotificationCompat
                .Builder(this, MusicPlayerApplication.CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_preparing))
                .setSmallIcon(R.drawable.ic_music_note)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build()
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(
                NOTIFICATION_ID,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
            )
        } else {
            @Suppress("DEPRECATION")
            startForeground(NOTIFICATION_ID, initialNotification)
        }

        initializeSessionAndPlayer()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        intent?.action?.let { action ->
            when (action) {
                MediaNotificationHelper.ACTION_PLAY -> player.play()
                MediaNotificationHelper.ACTION_PAUSE -> player.pause()
                MediaNotificationHelper.ACTION_PREVIOUS -> player.seekToPreviousMediaItem()
                MediaNotificationHelper.ACTION_NEXT -> player.seekToNextMediaItem()
                MediaNotificationHelper.ACTION_STOP -> {
                    player.stop()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    private fun initializeSessionAndPlayer() {
        player =
            ExoPlayer.Builder(this).build().apply {
                setAudioAttributes(
                    AudioAttributes
                        .Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    // handleAudioFocus =
                    true,
                )
                setHandleAudioBecomingNoisy(true)
                setWakeMode(C.WAKE_MODE_LOCAL)
            }

        audioManager = getSystemService(AudioManager::class.java)
        audioFocusHandler = AudioFocusHandler(audioManager, player)

        val sessionActivityPendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        mediaSession =
            MediaSession
                .Builder(this, player)
                .setCallback(MediaSessionCallback())
                .setSessionActivity(sessionActivityPendingIntent)
                .build()

        player.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> if (player.playWhenReady) audioFocusHandler.requestAudioFocus()
                        Player.STATE_ENDED, Player.STATE_IDLE -> audioFocusHandler.abandonAudioFocus()
                    }
                    updateNotification()
                }

                override fun onPlayWhenReadyChanged(
                    playWhenReady: Boolean,
                    reason: Int,
                ) {
                    if (playWhenReady) audioFocusHandler.requestAudioFocus() else audioFocusHandler.abandonAudioFocus()
                    updateNotification()
                }

                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int,
                ) {
                    updateNotification()
                }
            },
        )

        updateNotification()
    }

    private fun updateNotification() {
        val session = mediaSession ?: return
        val notification: Notification = MediaNotificationHelper.createNotification(this, session, player)
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            @Suppress("DEPRECATION")
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}
