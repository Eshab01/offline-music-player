package com.offlinemusicplayer.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "track_moods",
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
        Index(value = ["mood"]),
        Index(value = ["trackId", "mood"], unique = true)
    ]
)
data class TrackMood(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackId: Long,
    val mood: String, // "happy", "sad", "energetic", "calm", etc.
    val addedAt: Long = System.currentTimeMillis()
)