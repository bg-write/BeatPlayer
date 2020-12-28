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

import com.crrl.beatplayer.models.MediaId
import com.crrl.beatplayer.models.QueueInfo
import com.crrl.beatplayer.models.Song
import com.google.gson.Gson

fun String.toSong(): Song {
    return Gson().fromJson(this)
}

fun String.toQueueInfo(): QueueInfo {
    return Gson().fromJson(this)
}

fun String.toQueueList(): LongArray {
    return Gson().fromJson(this)
}

fun String.toMediaId(): MediaId {
    val parts = split("|")
    return if (parts.size > 1)
        MediaId(parts[0].trim(), parts[1].trim(), parts[2].trim())
    else MediaId()
}

fun String.fixName(): String {
    val index = indexOf("(")
    val nameFixed = if (index != -1) {
        substring(0, index)
    } else this
    return nameFixed.trim()
}

fun String.addIfNotEmpty(other: String): String{
    return if(isNotEmpty()) "$this $other" else this
}

fun String.khz(): String{
    return this.toFloatOrNull()?.div(1000)?.toString() ?: ""
}