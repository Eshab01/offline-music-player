package com.eshab.offlineplayer.media

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibrarySession
import androidx.media3.session.MediaSession
import com.eshab.offlineplayer.R
import com.eshab.offlineplayer.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val CHANNEL_ID = "playback"

@AndroidEntryPoint
@UnstableApi
class MusicService : MediaLibraryService() {

    @Inject lateinit var exoPlayer: ExoPlayer
    private var session: MediaLibrarySession? = null

    override fun onCreate() {
        super.onCreate()
        val callback = object : MediaLibrarySession.Callback {}

        session = MediaLibrarySession.Builder(this, exoPlayer, callback).build()
        setListener(object : Listener {})
        createNotificationChannelIfNeeded()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? = session

    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_music)
            .setContentTitle(getString(R.string.app_name_fallback))
            .setContentIntent(pi)
            .setOngoing(session.player.isPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(session.sessionCompatToken))
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        val notification = builder.build()
        if (startInForegroundRequired) {
            startForeground(1001, notification)
        } else {
            NotificationManagerCompat.from(this).notify(1001, notification)
        }
        return notification
    }

    override fun onDestroy() {
        session?.release()
        session = null
        super.onDestroy()
    }

    private fun createNotificationChannelIfNeeded() {
        // TODO: create channel for API 26+
    }
}