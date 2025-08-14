package com.offlinemusicplayer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.offlinemusicplayer.data.database.MusicDao
import com.offlinemusicplayer.data.model.Track
import kotlinx.coroutines.flow.Flow

class MusicRepository(
    private val musicDao: MusicDao,
) {
    fun getAllTracksPaged(): Flow<PagingData<Track>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 5,
                    enablePlaceholders = false,
                ),
            pagingSourceFactory = { musicDao.getAllTracksPaged() },
        ).flow

    fun getAllTracksFlow(): Flow<List<Track>> = musicDao.getAllTracksFlow()

    suspend fun getTrackById(id: Long): Track? = musicDao.getTrackById(id)

    suspend fun getTrackByUri(uri: String): Track? = musicDao.getTrackByUri(uri)

    suspend fun insertTrack(track: Track): Long = musicDao.insertTrack(track)

    suspend fun insertTracks(tracks: List<Track>) = musicDao.insertTracks(tracks)

    suspend fun updateTrack(track: Track) = musicDao.updateTrack(track)

    suspend fun deleteTrack(track: Track) = musicDao.deleteTrack(track)

    suspend fun deleteTrackByUri(uri: String) = musicDao.deleteTrackByUri(uri)

    suspend fun deleteAllTracks() = musicDao.deleteAllTracks()

    suspend fun getTrackCount(): Int = musicDao.getTrackCount()

    fun searchTracks(
        query: String,
        useFts: Boolean,
    ): Flow<PagingData<Track>> {
        val escapedQuery = if (useFts) query else musicDao.escapeForSqlLike(query)

        return Pager(
            config =
                PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 5,
                    enablePlaceholders = false,
                ),
            pagingSourceFactory = {
                if (useFts) {
                    musicDao.searchTracksFts(escapedQuery)
                } else {
                    musicDao.searchTracksLike(escapedQuery)
                }
            },
        ).flow
    }
}
