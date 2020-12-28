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