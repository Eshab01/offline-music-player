package com.offlinemusicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import com.offlinemusicplayer.data.database.MusicDatabase
import com.offlinemusicplayer.data.repository.MusicRepository

class MusicPlayerApplication : Application() {

    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            MusicDatabase::class.java,
            "music_database"
        ).build()
    }

    val repository by lazy { MusicRepository(database.musicDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "music_playback_channel"
    }
}