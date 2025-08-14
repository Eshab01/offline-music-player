package com.eshab.offlineplayer.data.db

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY title COLLATE NOCASE ASC")
    fun pagingAll(): PagingSource<Int, TrackEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tracks: List<TrackEntity>): List<Long>

    @Update
    suspend fun update(track: TrackEntity)

    @Query("DELETE FROM tracks WHERE uri NOT IN (:uris)")
    suspend fun deleteMissing(uris: List<String>)
}

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(genre: GenreEntity): Long

    @Query("SELECT * FROM genres ORDER BY name COLLATE NOCASE")
    suspend fun all(): List<GenreEntity>

    @Query("DELETE FROM genres WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface TrackGenreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ref: TrackGenreCrossRef)
}

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playlist: PlaylistEntity): Long

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    suspend fun all(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(cross: PlaylistTrackCrossRef)
}

@Dao
interface PlayHistoryDao {
    @Insert
    suspend fun insert(entry: PlayHistoryEntity)
}