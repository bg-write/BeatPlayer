package com.crrl.beatplayer.utils

import com.crrl.beatplayer.interfaces.LoadEventController
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.Allocator
import com.google.android.exoplayer2.upstream.DefaultAllocator

class LoadController : LoadControl {
    var eventController: LoadEventController? = null

    override fun onPrepared() {
        eventController?.onPrepared()
    }

    override fun onTracksSelected(
        renderers: Array<Renderer>, trackGroupArray: TrackGroupArray,
        trackSelectionArray: TrackSelectionArray
    ) {}

    override fun onStopped() {}

    override fun onReleased() {}

    override fun getAllocator(): Allocator {
        return DefaultAllocator(true, 20)
    }

    override fun getBackBufferDurationUs(): Long {
        return 0
    }

    override fun retainBackBufferFromKeyframe(): Boolean {
        return true
    }

    override fun shouldContinueLoading(
        playbackPositionUs: Long,
        bufferedDurationUs: Long,
        playbackSpeed: Float
    ): Boolean {
        return true
    }

    override fun shouldStartPlayback(
        bufferedDurationUs: Long, playbackSpeed: Float,
        rebuffering: Boolean
    ): Boolean {
        return true
    }
}