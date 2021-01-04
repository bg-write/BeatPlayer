package com.crrl.beatplayer.interfaces

import com.crrl.beatplayer.enums.State

interface StateListener {
    fun onStateChanged(state: State, seconds: Int)
}