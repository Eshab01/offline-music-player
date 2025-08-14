package com.offlinemusicplayer.data.repository

import com.offlinemusicplayer.data.database.SettingsDao
import com.offlinemusicplayer.data.model.Setting
import com.offlinemusicplayer.data.model.SettingKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val settingsDao: SettingsDao,
) {
    suspend fun getFts5Enabled(): Boolean = settingsDao.getSetting(SettingKeys.FTS5_ENABLED)?.value?.toBoolean() ?: true

    fun getFts5EnabledFlow(): Flow<Boolean> =
        settingsDao
            .getSettingFlow(SettingKeys.FTS5_ENABLED)
            .map { it?.value?.toBoolean() ?: true }

    suspend fun setFts5Enabled(enabled: Boolean) {
        settingsDao.setSetting(Setting(SettingKeys.FTS5_ENABLED, enabled.toString()))
    }

    suspend fun getShuffleMode(): Boolean = settingsDao.getSetting(SettingKeys.SHUFFLE_MODE)?.value?.toBoolean() ?: false

    suspend fun setShuffleMode(enabled: Boolean) {
        settingsDao.setSetting(Setting(SettingKeys.SHUFFLE_MODE, enabled.toString()))
    }

    suspend fun getRepeatMode(): Int = settingsDao.getSetting(SettingKeys.REPEAT_MODE)?.value?.toIntOrNull() ?: 0

    suspend fun setRepeatMode(mode: Int) {
        settingsDao.setSetting(Setting(SettingKeys.REPEAT_MODE, mode.toString()))
    }
}
