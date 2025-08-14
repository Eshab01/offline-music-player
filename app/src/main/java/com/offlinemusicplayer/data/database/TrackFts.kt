package com.offlinemusicplayer.data.model

import androidx.room.Entity
import androidx.room.Fts4

// Virtual FTS table that mirrors searchable fields from Track.
// Room will keep this table in sync with Track via contentEntity mapping.
@Fts4(contentEntity = Track::class)
@Entity(tableName = "tracks_fts")
data class TrackFts(
    val title: String,
    val artist: String?,
    val album: String?,
    val genre: String?,
    val ovTitle: String?,
    val ovArtist: String?,
    val ovAlbum: String?,
    val ovGenre: String?,
)
