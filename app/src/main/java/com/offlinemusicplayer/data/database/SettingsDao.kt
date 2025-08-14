package com.offlinemusicplayer.data.database

import androidx.room.*
import com.offlinemusicplayer.data.model.Setting
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    
    @Query("SELECT * FROM settings WHERE key = :key")
    suspend fun getSetting(key: String): Setting?

    @Query("SELECT * FROM settings WHERE key = :key")
    fun getSettingFlow(key: String): Flow<Setting?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: Setting)

    @Query("DELETE FROM settings WHERE key = :key")
    suspend fun deleteSetting(key: String)

    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<Setting>>
}