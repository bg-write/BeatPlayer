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

package com.crrl.beatplayer.extensions

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.SongsRepository

fun <T> List<T>?.moveElement(fromIndex: Int, toIndex: Int): List<T> {
    this ?: return emptyList()
    if(this.isEmpty()) return emptyList()
    return toMutableList().apply {
        val deleted = removeAt(fromIndex)
        add(toIndex, deleted)
    }
}

fun <E> MutableList<E>.setAll(list: List<E>) {
    clear()
    addAll(list)
}

fun <T> MutableList<T>.delete(item: T) {
    item ?: throw NullPointerException("The index can't be null.")

    setAll(filterNot { it == item }.optimizeReadOnlyList())
}

internal fun <T> List<T>.optimizeReadOnlyList() = when (size) {
    0 -> emptyList()
    1 -> listOf(this[0])
    else -> this
}

fun <T : MediaItem> Collection<T>.deepEquals(
    other: Collection<T>
) = when {
    size != other.size || size == 0 -> false
    else -> zip(other).all { (a, b) -> a.compare(b) }
}

fun List<MediaItem>?.toIDList(): LongArray {
    return this?.map { it._id }?.toLongArray() ?: LongArray(0)
}

fun List<MediaSessionCompat.QueueItem>?.toIdList(): LongArray {
    return this?.map { it.queueId }?.toLongArray() ?: LongArray(0)
}

fun List<Song?>.toQueue(): List<MediaSessionCompat.QueueItem> {
    return filterNotNull().map {
        MediaSessionCompat.QueueItem(it.toDescription(), it.id)
    }
}

fun List<Song>.toMediaItemList(): List<MediaBrowserCompat.MediaItem> {
    return this.map { it.toMediaItem() }
}

fun LongArray.toQueue(songsRepository: SongsRepository): List<MediaSessionCompat.QueueItem> {
    val songList = songsRepository.getSongsForIds(this)
    songList.keepInOrder(this)?.let {
        return it.toQueue()
    } ?: return songList.toQueue()
}

fun LongArray.toSongList(songsRepository: SongsRepository): List<Song> {
    val songList = songsRepository.getSongsForIds(this)
    songList.keepInOrder(this)?.let {
        return it
    } ?: return songList
}

fun List<Song>.keepInOrder(queue: LongArray): List<Song>? {
    if (size != queue.size) return this
    return if (isNotEmpty() && queue.isNotEmpty()) {
        val keepOrderList = Array(size, init = { Song() })
        forEach {
            keepOrderList[queue.indexOf(it.id)] = it
        }
        keepOrderList.asList()
    } else null
}