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
import android.support.v4.media.session.PlaybackStateCompat
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.delete
import com.crrl.beatplayer.extensions.moveElement
import com.crrl.beatplayer.extensions.position
import com.crrl.beatplayer.extensions.toQueue
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.SongsRepository
import com.crrl.beatplayer.utils.BeatConstants.MAX_RANDOM_BUFFER_SIZE
import kotlin.random.Random

interface QueueUtils {
    var currentSongId: Long
    var queue: LongArray
    var queueTitle: String

    var currentSong: Song
    val previousSongId: Long?
    val nextSongIndex: Int?
    val nextSongId: Long?

    fun setMediaSession(session: MediaSessionCompat)
    fun playNext(id: Long)
    fun remove(id: Long)
    fun swap(from: Int, to: Int)
    fun queue(): String
    fun clear()
    fun clearPreviousRandomIndexes()
}

class QueueUtilsImplementation(
    private val context: Application,
    private val songsRepository: SongsRepository
) : QueueUtils {

    private lateinit var mediaSession: MediaSessionCompat
    private val previousRandomIndexes = mutableListOf<Int>()

    private val currentSongIndex
        get() = queue.indexOf(currentSongId)

    override var currentSongId: Long = -1

    override var queue: LongArray = longArrayOf()
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                mediaSession.setQueue(value.toQueue(songsRepository))
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
            val controller = mediaSession.controller

            return when {
                controller.shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                    getPreviousRandomIndex()
                }
                previousIndex >= 0 -> {
                    queue[previousIndex]
                }
                else -> null
            }
        }

    override val nextSongIndex: Int?
        get() {
            val nextIndex = currentSongIndex + 1
            val controller = mediaSession.controller
            return when {
                controller.shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                    val index = getRandomIndex()
                    if (index >= 0) index else null
                }
                nextIndex < queue.size -> nextIndex
                else -> null
            }
        }

    override val nextSongId: Long?
        get() {
            val nxtIdx = nextSongIndex
            return if (nxtIdx != null) queue[nxtIdx] else null
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

    override fun clearPreviousRandomIndexes() {
        previousRandomIndexes.clear()
    }

    private fun getPreviousRandomIndex(): Long {
        return if (previousRandomIndexes.size > 1) {
            previousRandomIndexes.removeLast()
            queue[previousRandomIndexes.last()]
        } else currentSongId
    }

    private fun getRandomIndex(): Int {
        if (queue.isEmpty()) return -1
        if (queue.size == 1) return 0

        val randomIndex = Random.nextInt(0, queue.size - 1)

        if (previousRandomIndexes.contains(randomIndex)) {
            return getRandomIndex()
        }

        if (previousRandomIndexes.isEmpty()) previousRandomIndexes.add(currentSongIndex)
        previousRandomIndexes.add(randomIndex)

        if (previousRandomIndexes.size > MAX_RANDOM_BUFFER_SIZE) {
            previousRandomIndexes.removeAt(0)
        }

        return randomIndex
    }
}