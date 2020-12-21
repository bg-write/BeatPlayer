package com.crrl.beatplayer.ui.transformers

import android.view.View
import com.crrl.beatplayer.ui.transformers.base.BaseTransformer
import kotlin.math.abs

class CubeOutTransformer : BaseTransformer() {
    override fun onTransform(view: View, position: Float) {
        view.pivotX = (if (position < 0) view.width else 0).toFloat()
        view.pivotY = view.height * 0.5f
        view.rotationY = 90f * position

        view.alpha = 1 - abs(position)
    }

    override fun isPagingEnabled() = true
}