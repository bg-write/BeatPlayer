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

package com.crrl.beatplayer.models

import com.crrl.beatplayer.extensions.addIfNotEmpty
import com.crrl.beatplayer.extensions.fromJson
import com.crrl.beatplayer.extensions.khz
import com.google.gson.Gson
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.tag.FieldKey

data class ExtraInfo(
    val bitRate: String,
    val fileType: String,
    val frequency: String,
    val queuePosition: String
){
    companion object{
        fun fromString(info: String): ExtraInfo{
            return Gson().fromJson(info)
        }

        fun createFromAudioFile(audioFile: AudioFile, queuePosition: String): ExtraInfo {
            return ExtraInfo(
                audioFile.audioHeader.bitRate.addIfNotEmpty("kbps"),
                audioFile.file.extension,
                audioFile.audioHeader.sampleRate.khz().addIfNotEmpty("khz"),
                queuePosition
            )
        }
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
