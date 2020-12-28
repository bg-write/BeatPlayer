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

package com.crrl.beatplayer.ui.transformers.base

import android.view.View
import com.yarolegovich.discretescrollview.transform.DiscreteScrollItemTransformer

abstract class BaseTransformer : DiscreteScrollItemTransformer {

    protected abstract fun onTransform(view: View, position: Float)

    override fun transformItem(view: View, position: Float) {
        onPreTransform(view, position)
        onTransform(view, position)
        onPostTransform(view, position)
    }

    protected open fun onPreTransform(view: View, position: Float) {
        val width = view.width.toFloat()
        view.rotationX = 0.0f
        view.rotationY = 0.0f
        view.rotation = 0.0f
        view.scaleX = 1.0f
        view.scaleY = 1.0f
        view.pivotX = 0.0f
        view.pivotY = 0.0f
        view.translationY = 0.0f
        view.translationX = if (isPagingEnabled()) 0.0f else -width * position
        if (hideOffscreenPages()) {
            view.alpha = if (position > -1.0f && position < 1.0f) 1.0f else 0.0f
        } else {
            view.alpha = 1.0f
        }
    }

    protected open fun onPostTransform(view: View, position: Float) {}
    protected open fun hideOffscreenPages() = true
    protected open fun isPagingEnabled() = false
}