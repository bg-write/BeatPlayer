package com.crrl.beatplayer.ui.transformers

import android.view.View
import com.crrl.beatplayer.ui.transformers.base.BaseTransformer
import kotlin.math.abs

class NormalTransformer : BaseTransformer() {

    override fun onTransform(view: View, position: Float) {
        view.pivotX = view.width / 2.0f
        view.pivotY = view.height / 2.0f
        val scale = 1 - 0.3f * abs(position)
        view.scaleX = scale
        view.scaleY = scale

        view.translationX = -position * view.width / 3.8f

        if (position <= 0) {
            view.rotationY = 15 * abs(position)
        } else if (position <= 1) {
            view.rotationY = -15 * abs(position)
        }
        view.rotationX = 10 * abs(position)
        view.alpha = 1 - abs(position)
    }

    override fun isPagingEnabled() = true
}