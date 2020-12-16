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

package com.crrl.beatplayer.playback.players

import android.app.Application
import android.net.Uri
import android.os.PowerManager
import com.crrl.beatplayer.alias.OnCompletion
import com.crrl.beatplayer.alias.OnError
import com.crrl.beatplayer.alias.OnPrepared
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.util.PriorityTaskManager
import java.io.File

interface AudioPlayer {
    fun play()
    fun setSource(uri: Uri? = null, path: String? = null): Boolean
    fun prepare()
    fun seekTo(position: Int)
    fun isPrepared(): Boolean
    fun isPlaying(): Boolean
    fun position(): Int
    fun pause()
    fun stop()
    fun release()
    fun onPrepared(prepared: OnPrepared<AudioPlayer>)
    fun onError(error: OnError<AudioPlayer>)
    fun onCompletion(completion: OnCompletion<AudioPlayer>)
}

class AudioPlayerImplementation(
    internal val context: Application
) : AudioPlayer,
    Player.EventListener {

    private var playerBase: ExoPlayer? = null
    private val player: ExoPlayer
        get() {
            if (playerBase == null) {
                playerBase = createPlayer(this)
            }
            return playerBase ?: throw IllegalStateException("Could not create an audio player")
        }

    private var isPrepared = false
    private var onPrepared: OnPrepared<AudioPlayer> = {}
    private var onError: OnError<AudioPlayer> = {}
    private var onCompletion: OnCompletion<AudioPlayer> = {}

    override fun play() {
        player.play()
    }

    override fun setSource(uri: Uri?, path: String?): Boolean {
        return try {
            uri?.let {
                player.setMediaItem(MediaItem.fromUri(it))
            }
            path?.let {
                player.setMediaItem(MediaItem.fromUri(Uri.fromFile(File(it))))
            }
            true
        } catch (ex: Exception) {
            onError(this, ex)
            false
        }
    }

    override fun prepare() {
        player.prepare()
    }

    override fun seekTo(position: Int) {
        player.seekTo(position.toLong())
    }

    override fun isPrepared() = isPrepared

    override fun isPlaying() = player.isPlaying

    override fun position() = player.currentPosition.toInt()

    override fun pause() {
        player.pause()
    }

    override fun stop() {
        player.stop()
    }

    override fun release() {
        player.release()
    }

    override fun onPrepared(prepared: OnPrepared<AudioPlayer>) {
        this.onPrepared = prepared
    }

    override fun onError(error: OnError<AudioPlayer>) {
        this.onError = error
    }

    override fun onCompletion(completion: OnCompletion<AudioPlayer>) {
        this.onCompletion = completion
    }

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        if (state == Player.STATE_ENDED) {
            onCompletion(this)
        }
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)
        if (!isLoading) {
            isPrepared = true
            onPrepared(this)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        isPrepared = false
        onError(this, error)
    }

    private fun createPlayer(owner: AudioPlayerImplementation): ExoPlayer {
        return SimpleExoPlayer.Builder(context)
            .build().apply {
                setWakeMode(PowerManager.PARTIAL_WAKE_LOCK)
                val attr = com.google.android.exoplayer2.audio.AudioAttributes.Builder().apply {
                    setContentType(C.CONTENT_TYPE_MUSIC)
                    setUsage(C.USAGE_MEDIA)
                }.build()
                setAudioAttributes(attr, true)
                setHandleAudioBecomingNoisy(true)
                setForegroundMode(true)
                setPriorityTaskManager(PriorityTaskManager())
                val seekParams = SeekParameters(0, 0)
                setSeekParameters(seekParams)
                addListener(owner)
            }
    }
}