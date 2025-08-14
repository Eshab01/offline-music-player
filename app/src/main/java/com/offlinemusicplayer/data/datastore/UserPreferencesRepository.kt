package com.offlinemusicplayer.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {
    
    companion object {
        val CROSSFADE_ENABLED = booleanPreferencesKey("crossfade_enabled")
        val CROSSFADE_DURATION = intPreferencesKey("crossfade_duration")
        val SKIP_SILENCE = booleanPreferencesKey("skip_silence")
        val EQUALIZER_ENABLED = booleanPreferencesKey("equalizer_enabled")
        val EQUALIZER_PRESET = stringPreferencesKey("equalizer_preset")
        val SHUFFLE_MODE = booleanPreferencesKey("shuffle_mode")
        val REPEAT_MODE = stringPreferencesKey("repeat_mode")
        val SLEEP_TIMER_DURATION = intPreferencesKey("sleep_timer_duration")
        val SLEEP_TIMER_FADE_OUT = booleanPreferencesKey("sleep_timer_fade_out")
    }

    val crossfadeEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[CROSSFADE_ENABLED] ?: false
    }

    val crossfadeDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[CROSSFADE_DURATION] ?: 3000 // 3 seconds in milliseconds
    }

    val skipSilence: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SKIP_SILENCE] ?: false
    }

    val equalizerEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[EQUALIZER_ENABLED] ?: false
    }

    val equalizerPreset: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[EQUALIZER_PRESET] ?: "Normal"
    }

    val shuffleMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SHUFFLE_MODE] ?: false
    }

    val repeatMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[REPEAT_MODE] ?: "OFF"
    }

    val sleepTimerDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SLEEP_TIMER_DURATION] ?: 0 // 0 means disabled
    }

    val sleepTimerFadeOut: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SLEEP_TIMER_FADE_OUT] ?: true
    }

    suspend fun setCrossfadeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CROSSFADE_ENABLED] = enabled
        }
    }

    suspend fun setCrossfadeDuration(durationMs: Int) {
        context.dataStore.edit { preferences ->
            preferences[CROSSFADE_DURATION] = durationMs
        }
    }

    suspend fun setSkipSilence(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SKIP_SILENCE] = enabled
        }
    }

    suspend fun setEqualizerEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[EQUALIZER_ENABLED] = enabled
        }
    }

    suspend fun setEqualizerPreset(preset: String) {
        context.dataStore.edit { preferences ->
            preferences[EQUALIZER_PRESET] = preset
        }
    }

    suspend fun setShuffleMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHUFFLE_MODE] = enabled
        }
    }

    suspend fun setRepeatMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[REPEAT_MODE] = mode
        }
    }

    suspend fun setSleepTimerDuration(durationMs: Int) {
        context.dataStore.edit { preferences ->
            preferences[SLEEP_TIMER_DURATION] = durationMs
        }
    }

    suspend fun setSleepTimerFadeOut(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SLEEP_TIMER_FADE_OUT] = enabled
        }
    }
}