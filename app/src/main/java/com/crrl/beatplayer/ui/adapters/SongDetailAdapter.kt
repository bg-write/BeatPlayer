package com.crrl.beatplayer.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.SongDetailItemBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.setAll
import com.crrl.beatplayer.models.Song

class SongDetailAdapter : RecyclerView.Adapter<SongDetailAdapter.SongDetailViewHolder>() {

    val songList = mutableListOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongDetailViewHolder {
        val viewBinding =
            parent.inflateWithBinding<SongDetailItemBinding>(R.layout.song_detail_item)
        return SongDetailViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: SongDetailViewHolder, position: Int) {
        holder.bind(songList[position])
    }

    override fun getItemCount() = songList.size

    fun updateData(songsList: List<Song>) {
        this.songList.setAll(songsList)
        notifyDataSetChanged()
    }

    inner class SongDetailViewHolder(internal val binding: SongDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.apply {
                this.song = song
                executePendingBindings()
            }
        }
    }
}