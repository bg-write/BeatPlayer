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

        view.translationX = -position * view.width / 3.3f

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

/*
    val coverFront = view.findViewById<LinearLayout>(R.id.cover_front) ?: view

    coverFront.pivotX = coverFront.width / 2.0f
    coverFront.pivotY = coverFront.height / 2.0f
    val scale = 1 - 0.3f * abs(position)
    coverFront.scaleX = scale
    coverFront.scaleY = scale

    coverFront.translationX = position * coverFront.width / 1.3f
    view.translationX = -position * view.width

    if (position <= 0) {
        coverFront.rotationY = 15 * abs(position)
    } else if (position <= 1) {
        coverFront.rotationY = -15 * abs(position)
    }

    coverFront.rotationX = 10 * abs(position)
    view.alpha = 1 - abs(position)
*/