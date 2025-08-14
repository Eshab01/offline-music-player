package com.eshab.offlineplayer.util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.eshab.offlineplayer.media.MusicService

fun ensurePlaybackService(context: Context) {
    val intent = Intent(context, MusicService::class.java)
    ContextCompat.startForegroundService(context, intent)
}