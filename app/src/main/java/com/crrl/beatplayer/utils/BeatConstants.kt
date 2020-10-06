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

import android.net.Uri
import android.provider.MediaStore
import com.crrl.beatplayer.R

object BeatConstants {

    const val SONG_ID_DEFAULT = -7173L
    const val CHANNEL_ID = "beatplayer_channel_id"
    const val NOTIFICATION_ID = 8341

    const val REMOVE_SONG = "action_remove_song"
    const val PREVIOUS = "action_previous"
    const val NEXT = "action_next"
    const val PLAY_PAUSE = "action_play_or_pause"
    const val REPEAT_ONE = "action_repeat_one"
    const val REPEAT_ALL = "action_repeat_all"
    const val PLAY_ALL_SHUFFLED = "action_play_all_shuffled"
    const val UPDATE_QUEUE = "action_update_queue"
    const val SET_MEDIA_STATE = "action_set_media_state"
    const val PLAY_ACTION = "action_play"
    const val PAUSE_ACTION = "action_pause"
    const val PLAY_SONG_FROM_INTENT = "action_play_song_from_intent"

    const val REPEAT_MODE = "repeat_mode"
    const val SHUFFLE_MODE = "shuffle_mode"
    const val BIND_STATE_BOUND = "state_bound"

    const val BIND_STATE_CANCELED = "state_canceled"
    const val LYRIC = "lyrics_fragment"


    const val PLAY_LIST_DETAIL = "play_list_detail"
    const val NOW_PLAYING = "now_playing"
    const val ARTIST_DETAIL = "artist_detail"
    const val FAVORITE_KEY = "favorite_key"

    const val ARTIST_KEY = "artist_key"
    const val ALBUM_KEY = "album_key"
    const val FOLDER_KEY = "folder_key"
    const val SONG_KEY = "song_key"
    const val QUEUE_LIST_TYPE_KEY = "queue_list_type_key"
    const val BY_UI_KEY = "by_ui_key"

    const val LIBRARY = "library_fragment"
    const val SONG_LIST_NAME = "song_list_name"
    const val SEEK_TO_POS = "seek_to_pos"
    const val SEEK_TO = "action_seek_to"
    const val SONG_DETAIL = "song_detail_fragment"
    const val ALBUM_DETAIL = "album_detail_fragment"
    const val LIGHT_THEME = "light_theme"
    const val BLACK_THEME = "black_theme"
    const val DARK_THEME = "dark_theme"
    const val AUTO_THEME = "auto_theme"
    const val FAVORITE_ID = -7440L
    const val FAVORITE_NAME = "${R.string.favorites}"

    const val FAVORITE_TYPE = "Favorite"
    const val ALBUM_TYPE = "Album"
    const val SONG_TYPE = "Song"
    const val ARTIST_TYPE = "Artist"
    const val FOLDER_TYPE = "Folder"
    const val PLAY_LIST_TYPE = "Playlist"

    const val MAX_RANDOM_BUFFER_SIZE = 20

    const val READ_ONLY_MODE = "r"

    val ARTWORK_URI: Uri = Uri.parse("content://media/external/audio/albumart")
    val SONG_URI: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
}