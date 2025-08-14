package com.offlinemusicplayer.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.offlinemusicplayer.R
import com.offlinemusicplayer.service.MusicPlayerService
import com.offlinemusicplayer.ui.compose.ComposeMainActivity

class MusicWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Create an Intent to launch the main activity
            val mainIntent = Intent(context, ComposeMainActivity::class.java)
            val mainPendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Create intents for playback controls
            val playPauseIntent = Intent(context, MusicPlayerService::class.java).apply {
                action = "com.offlinemusicplayer.PLAY_PAUSE"
            }
            val playPausePendingIntent = PendingIntent.getService(
                context, 1, playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val nextIntent = Intent(context, MusicPlayerService::class.java).apply {
                action = "com.offlinemusicplayer.NEXT"
            }
            val nextPendingIntent = PendingIntent.getService(
                context, 2, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val previousIntent = Intent(context, MusicPlayerService::class.java).apply {
                action = "com.offlinemusicplayer.PREVIOUS"
            }
            val previousPendingIntent = PendingIntent.getService(
                context, 3, previousIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_music_player)
            
            // Set up click handlers
            views.setOnClickPendingIntent(R.id.widget_app_icon, mainPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_track_info, mainPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_play_pause, playPausePendingIntent)
            views.setOnClickPendingIntent(R.id.widget_next, nextPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_previous, previousPendingIntent)

            // Update text content
            views.setTextViewText(R.id.widget_track_title, "No music playing")
            views.setTextViewText(R.id.widget_track_artist, "Tap to open app")
            
            // Set initial play button state
            views.setImageViewResource(R.id.widget_play_pause, R.drawable.ic_play)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateWidgetWithTrackInfo(
            context: Context,
            title: String,
            artist: String,
            isPlaying: Boolean
        ) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                android.content.ComponentName(context, MusicWidgetProvider::class.java)
            )

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_music_player)
                
                views.setTextViewText(R.id.widget_track_title, title)
                views.setTextViewText(R.id.widget_track_artist, artist)
                views.setImageViewResource(
                    R.id.widget_play_pause,
                    if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                )

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}