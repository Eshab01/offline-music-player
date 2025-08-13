package com.offlinemusicplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinemusicplayer.domain.model.Genre
import com.offlinemusicplayer.domain.usecase.GetAllGenresUseCase
import com.offlinemusicplayer.domain.usecase.CreateGenreUseCase
import com.offlinemusicplayer.domain.usecase.UpdateGenreUseCase
import com.offlinemusicplayer.domain.usecase.DeleteGenreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenresViewModel @Inject constructor(
    private val getAllGenresUseCase: GetAllGenresUseCase,
    private val createGenreUseCase: CreateGenreUseCase,
    private val updateGenreUseCase: UpdateGenreUseCase,
    private val deleteGenreUseCase: DeleteGenreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenresUiState())
    val uiState: StateFlow<GenresUiState> = _uiState.asStateFlow()

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getAllGenresUseCase().collect { genres ->
                _uiState.value = _uiState.value.copy(
                    genres = genres,
                    isLoading = false
                )
            }
        }
    }

    fun createGenre(name: String, color: String? = null, description: String? = null) {
        viewModelScope.launch {
            try {
                val genre = Genre(
                    name = name,
                    color = color,
                    description = description
                )
                createGenreUseCase(genre)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to create genre: ${e.message}"
                )
            }
        }
    }

    fun updateGenre(genre: Genre) {
        viewModelScope.launch {
            try {
                updateGenreUseCase(genre)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update genre: ${e.message}"
                )
            }
        }
    }

    fun deleteGenre(genre: Genre) {
        viewModelScope.launch {
            try {
                deleteGenreUseCase(genre)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete genre: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class GenresUiState(
    val genres: List<Genre> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)