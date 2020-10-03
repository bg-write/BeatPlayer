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

package com.crrl.beatplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.SongsRepository
import com.crrl.beatplayer.ui.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class SongViewModel(
    private val songsRepository: SongsRepository
) : CoroutineViewModel(Main) {

    private val songsData = MutableLiveData<List<Song>>().apply { value = mutableListOf() }
    private val songsSelected =
        MutableLiveData<MutableList<Song>>().apply { value = mutableListOf() }

    fun update() {
        launch {
            val songs = withContext(IO) {
                songsRepository.loadSongs()
            }
            if (songs.isNotEmpty()) songsData.postValue(songs)
        }
    }

    fun update(song: MutableList<Song>) {
        songsSelected.postValue(song)
    }

    fun getSongList(): LiveData<List<Song>> {
        update()
        return songsData
    }

    fun selectedSongs(): LiveData<MutableList<Song>> {
        return songsSelected
    }

    fun delete(ids: LongArray): Int {
        return songsRepository.deleteTracks(ids)
    }

    fun getSongById(id: Long): Song {
        return songsRepository.getSongForId(id)
    }
}