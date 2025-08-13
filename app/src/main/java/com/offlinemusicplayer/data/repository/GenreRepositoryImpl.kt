package com.offlinemusicplayer.data.repository

import com.offlinemusicplayer.data.database.GenreDao
import com.offlinemusicplayer.data.database.GenreEntity
import com.offlinemusicplayer.domain.model.Genre
import com.offlinemusicplayer.domain.repository.GenreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepositoryImpl @Inject constructor(
    private val genreDao: GenreDao
) : GenreRepository {

    override fun getAllGenres(): Flow<List<Genre>> {
        return genreDao.getAllGenres().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getGenreById(id: Long): Flow<Genre?> {
        return genreDao.getAllGenres().map { entities ->
            entities.find { it.id == id }?.toDomainModel()
        }
    }

    override fun getGenreByName(name: String): Flow<Genre?> {
        return genreDao.getAllGenres().map { entities ->
            entities.find { it.name == name }?.toDomainModel()
        }
    }

    override suspend fun insertGenre(genre: Genre): Long {
        return genreDao.insertGenre(genre.toEntity())
    }

    override suspend fun insertGenres(genres: List<Genre>) {
        genreDao.insertGenres(genres.map { it.toEntity() })
    }

    override suspend fun updateGenre(genre: Genre) {
        genreDao.updateGenre(genre.toEntity())
    }

    override suspend fun deleteGenre(genre: Genre) {
        genreDao.deleteGenre(genre.toEntity())
    }

    private fun GenreEntity.toDomainModel(): Genre {
        return Genre(
            id = id,
            name = name,
            color = color,
            description = description,
            createdAt = createdAt
        )
    }

    private fun Genre.toEntity(): GenreEntity {
        return GenreEntity(
            id = id,
            name = name,
            color = color,
            description = description,
            createdAt = createdAt
        )
    }
}