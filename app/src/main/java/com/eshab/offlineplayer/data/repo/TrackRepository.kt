package com.eshab.offlineplayer.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eshab.offlineplayer.data.db.AppDatabase
import com.eshab.offlineplayer.data.db.TrackEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    private val db: AppDatabase
) {
    fun pagedTracks(pageSize: Int = 30): Flow<PagingData<TrackEntity>> =
        Pager(PagingConfig(pageSize = pageSize, enablePlaceholders = false)) {
            db.trackDao().pagingAll()
        }.flow
}