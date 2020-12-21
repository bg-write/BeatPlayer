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

import com.crrl.beatplayer.models.Artist
import java.util.*

object SortModes {
    class SongModes {
        companion object {
            const val SONG_A_Z = "title"
            const val SONG_Z_A = "$SONG_A_Z DESC"
            const val SONG_DURATION = "duration DESC"
            const val SONG_YEAR = "year"
            const val SONG_LAST_ADDED = "date_added DESC"
            const val SONG_ALBUM = "album"
            const val SONG_ARTIST = "artist"
            const val SONG_TRACK = "track, title_key"
        }
    }

    class AlbumModes {
        companion object {
            const val ALBUM_A_Z = "album"
            const val ALBUM_Z_A = "$ALBUM_A_Z DESC"
            const val ALBUM_YEAR = "minyear DESC"
            const val ALBUM_SONG_COUNT = "numsongs"
            const val ALBUM_ARTIST = "artist"
        }
    }

    class ArtistModes {
        companion object {
            const val ARTIST_A_Z = "artist"
            const val ARTIST_Z_A = "$ARTIST_A_Z DESC"
            const val ARTIST_ALBUM_COUNT = "number_of_albums"
            const val ARTIST_SONG_COUNT = "numsongs"

            fun sortArtistList(artistList: MutableList<Artist>, sortMode: String) {
                when (sortMode) {
                    ARTIST_A_Z -> artistList.sortBy { it.name.toLowerCase(Locale.ROOT) }
                    ARTIST_Z_A -> artistList.sortByDescending { it.name.toLowerCase(Locale.ROOT) }
                    ARTIST_SONG_COUNT -> artistList.sortBy { it.songCount }
                    ARTIST_ALBUM_COUNT -> artistList.sortBy { it.albumCount }
                }
            }
        }
    }
}
