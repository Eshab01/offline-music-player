package com.offlinemusicplayer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.offlinemusicplayer.data.database.SettingsDao
import com.offlinemusicplayer.data.model.Setting
import com.offlinemusicplayer.data.model.SettingKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao,
    private val dataStore: DataStore<Preferences>
) {
    
    // DataStore keys for preferences
    companion object {
        val THEME_MODE = stringPreferencesKey(SettingKeys.THEME_MODE)
        val DYNAMIC_COLORS = booleanPreferencesKey(SettingKeys.DYNAMIC_COLORS)
        val OFFLINE_MODE = booleanPreferencesKey(SettingKeys.OFFLINE_MODE)
        val CLOUD_SYNC_ENABLED = booleanPreferencesKey(SettingKeys.CLOUD_SYNC_ENABLED)
        val FIREBASE_CRASHLYTICS = booleanPreferencesKey(SettingKeys.FIREBASE_CRASHLYTICS)
        val SHUFFLE_MODE = booleanPreferencesKey(SettingKeys.SHUFFLE_MODE)
        val REPEAT_MODE = intPreferencesKey(SettingKeys.REPEAT_MODE)
        val EQUALIZER_ENABLED = booleanPreferencesKey(SettingKeys.EQUALIZER_ENABLED)
        val EQUALIZER_PRESET = stringPreferencesKey(SettingKeys.EQUALIZER_PRESET)
        val BASS_BOOST = intPreferencesKey(SettingKeys.BASS_BOOST)
        val LOUDNESS_ENHANCER = intPreferencesKey(SettingKeys.LOUDNESS_ENHANCER)
        val PLAYBACK_SPEED = floatPreferencesKey(SettingKeys.PLAYBACK_SPEED)
        val SKIP_SILENCE = booleanPreferencesKey(SettingKeys.SKIP_SILENCE)
        val CROSSFADE_DURATION = intPreferencesKey(SettingKeys.CROSSFADE_DURATION)
        val AUDIO_FOCUS_DUCK = booleanPreferencesKey(SettingKeys.AUDIO_FOCUS_DUCK)
        val AUTO_PAUSE_HEADPHONES = booleanPreferencesKey(SettingKeys.AUTO_PAUSE_HEADPHONES)
        val FOLDER_SCAN_ENABLED = booleanPreferencesKey(SettingKeys.FOLDER_SCAN_ENABLED)
        val EXCLUDED_FOLDERS = stringSetPreferencesKey(SettingKeys.EXCLUDED_FOLDERS)
        val MINIMUM_TRACK_DURATION = longPreferencesKey(SettingKeys.MINIMUM_TRACK_DURATION)
        val AUTO_SYNC_INTERVAL = longPreferencesKey(SettingKeys.AUTO_SYNC_INTERVAL)
        val WIFI_ONLY_SYNC = booleanPreferencesKey(SettingKeys.WIFI_ONLY_SYNC)
        val BACKUP_METADATA = booleanPreferencesKey(SettingKeys.BACKUP_METADATA)
        val APP_LOCK_ENABLED = booleanPreferencesKey(SettingKeys.APP_LOCK_ENABLED)
        val APP_LOCK_TYPE = stringPreferencesKey(SettingKeys.APP_LOCK_TYPE)
        val LOCK_TIMEOUT = longPreferencesKey(SettingKeys.LOCK_TIMEOUT)
        val MINI_PLAYER_ENABLED = booleanPreferencesKey(SettingKeys.MINI_PLAYER_ENABLED)
        val FLOATING_PLAYER_ENABLED = booleanPreferencesKey(SettingKeys.FLOATING_PLAYER_ENABLED)
        val GESTURES_ENABLED = booleanPreferencesKey(SettingKeys.GESTURES_ENABLED)
        val HIGH_CONTRAST = booleanPreferencesKey(SettingKeys.HIGH_CONTRAST)
        val LARGE_FONT = booleanPreferencesKey(SettingKeys.LARGE_FONT)
        val SCREEN_READER_SUPPORT = booleanPreferencesKey(SettingKeys.SCREEN_READER_SUPPORT)
        val NOTIFICATION_ACTIONS = stringSetPreferencesKey(SettingKeys.NOTIFICATION_ACTIONS)
        val LOCK_SCREEN_CONTROLS = booleanPreferencesKey(SettingKeys.LOCK_SCREEN_CONTROLS)
        val SMART_QUEUE_ENABLED = booleanPreferencesKey(SettingKeys.SMART_QUEUE_ENABLED)
        val MOOD_SUGGESTIONS = booleanPreferencesKey(SettingKeys.MOOD_SUGGESTIONS)
        val ANALYTICS_ENABLED = booleanPreferencesKey(SettingKeys.ANALYTICS_ENABLED)
        val LIBRARY_LAST_SCAN = longPreferencesKey(SettingKeys.LIBRARY_LAST_SCAN)
        val FIRST_RUN_COMPLETED = booleanPreferencesKey(SettingKeys.FIRST_RUN_COMPLETED)
    }

    // =============== THEME SETTINGS ===============

    fun getThemeMode(): Flow<String> = 
        dataStore.data.map { preferences -> preferences[THEME_MODE] ?: "system" }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences -> preferences[THEME_MODE] = mode }
    }

    fun getDynamicColors(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[DYNAMIC_COLORS] ?: true }

    suspend fun setDynamicColors(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[DYNAMIC_COLORS] = enabled }
    }

    // =============== PRIVACY SETTINGS ===============

    fun getOfflineMode(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[OFFLINE_MODE] ?: true }

    suspend fun setOfflineMode(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[OFFLINE_MODE] = enabled }
    }

    fun getCloudSyncEnabled(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[CLOUD_SYNC_ENABLED] ?: false }

    suspend fun setCloudSyncEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[CLOUD_SYNC_ENABLED] = enabled }
    }

    fun getFirebaseCrashlyticsEnabled(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[FIREBASE_CRASHLYTICS] ?: false }

    suspend fun setFirebaseCrashlyticsEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[FIREBASE_CRASHLYTICS] = enabled }
    }

    // =============== PLAYBACK SETTINGS ===============

    fun getShuffleMode(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[SHUFFLE_MODE] ?: false }

    suspend fun setShuffleMode(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[SHUFFLE_MODE] = enabled }
    }

    fun getRepeatMode(): Flow<Int> = 
        dataStore.data.map { preferences -> preferences[REPEAT_MODE] ?: 0 }

    suspend fun setRepeatMode(mode: Int) {
        dataStore.edit { preferences -> preferences[REPEAT_MODE] = mode }
    }

    fun getPlaybackSpeed(): Flow<Float> = 
        dataStore.data.map { preferences -> preferences[PLAYBACK_SPEED] ?: 1.0f }

    suspend fun setPlaybackSpeed(speed: Float) {
        dataStore.edit { preferences -> preferences[PLAYBACK_SPEED] = speed }
    }

    fun getSkipSilence(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[SKIP_SILENCE] ?: false }

    suspend fun setSkipSilence(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[SKIP_SILENCE] = enabled }
    }

    fun getCrossfadeDuration(): Flow<Int> = 
        dataStore.data.map { preferences -> preferences[CROSSFADE_DURATION] ?: 0 }

    suspend fun setCrossfadeDuration(duration: Int) {
        dataStore.edit { preferences -> preferences[CROSSFADE_DURATION] = duration }
    }

    // =============== AUDIO SETTINGS ===============

    fun getEqualizerEnabled(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[EQUALIZER_ENABLED] ?: false }

    suspend fun setEqualizerEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[EQUALIZER_ENABLED] = enabled }
    }

    fun getEqualizerPreset(): Flow<String> = 
        dataStore.data.map { preferences -> preferences[EQUALIZER_PRESET] ?: "flat" }

    suspend fun setEqualizerPreset(preset: String) {
        dataStore.edit { preferences -> preferences[EQUALIZER_PRESET] = preset }
    }

    fun getBassBoost(): Flow<Int> = 
        dataStore.data.map { preferences -> preferences[BASS_BOOST] ?: 0 }

    suspend fun setBassBoost(strength: Int) {
        dataStore.edit { preferences -> preferences[BASS_BOOST] = strength }
    }

    fun getLoudnessEnhancer(): Flow<Int> = 
        dataStore.data.map { preferences -> preferences[LOUDNESS_ENHANCER] ?: 0 }

    suspend fun setLoudnessEnhancer(gain: Int) {
        dataStore.edit { preferences -> preferences[LOUDNESS_ENHANCER] = gain }
    }

    fun getAudioFocusDuck(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[AUDIO_FOCUS_DUCK] ?: true }

    suspend fun setAudioFocusDuck(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[AUDIO_FOCUS_DUCK] = enabled }
    }

    fun getAutoPauseHeadphones(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[AUTO_PAUSE_HEADPHONES] ?: true }

    suspend fun setAutoPauseHeadphones(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[AUTO_PAUSE_HEADPHONES] = enabled }
    }

    // =============== LIBRARY SETTINGS ===============

    fun getFolderScanEnabled(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[FOLDER_SCAN_ENABLED] ?: true }

    suspend fun setFolderScanEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[FOLDER_SCAN_ENABLED] = enabled }
    }

    fun getExcludedFolders(): Flow<Set<String>> = 
        dataStore.data.map { preferences -> preferences[EXCLUDED_FOLDERS] ?: emptySet() }

    suspend fun setExcludedFolders(folders: Set<String>) {
        dataStore.edit { preferences -> preferences[EXCLUDED_FOLDERS] = folders }
    }

    fun getMinimumTrackDuration(): Flow<Long> = 
        dataStore.data.map { preferences -> preferences[MINIMUM_TRACK_DURATION] ?: 30000L }

    suspend fun setMinimumTrackDuration(duration: Long) {
        dataStore.edit { preferences -> preferences[MINIMUM_TRACK_DURATION] = duration }
    }

    fun getLibraryLastScan(): Flow<Long> = 
        dataStore.data.map { preferences -> preferences[LIBRARY_LAST_SCAN] ?: 0L }

    suspend fun setLibraryLastScan(timestamp: Long) {
        dataStore.edit { preferences -> preferences[LIBRARY_LAST_SCAN] = timestamp }
    }

    // =============== SYNC SETTINGS ===============

    fun getAutoSyncInterval(): Flow<Long> = 
        dataStore.data.map { preferences -> preferences[AUTO_SYNC_INTERVAL] ?: 3600000L } // 1 hour

    suspend fun setAutoSyncInterval(interval: Long) {
        dataStore.edit { preferences -> preferences[AUTO_SYNC_INTERVAL] = interval }
    }

    fun getWifiOnlySync(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[WIFI_ONLY_SYNC] ?: true }

    suspend fun setWifiOnlySync(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[WIFI_ONLY_SYNC] = enabled }
    }

    fun getBackupMetadata(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[BACKUP_METADATA] ?: true }

    suspend fun setBackupMetadata(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[BACKUP_METADATA] = enabled }
    }

    // =============== SECURITY SETTINGS ===============

    fun getAppLockEnabled(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[APP_LOCK_ENABLED] ?: false }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[APP_LOCK_ENABLED] = enabled }
    }

    fun getAppLockType(): Flow<String> = 
        dataStore.data.map { preferences -> preferences[APP_LOCK_TYPE] ?: "pin" }

    suspend fun setAppLockType(type: String) {
        dataStore.edit { preferences -> preferences[APP_LOCK_TYPE] = type }
    }

    fun getLockTimeout(): Flow<Long> = 
        dataStore.data.map { preferences -> preferences[LOCK_TIMEOUT] ?: 300000L } // 5 minutes

    suspend fun setLockTimeout(timeout: Long) {
        dataStore.edit { preferences -> preferences[LOCK_TIMEOUT] = timeout }
    }

    // =============== UI SETTINGS ===============

    fun getMiniPlayerEnabled(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[MINI_PLAYER_ENABLED] ?: true }

    suspend fun setMiniPlayerEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[MINI_PLAYER_ENABLED] = enabled }
    }

    fun getFloatingPlayerEnabled(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[FLOATING_PLAYER_ENABLED] ?: false }

    suspend fun setFloatingPlayerEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[FLOATING_PLAYER_ENABLED] = enabled }
    }

    fun getGesturesEnabled(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[GESTURES_ENABLED] ?: true }

    suspend fun setGesturesEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[GESTURES_ENABLED] = enabled }
    }

    // =============== ACCESSIBILITY SETTINGS ===============

    fun getHighContrast(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[HIGH_CONTRAST] ?: false }

    suspend fun setHighContrast(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[HIGH_CONTRAST] = enabled }
    }

    fun getLargeFont(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[LARGE_FONT] ?: false }

    suspend fun setLargeFont(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[LARGE_FONT] = enabled }
    }

    fun getScreenReaderSupport(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[SCREEN_READER_SUPPORT] ?: false }

    suspend fun setScreenReaderSupport(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[SCREEN_READER_SUPPORT] = enabled }
    }

    // =============== NOTIFICATION SETTINGS ===============

    fun getNotificationActions(): Flow<Set<String>> = 
        dataStore.data.map { preferences -> 
            preferences[NOTIFICATION_ACTIONS] ?: setOf("play_pause", "prev", "next") 
        }

    suspend fun setNotificationActions(actions: Set<String>) {
        dataStore.edit { preferences -> preferences[NOTIFICATION_ACTIONS] = actions }
    }

    fun getLockScreenControls(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[LOCK_SCREEN_CONTROLS] ?: true }

    suspend fun setLockScreenControls(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[LOCK_SCREEN_CONTROLS] = enabled }
    }

    // =============== SMART FEATURES ===============

    fun getSmartQueueEnabled(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[SMART_QUEUE_ENABLED] ?: true }

    suspend fun setSmartQueueEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[SMART_QUEUE_ENABLED] = enabled }
    }

    fun getMoodSuggestions(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[MOOD_SUGGESTIONS] ?: true }

    suspend fun setMoodSuggestions(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[MOOD_SUGGESTIONS] = enabled }
    }

    fun getAnalyticsEnabled(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[ANALYTICS_ENABLED] ?: false }

    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[ANALYTICS_ENABLED] = enabled }
    }

    // =============== FIRST RUN ===============

    fun getFirstRunCompleted(): Flow<Boolean> = 
        dataStore.data.map { preferences -> preferences[FIRST_RUN_COMPLETED] ?: false }

    suspend fun setFirstRunCompleted(completed: Boolean) {
        dataStore.edit { preferences -> preferences[FIRST_RUN_COMPLETED] = completed }
    }

    // =============== LEGACY ROOM SETTINGS (for migration) ===============

    suspend fun migrateLegacySettings() {
        // Migrate any existing Room settings to DataStore
        // This would be called once during app initialization
        TODO("Implement migration from Room to DataStore")
    }
}
