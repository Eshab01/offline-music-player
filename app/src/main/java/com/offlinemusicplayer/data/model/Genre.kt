package com.offlinemusicplayer.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "genres",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class Genre(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)