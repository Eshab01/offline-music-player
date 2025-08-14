package com.offlinemusicplayer.data.database

import androidx.paging.PagingSource
import androidx.room.*
import com.offlinemusicplayer.data.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    
    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun getAllTracksPaged(): PagingSource<Int, Track>

    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun getAllTracksFlow(): Flow<List<Track>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: Long): Track?

    @Query("SELECT * FROM tracks WHERE uri = :uri")
    suspend fun getTrackByUri(uri: String): Track?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<Track>)

    @Update
    suspend fun updateTrack(track: Track)

    @Delete
    suspend fun deleteTrack(track: Track)

    @Query("DELETE FROM tracks WHERE uri = :uri")
    suspend fun deleteTrackByUri(uri: String)

    @Query("DELETE FROM tracks")
    suspend fun deleteAllTracks()

    // Search queries with fallback to LIKE
    @Query("""
        SELECT * FROM tracks 
        WHERE title LIKE '%' || :query || '%' 
           OR artist LIKE '%' || :query || '%' 
           OR album LIKE '%' || :query || '%'
           OR ovTitle LIKE '%' || :query || '%'
           OR ovArtist LIKE '%' || :query || '%'
           OR ovAlbum LIKE '%' || :query || '%'
        ORDER BY title ASC
        LIMIT :limit
    """)
    fun searchTracksLike(query: String, limit: Int = 100): PagingSource<Int, Track>

    // FTS5 search (will be implemented conditionally)
    @Query("""
        SELECT tracks.* FROM tracks_fts 
        JOIN tracks ON tracks.id = tracks_fts.rowid 
        WHERE tracks_fts MATCH :query 
        ORDER BY rank
    """)
    fun searchTracksFts(query: String): PagingSource<Int, Track>

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTrackCount(): Int

    // Helper function to escape LIKE queries
    fun escapeForSqlLike(input: String): String {
        return input.replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
    }
}