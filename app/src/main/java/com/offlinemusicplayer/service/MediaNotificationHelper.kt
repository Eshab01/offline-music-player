package com.offlinemusicplayer.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.offlinemusicplayer.MusicPlayerApplication
import com.offlinemusicplayer.R

object MediaNotificationHelper {
    const val ACTION_PLAY = "action_play"
    const val ACTION_PAUSE = "action_pause"
    const val ACTION_PREVIOUS = "action_prev"
    const val ACTION_NEXT = "action_next"
    const val ACTION_STOP = "action_stop"

    fun createNotification(
        context: Context,
        mediaSession: MediaSession,
        player: Player,
    ): Notification {
        val builder = NotificationCompat.Builder(context, MusicPlayerApplication.CHANNEL_ID)

        val mediaStyle =
            MediaStyleNotificationHelper
                .MediaStyle(mediaSession)
                .setShowActionsInCompactView(0, 1, 2)

        val metadata = player.mediaMetadata
        builder.apply {
            setContentTitle(metadata.title ?: context.getString(R.string.app_name))
            setContentText(metadata.artist)
            setSubText(metadata.albumTitle)
            // Disambiguate the overload by casting null to Bitmap?
            setLargeIcon(null as Bitmap?)
            setContentIntent(mediaSession.sessionActivity)
            setDeleteIntent(createStopIntent(context))
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setOnlyAlertOnce(true)
            setStyle(mediaStyle)
            setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            setSmallIcon(R.drawable.ic_music_note)
            priority = NotificationCompat.PRIORITY_LOW
        }

        builder.addAction(createPreviousAction(context))
        builder.addAction(createPlayPauseAction(context, player.isPlaying))
        builder.addAction(createNextAction(context))

        return builder.build()
    }

    private fun createPlayPauseAction(
        context: Context,
        isPlaying: Boolean,
    ): NotificationCompat.Action {
        val icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val title = if (isPlaying) context.getString(R.string.notification_pause) else context.getString(R.string.notification_play)
        val intent =
            Intent(context, MusicPlayerService::class.java).apply {
                action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
            }
        val pendingIntent =
            PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        return NotificationCompat.Action.Builder(icon, title, pendingIntent).build()
    }

    private fun createPreviousAction(context: Context): NotificationCompat.Action {
        val intent = Intent(context, MusicPlayerService::class.java).apply { action = ACTION_PREVIOUS }
        val pendingIntent =
            PendingIntent.getService(
                context,
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        return NotificationCompat.Action
            .Builder(
                R.drawable.ic_skip_previous,
                context.getString(R.string.notification_previous),
                pendingIntent,
            ).build()
    }

    private fun createNextAction(context: Context): NotificationCompat.Action {
        val intent = Intent(context, MusicPlayerService::class.java).apply { action = ACTION_NEXT }
        val pendingIntent =
            PendingIntent.getService(
                context,
                2,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        return NotificationCompat.Action
            .Builder(
                R.drawable.ic_skip_next,
                context.getString(R.string.notification_next),
                pendingIntent,
            ).build()
    }

    private fun createStopIntent(context: Context): PendingIntent {
        val intent = Intent(context, MusicPlayerService::class.java).apply { action = ACTION_STOP }
        return PendingIntent.getService(
            context,
            3,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }
}
