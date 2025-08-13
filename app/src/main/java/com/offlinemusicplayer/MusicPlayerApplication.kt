package com.offlinemusicplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MusicPlayerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}