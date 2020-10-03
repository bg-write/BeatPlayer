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

import com.crrl.beatplayer.models.MediaItemData
import com.crrl.beatplayer.repository.SongsRepository
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagException
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

object LyricsHelper {
    fun getEmbeddedLyrics(
        songsRepository: SongsRepository,
        mediaItemData: MediaItemData,
        isSync: Boolean = true
    ): String? {
        val lyrics = StringBuilder()
        val file = File(Objects.requireNonNull(songsRepository.getPath(mediaItemData.id)))
        try {
            val tag = if (isSync) FieldKey.CUSTOM1 else FieldKey.LYRICS
            val audioFile = AudioFileIO.read(file)

            lyrics.append(audioFile.tag.getFirst(tag))
        } catch (ex: CannotReadException) {
            Timber.e(ex)
        } catch (ex: IOException) {
            Timber.e(ex)
        } catch (ex: TagException) {
            Timber.e(ex)
        } catch (ex: ReadOnlyFileException) {
            Timber.e(ex)
        } catch (ex: InvalidAudioFrameException) {
            Timber.e(ex)
        } catch (ex: IllegalArgumentException) {
            Timber.e(ex)
        }
        if (isSync && lyrics.toString().isEmpty()) return getEmbeddedLyrics(
            songsRepository,
            mediaItemData,
            false
        )
        return if (lyrics.toString().isEmpty()) null else lyrics.toString()
    }

    fun setEmbeddedLyrics(
        songsRepository: SongsRepository,
        id: Long,
        lyrics: String,
        isSync: Boolean = false
    ): Boolean {
        val key = if (isSync) FieldKey.CUSTOM1 else FieldKey.LYRICS
        return TagUtils.writeTag(songsRepository.getPath(id), key, lyrics)
    }
}