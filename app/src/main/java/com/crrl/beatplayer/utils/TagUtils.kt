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

import android.content.Context
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.SongsRepositoryImplementation
import com.crrl.beatplayer.utils.BeatConstants.SONG_ID_DEFAULT
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.CannotWriteException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagException
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

object TagUtils {
    fun writeTag(path: String, key: FieldKey, value: String): Boolean {
        try {
            val audioFile = AudioFileIO.read(Objects.requireNonNull(File(path)))

            audioFile.tag.setField(key, value)
            audioFile.commit()
            return true
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
        } catch (ex: CannotWriteException) {
            Timber.e(ex)
        }
        return false
    }

    fun readTagsAsSong(context: Context, path: String): Song {
        try {
            val audioFile = AudioFileIO.read(Objects.requireNonNull(File(path)))

            val song = Song.createFromAudioFile(audioFile)
            val ids =
                SongsRepositoryImplementation(context).getAlbumIdArtistId(song.album, song.artist)
            song.artistId = ids[0]
            song.albumId = ids[1]
            if (song.title.isEmpty() || song.title.isBlank()) {
                return Song(
                    song.id,
                    title = audioFile.file.name,
                    artist = "Unknown",
                    album = "Unknown",
                    duration = song.duration,
                    path = song.path
                )
            }
            return song
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
        } catch (ex: CannotWriteException) {
            Timber.e(ex)
        }
        return Song(
            id = SONG_ID_DEFAULT,
            title = File(path).name,
            artist = "Unknown",
            album = "Unknown",
            path = path
        )
    }
}