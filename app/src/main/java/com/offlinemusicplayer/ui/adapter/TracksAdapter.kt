package com.offlinemusicplayer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.offlinemusicplayer.R
import com.offlinemusicplayer.data.model.Track
import com.offlinemusicplayer.databinding.ItemTrackBinding
import java.util.concurrent.TimeUnit

class TracksAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onMoreClick: (Track) -> Unit,
) : PagingDataAdapter<Track, TracksAdapter.TrackViewHolder>(TrackDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrackViewHolder {
        val binding =
            ItemTrackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int,
    ) {
        val track = getItem(position)
        if (track != null) {
            holder.bind(track)
        }
    }

    inner class TrackViewHolder(
        private val binding: ItemTrackBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val track = getItem(position)
                    if (track != null) {
                        onTrackClick(track)
                    }
                }
            }

            binding.moreOptions.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val track = getItem(position)
                    if (track != null) {
                        onMoreClick(track)
                    }
                }
            }
        }

        fun bind(track: Track) {
            binding.apply {
                title.text = track.displayTitle
                artist.text = track.displayArtist
                duration.text = formatDuration(track.duration)

                // Load album art with Coil
                albumArt.load(track.albumArtUri) {
                    crossfade(true)
                    placeholder(R.drawable.ic_music_note)
                    error(R.drawable.ic_music_note)
                    size(192, 192) // Size hint for better performance
                }
            }
        }

        private fun formatDuration(durationMs: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
            return String.format("%d:%02d", minutes, seconds)
        }
    }

    class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(
            oldItem: Track,
            newItem: Track,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Track,
            newItem: Track,
        ): Boolean = oldItem == newItem
    }
}
