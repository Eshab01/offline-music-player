package com.offlinemusicplayer.domain.repository

import com.offlinemusicplayer.domain.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    fun getAllGenres(): Flow<List<Genre>>
    fun getGenreById(id: Long): Flow<Genre?>
    fun getGenreByName(name: String): Flow<Genre?>
    suspend fun insertGenre(genre: Genre): Long
    suspend fun insertGenres(genres: List<Genre>)
    suspend fun updateGenre(genre: Genre)
    suspend fun deleteGenre(genre: Genre)
}