/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    var currentPosition = -1

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