package com.offlinemusicplayer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.offlinemusicplayer.data.database.MusicDao
import com.offlinemusicplayer.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
    private val musicDao: MusicDao,
) {
    companion object {
        private val PAGING_CONFIG = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false,
        )
    }

    // =============== GENRE OPERATIONS ===============

    fun getAllGenres(): Flow<List<Genre>> = musicDao.getAllGenres()

    fun getAllGenresPaged(): Flow<PagingData<Genre>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getAllGenresPaged() },
        ).flow

    suspend fun getGenreById(id: Long): Genre? = musicDao.getGenreById(id)

    suspend fun getGenreByName(name: String): Genre? = musicDao.getGenreByName(name)

    suspend fun createGenre(name: String, isCustom: Boolean = true): Long {
        val genre = Genre(
            name = name,
            isCustom = isCustom
        )
        return musicDao.insertGenre(genre)
    }

    suspend fun insertGenre(genre: Genre): Long = musicDao.insertGenre(genre)

    suspend fun insertGenres(genres: List<Genre>) = musicDao.insertGenres(genres)

    suspend fun updateGenre(genre: Genre) {
        musicDao.updateGenre(genre.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteGenre(genre: Genre) = musicDao.deleteGenre(genre)

    suspend fun deleteGenreById(genreId: Long) = musicDao.deleteGenreById(genreId)

    fun getGenresWithTrackCount(): Flow<List<GenreWithTrackCount>> = 
        musicDao.getGenresWithTrackCount()

    // =============== GENRE-TRACK OPERATIONS ===============

    suspend fun getOrCreateGenre(name: String): Genre {
        return getGenreByName(name) ?: run {
            val genreId = createGenre(name, isCustom = false)
            getGenreById(genreId)!!
        }
    }

    suspend fun assignGenreToTrack(trackId: Long, genreName: String) {
        val genre = getOrCreateGenre(genreName)
        musicDao.insertTrackGenre(
            TrackGenre(trackId = trackId, genreId = genre.id)
        )
    }

    suspend fun assignGenreToTracks(trackIds: List<Long>, genreName: String) {
        val genre = getOrCreateGenre(genreName)
        val trackGenres = trackIds.map { trackId ->
            TrackGenre(trackId = trackId, genreId = genre.id)
        }
        musicDao.insertTrackGenres(trackGenres)
    }

    suspend fun removeGenreFromTrack(trackId: Long, genreId: Long) = 
        musicDao.deleteTrackGenre(trackId, genreId)

    suspend fun removeAllGenresFromTrack(trackId: Long) = 
        musicDao.deleteAllTrackGenres(trackId)

    suspend fun getTrackGenres(trackId: Long): List<TrackGenre> = 
        musicDao.getTrackGenres(trackId)

    fun getTracksByGenre(genreId: Long): Flow<PagingData<Track>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getTracksByGenre(genreId) },
        ).flow

    // =============== BATCH OPERATIONS ===============

    suspend fun batchUpdateGenres(
        trackIds: List<Long>,
        addGenres: List<String> = emptyList(),
        removeGenres: List<String> = emptyList()
    ) {
        // Remove specified genres
        if (removeGenres.isNotEmpty()) {
            for (genreName in removeGenres) {
                val genre = getGenreByName(genreName) ?: continue
                for (trackId in trackIds) {
                    removeGenreFromTrack(trackId, genre.id)
                }
            }
        }

        // Add specified genres
        if (addGenres.isNotEmpty()) {
            for (genreName in addGenres) {
                assignGenreToTracks(trackIds, genreName)
            }
        }
    }

    suspend fun replaceTrackGenres(trackId: Long, newGenres: List<String>) {
        // Remove all existing genres for this track
        removeAllGenresFromTrack(trackId)

        // Add new genres
        for (genreName in newGenres) {
            assignGenreToTrack(trackId, genreName)
        }
    }

    // =============== ANALYTICS ===============

    suspend fun getGenreStatistics(): List<GenreWithTrackCount> {
        // This would be implemented with a proper query in a real app
        // For now, using the existing flow but converting to suspend
        TODO("Implement genre statistics")
    }

    suspend fun getMostUsedGenres(limit: Int = 10): List<GenreWithTrackCount> {
        // This would be implemented with a proper query in a real app
        TODO("Implement most used genres query")
    }
}