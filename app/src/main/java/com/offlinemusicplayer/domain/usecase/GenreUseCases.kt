package com.offlinemusicplayer.domain.usecase

import com.offlinemusicplayer.domain.model.Genre
import com.offlinemusicplayer.domain.repository.GenreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllGenresUseCase @Inject constructor(
    private val genreRepository: GenreRepository
) {
    operator fun invoke(): Flow<List<Genre>> {
        return genreRepository.getAllGenres()
    }
}

class CreateGenreUseCase @Inject constructor(
    private val genreRepository: GenreRepository
) {
    suspend operator fun invoke(genre: Genre): Long {
        return genreRepository.insertGenre(genre)
    }
}

class UpdateGenreUseCase @Inject constructor(
    private val genreRepository: GenreRepository
) {
    suspend operator fun invoke(genre: Genre) {
        genreRepository.updateGenre(genre)
    }
}

class DeleteGenreUseCase @Inject constructor(
    private val genreRepository: GenreRepository
) {
    suspend operator fun invoke(genre: Genre) {
        genreRepository.deleteGenre(genre)
    }
}