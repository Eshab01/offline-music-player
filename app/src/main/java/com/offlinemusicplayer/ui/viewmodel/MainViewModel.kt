package com.offlinemusicplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.offlinemusicplayer.data.model.Track
import com.offlinemusicplayer.data.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class MainViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _useFtsSearch = MutableStateFlow(true)
    val useFtsSearch: StateFlow<Boolean> = _useFtsSearch.asStateFlow()

    val tracks: Flow<PagingData<Track>> = searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            repository.getAllTracksPaged()
        } else {
            repository.searchTracks(query, useFtsSearch.value)
        }
    }.cachedIn(viewModelScope)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFtsSearchEnabled(enabled: Boolean) {
        _useFtsSearch.value = enabled
    }

    class Factory(
        private val repository: MusicRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}