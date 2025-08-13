package com.offlinemusicplayer.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun getAllGenres(): Flow<List<GenreEntity>>

    @Query("SELECT * FROM genres WHERE id = :id")
    suspend fun getGenreById(id: Long): GenreEntity?

    @Query("SELECT * FROM genres WHERE name = :name")
    suspend fun getGenreByName(name: String): GenreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: GenreEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Update
    suspend fun updateGenre(genre: GenreEntity)

    @Delete
    suspend fun deleteGenre(genre: GenreEntity)
}