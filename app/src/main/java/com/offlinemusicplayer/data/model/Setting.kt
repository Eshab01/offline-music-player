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
    const val FTS5_ENABLED = "fts5_enabled"
    const val SHUFFLE_MODE = "shuffle_mode"
    const val REPEAT_MODE = "repeat_mode"
}
