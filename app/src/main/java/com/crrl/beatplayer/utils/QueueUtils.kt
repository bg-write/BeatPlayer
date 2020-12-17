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

package com.crrl.beatplayer.utils

import android.app.Application
import android.support.v4.media.session.MediaSessionCompat
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.SongsRepository
import com.google.gson.Gson

interface QueueUtils {
    var currentSongId: Long
    var queue: LongArray
    var queueTitle: String

    var currentSong: Song
    val previousSongId: Long?
    val nextSongId: Long?

    fun setMediaSession(session: MediaSessionCompat)
    fun playNext(id: Long)
    fun remove(id: Long)
    fun swap(from: Int, to: Int)
    fun queue(): String
    fun clear()
    fun clearPlayedSongs()
    fun shuffleQueue(isShuffle: Boolean = false)
}

class QueueUtilsImplementation(
    private val context: Application,
    private val songsRepository: SongsRepository,
    private val settingsUtility: SettingsUtility
) : QueueUtils {

    private lateinit var mediaSession: MediaSessionCompat
    private val playedSongs = mutableListOf<Int>()
    private val auxQueue = mutableListOf<Long>()

    private val currentSongIndex
        get() = queue.indexOf(currentSongId)

    override var currentSongId: Long = -1

    override var queue: LongArray = longArrayOf()
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                mediaSession.setQueue(value.toQueue(songsRepository))
                auxQueue.setAll(settingsUtility.originalQueueList.toQueueList().toMutableList())
            }
        }

    override var queueTitle: String = ""
        set(value) {
            field = if (value.isNotEmpty()) {
                value
            } else context.getString(R.string.all_songs)

            mediaSession.setQueueTitle(value)
        }

    override var currentSong: Song = Song()
        get() = if (field.id != currentSongId) songsRepository.getSongForId(currentSongId) else field

    override val previousSongId: Long?
        get() {
            if (mediaSession.position() >= 5000) return currentSongId
            val previousIndex = currentSongIndex - 1

            return when {
                previousIndex >= 0 -> {
                    queue[previousIndex]
                }
                else -> null
            }
        }

    override val nextSongId: Long?
        get() {
            val nextIndex = currentSongIndex + 1
            return when {
                nextIndex < queue.size -> queue[nextIndex]
                else -> null
            }
        }

    override fun setMediaSession(session: MediaSessionCompat) {
        mediaSession = session
    }

    override fun playNext(id: Long) {
        val nextIndex = currentSongIndex + 1
        swap(queue.indexOf(id), nextIndex)
    }

    override fun remove(id: Long) {
        queue = queue.toMutableList().apply { delete(id) }.toLongArray()
    }

    override fun swap(from: Int, to: Int) {
        queue = queue.toMutableList().moveElement(from, to).toLongArray()
    }

    override fun queue(): String {
        return "${currentSongIndex + 1}/${queue.size}"
    }

    override fun clear() {
        queue = longArrayOf()
        queueTitle = ""
        currentSongId = 0
    }

    override fun clearPlayedSongs() {
        playedSongs.clear()
    }

    override fun shuffleQueue(isShuffle: Boolean) {
        if (isShuffle)
            mediaSession.setQueue(shuffleQueue())
        else
            restoreQueueOrder()
    }

    private fun shuffleQueue(): List<MediaSessionCompat.QueueItem> {
        val sQueue = mediaSession.controller.queue.shuffled()
        val realQueue = sQueue.moveElement(sQueue.indexOfFirst { it.queueId == currentSongId }, 0)

        auxQueue.setAll(queue.toList())
        settingsUtility.originalQueueList = Gson().toJson(auxQueue)
        queue = realQueue.toIdList()

        return realQueue
    }

    private fun restoreQueueOrder() {
        queue = auxQueue.toLongArray()
        settingsUtility.originalQueueList = "[]"
        auxQueue.clear()
    }
}