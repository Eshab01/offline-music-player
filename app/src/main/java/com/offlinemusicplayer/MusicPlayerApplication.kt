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
        Room
            .databaseBuilder(
                applicationContext,
                MusicDatabase::class.java,
                "music_database",
            ).build()
    }

    val repository by lazy { MusicRepository(database.musicDao()) }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            val ch =
                NotificationChannel(
                    CHANNEL_ID,
                    "Playback",
                    NotificationManager.IMPORTANCE_LOW,
                )
            nm.createNotificationChannel(ch)
        }
    }

    companion object {
        const val CHANNEL_ID = "playback"
    }
}
