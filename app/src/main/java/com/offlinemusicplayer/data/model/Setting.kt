package com.offlinemusicplayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Setting(
    @PrimaryKey
    val key: String,
    val value: String,
)

object SettingKeys {
    const val THEME_MODE = "theme_mode" // "light", "dark", "system"
    const val DYNAMIC_COLORS = "dynamic_colors"
    const val OFFLINE_MODE = "offline_mode" 
    const val CLOUD_SYNC_ENABLED = "cloud_sync_enabled"
    const val FIREBASE_CRASHLYTICS = "firebase_crashlytics"
    const val SHUFFLE_MODE = "shuffle_mode"
    const val REPEAT_MODE = "repeat_mode"
    const val EQUALIZER_ENABLED = "equalizer_enabled"
    const val EQUALIZER_PRESET = "equalizer_preset"
    const val BASS_BOOST = "bass_boost"
    const val LOUDNESS_ENHANCER = "loudness_enhancer"
    const val PLAYBACK_SPEED = "playback_speed"
    const val SKIP_SILENCE = "skip_silence"
    const val CROSSFADE_DURATION = "crossfade_duration"
    const val AUDIO_FOCUS_DUCK = "audio_focus_duck"
    const val AUTO_PAUSE_HEADPHONES = "auto_pause_headphones"
    const val FOLDER_SCAN_ENABLED = "folder_scan_enabled"
    const val EXCLUDED_FOLDERS = "excluded_folders"
    const val MINIMUM_TRACK_DURATION = "minimum_track_duration"
    const val AUTO_SYNC_INTERVAL = "auto_sync_interval"
    const val WIFI_ONLY_SYNC = "wifi_only_sync"
    const val BACKUP_METADATA = "backup_metadata"
    const val APP_LOCK_ENABLED = "app_lock_enabled"
    const val APP_LOCK_TYPE = "app_lock_type" // "pin", "biometric"
    const val LOCK_TIMEOUT = "lock_timeout"
    const val MINI_PLAYER_ENABLED = "mini_player_enabled"
    const val FLOATING_PLAYER_ENABLED = "floating_player_enabled"
    const val GESTURES_ENABLED = "gestures_enabled"
    const val HIGH_CONTRAST = "high_contrast"
    const val LARGE_FONT = "large_font"
    const val SCREEN_READER_SUPPORT = "screen_reader_support"
    const val NOTIFICATION_ACTIONS = "notification_actions"
    const val LOCK_SCREEN_CONTROLS = "lock_screen_controls"
    const val SMART_QUEUE_ENABLED = "smart_queue_enabled"
    const val MOOD_SUGGESTIONS = "mood_suggestions"
    const val ANALYTICS_ENABLED = "analytics_enabled"
    const val LIBRARY_LAST_SCAN = "library_last_scan"
    const val FIRST_RUN_COMPLETED = "first_run_completed"
}
