package com.eshab.offlineplayer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.eshab.offlineplayer.data.db.TrackEntity
import com.eshab.offlineplayer.data.repo.TrackRepository
import com.eshab.offlineplayer.work.ScannerWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    app: Application,
    repo: TrackRepository
) : AndroidViewModel(app) {

    val tracks: Flow<PagingData<TrackEntity>> = repo.pagedTracks().cachedIn(viewModelScope)

    fun triggerScan() {
        val req = OneTimeWorkRequestBuilder<ScannerWorker>().build()
        WorkManager.getInstance(getApplication()).enqueueUniqueWork(
            "scan_once",
            ExistingWorkPolicy.KEEP,
            req
        )
    }
}