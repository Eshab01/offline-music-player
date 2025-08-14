package com.offlinemusicplayer.data.repository

import com.offlinemusicplayer.data.database.MusicDao
import com.offlinemusicplayer.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueRepository @Inject constructor(
    private val musicDao: MusicDao,
) {

    // =============== QUEUE OPERATIONS ===============

    fun getAllQueueItems(): Flow<List<QueueItem>> = musicDao.getAllQueueItems()

    suspend fun getAllQueueItemsList(): List<QueueItem> = musicDao.getAllQueueItemsList()

    suspend fun getCurrentlyPlayingQueueItem(): QueueItem? = 
        musicDao.getCurrentlyPlayingQueueItem()

    suspend fun addTrackToQueue(trackId: Long, source: String = "manual") {
        val position = musicDao.getNextQueuePosition()
        val queueItem = QueueItem(
            trackId = trackId,
            position = position,
            source = source
        )
        musicDao.insertQueueItem(queueItem)
    }

    suspend fun addTracksToQueue(trackIds: List<Long>, source: String = "manual") {
        var position = musicDao.getNextQueuePosition()
        val queueItems = trackIds.map { trackId ->
            QueueItem(
                trackId = trackId,
                position = position++,
                source = source
            )
        }
        musicDao.insertQueueItems(queueItems)
    }

    suspend fun insertTrackAtPosition(trackId: Long, position: Int, source: String = "manual") {
        // Shift existing items down
        val existingItems = getAllQueueItemsList()
        val itemsToUpdate = existingItems.filter { it.position >= position }
        
        // Update positions of existing items
        for (item in itemsToUpdate) {
            musicDao.updateQueueItemPosition(item.id, item.position + 1)
        }

        // Insert new item
        val queueItem = QueueItem(
            trackId = trackId,
            position = position,
            source = source
        )
        musicDao.insertQueueItem(queueItem)
    }

    suspend fun removeQueueItem(queueItem: QueueItem) {
        musicDao.deleteQueueItem(queueItem)
        
        // Reorder remaining items
        val remainingItems = getAllQueueItemsList()
            .filter { it.position > queueItem.position }
            .sortedBy { it.position }
        
        remainingItems.forEachIndexed { index, item ->
            musicDao.updateQueueItemPosition(item.id, queueItem.position + index)
        }
    }

    suspend fun clearQueue() = musicDao.clearQueue()

    suspend fun setCurrentlyPlaying(queueItemId: Long) {
        musicDao.clearCurrentlyPlaying()
        musicDao.setCurrentlyPlaying(queueItemId)
    }

    suspend fun moveQueueItem(fromPosition: Int, toPosition: Int) {
        val items = getAllQueueItemsList().sortedBy { it.position }
        val itemToMove = items.find { it.position == fromPosition } ?: return

        when {
            fromPosition < toPosition -> {
                // Moving down - shift items up
                val itemsToShift = items.filter { it.position in (fromPosition + 1)..toPosition }
                itemsToShift.forEach { item ->
                    musicDao.updateQueueItemPosition(item.id, item.position - 1)
                }
                musicDao.updateQueueItemPosition(itemToMove.id, toPosition)
            }
            fromPosition > toPosition -> {
                // Moving up - shift items down
                val itemsToShift = items.filter { it.position in toPosition until fromPosition }
                itemsToShift.forEach { item ->
                    musicDao.updateQueueItemPosition(item.id, item.position + 1)
                }
                musicDao.updateQueueItemPosition(itemToMove.id, toPosition)
            }
        }
    }

    suspend fun reorderQueue(newOrder: List<Long>) {
        newOrder.forEachIndexed { index, queueItemId ->
            musicDao.updateQueueItemPosition(queueItemId, index)
        }
    }

    // =============== QUEUE MANIPULATION ===============

    suspend fun shuffleQueue(preserveCurrentTrack: Boolean = true) {
        val items = getAllQueueItemsList()
        val currentItem = if (preserveCurrentTrack) {
            items.find { it.isCurrentlyPlaying }
        } else null

        val itemsToShuffle = if (currentItem != null) {
            items.filter { !it.isCurrentlyPlaying }
        } else {
            items
        }

        val shuffledItems = itemsToShuffle.shuffled()
        
        var position = 0
        
        // Keep current item at position 0 if preserving
        if (currentItem != null) {
            musicDao.updateQueueItemPosition(currentItem.id, position++)
        }

        // Assign new positions to shuffled items
        shuffledItems.forEach { item ->
            musicDao.updateQueueItemPosition(item.id, position++)
        }
    }

    suspend fun sortQueue(sortBy: QueueSortOption) {
        // This would require joining with tracks table to get sort data
        // For now, just implement basic functionality
        TODO("Implement queue sorting by various criteria")
    }

    suspend fun replaceQueue(trackIds: List<Long>, source: String = "manual") {
        clearQueue()
        addTracksToQueue(trackIds, source)
    }

    suspend fun getNextTrack(): QueueItem? {
        val currentItem = getCurrentlyPlayingQueueItem() ?: return null
        return getAllQueueItemsList()
            .sortedBy { it.position }
            .find { it.position > currentItem.position }
    }

    suspend fun getPreviousTrack(): QueueItem? {
        val currentItem = getCurrentlyPlayingQueueItem() ?: return null
        return getAllQueueItemsList()
            .sortedBy { it.position }
            .lastOrNull { it.position < currentItem.position }
    }

    suspend fun hasNextTrack(): Boolean = getNextTrack() != null

    suspend fun hasPreviousTrack(): Boolean = getPreviousTrack() != null

    suspend fun getQueueSize(): Int = getAllQueueItemsList().size

    suspend fun getQueuePosition(): Int {
        val currentItem = getCurrentlyPlayingQueueItem() ?: return -1
        return currentItem.position
    }
}

enum class QueueSortOption {
    TITLE, ARTIST, ALBUM, DURATION, DATE_ADDED, SHUFFLE
}