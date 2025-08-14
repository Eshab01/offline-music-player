package com.offlinemusicplayer.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "queue_items",
    indices = [
        Index(value = ["position"]),
        Index(value = ["trackId"])
    ]
)
data class QueueItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackId: Long,
    val position: Int,
    val isCurrentlyPlaying: Boolean = false,
    val addedAt: Long = System.currentTimeMillis(),
    val source: String = "manual" // "manual", "playlist", "album", "shuffle"
)