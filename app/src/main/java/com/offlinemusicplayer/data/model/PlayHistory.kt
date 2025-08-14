package com.offlinemusicplayer.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "play_history",
    foreignKeys = [
        ForeignKey(
            entity = Track::class,
            parentColumns = ["id"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["trackId"]),
        Index(value = ["playedAt"]),
        Index(value = ["isCompleted"])
    ]
)
data class PlayHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackId: Long,
    val playedAt: Long = System.currentTimeMillis(),
    val duration: Long = 0L, // Duration actually played
    val isCompleted: Boolean = false, // Whether track was played to completion
    val playbackSource: String = "manual" // "manual", "shuffle", "playlist", etc.
)