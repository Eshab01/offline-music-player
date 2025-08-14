package com.offlinemusicplayer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.offlinemusicplayer.R
import com.offlinemusicplayer.data.model.Track
import com.offlinemusicplayer.databinding.ItemQueueTrackBinding
import java.util.concurrent.TimeUnit

class QueueAdapter(
    private val onTrackClick: (Track, Int) -> Unit,
    private val onTrackRemove: (Track, Int) -> Unit,
    private val onTrackMove: (Int, Int) -> Unit,
) : ListAdapter<Track, QueueAdapter.QueueTrackViewHolder>(QueueTrackDiffCallback()) {
    private var currentPlayingIndex = -1

    fun setCurrentPlaying(index: Int) {
        val oldIndex = currentPlayingIndex
        currentPlayingIndex = index

        if (oldIndex != -1) notifyItemChanged(oldIndex)
        if (currentPlayingIndex != -1) notifyItemChanged(currentPlayingIndex)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): QueueTrackViewHolder {
        val binding =
            ItemQueueTrackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return QueueTrackViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: QueueTrackViewHolder,
        position: Int,
    ) {
        val track = getItem(position)
        holder.bind(track, position == currentPlayingIndex)
    }

    inner class QueueTrackViewHolder(
        private val binding: ItemQueueTrackBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTrackClick(getItem(position), position)
                }
            }
        }

        fun bind(
            track: Track,
            isCurrentlyPlaying: Boolean,
        ) {
            binding.apply {
                title.text = track.displayTitle
                artist.text = track.displayArtist
                duration.text = formatDuration(track.duration)

                // Show playing indicator
                playingIndicator.visibility =
                    if (isCurrentlyPlaying) {
                        android.view.View.VISIBLE
                    } else {
                        android.view.View.GONE
                    }

                // Load album art
                albumArt.load(track.albumArtUri) {
                    crossfade(true)
                    placeholder(R.drawable.ic_music_note)
                    error(R.drawable.ic_music_note)
                    size(96, 96)
                }

                // Update styling for current track
                val context = root.context
                if (isCurrentlyPlaying) {
                    title.setTextColor(context.getColor(R.color.md_theme_primary))
                    artist.setTextColor(context.getColor(R.color.md_theme_primary))
                } else {
                    title.setTextColor(context.getColor(R.color.md_theme_on_surface))
                    artist.setTextColor(context.getColor(R.color.md_theme_on_surface_variant))
                }
            }
        }

        private fun formatDuration(durationMs: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
            return String.format("%d:%02d", minutes, seconds)
        }
    }

    class QueueTrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(
            oldItem: Track,
            newItem: Track,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Track,
            newItem: Track,
        ): Boolean = oldItem == newItem
    }

    // ItemTouchHelper for swipe-to-remove and drag-to-reorder
    fun getItemTouchHelper(): ItemTouchHelper {
        return ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder,
                ): Boolean {
                    val fromPosition = viewHolder.bindingAdapterPosition
                    val toPosition = target.bindingAdapterPosition

                    if (fromPosition != RecyclerView.NO_POSITION && toPosition != RecyclerView.NO_POSITION) {
                        onTrackMove(fromPosition, toPosition)
                        return true
                    }
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int,
                ) {
                    val position = viewHolder.bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val track = getItem(position)
                        onTrackRemove(track, position)
                    }
                }

                override fun isLongPressDragEnabled(): Boolean = true

                override fun isItemViewSwipeEnabled(): Boolean = true
            },
        )
    }
}
