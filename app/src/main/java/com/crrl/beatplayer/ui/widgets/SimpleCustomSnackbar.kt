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

package com.crrl.beatplayer.ui.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.findSuitableParent
import com.google.android.material.snackbar.BaseTransientBottomBar
import timber.log.Timber

class SimpleCustomSnackbar(
    parent: ViewGroup,
    content: SimpleCustomSnackbarView
) : BaseTransientBottomBar<SimpleCustomSnackbar>(parent, content, content) {


    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {

        fun make(
            view: View,
            message: String, @BaseTransientBottomBar.Duration duration : Int,
            listener: View.OnClickListener?, icon: Int, action: String?, backgroundId: Int
        ): SimpleCustomSnackbar? {
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )
            return try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.layout_simple_custom_snackbar,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                customView.bind(message, listener, icon, action, backgroundId)

                SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }

    }

}