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

package com.crrl.beatplayer.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.MusicVisualizer
import com.crrl.beatplayer.ui.widgets.SimpleCustomSnackbar
import com.crrl.beatplayer.utils.GeneralUtils
import com.github.florent37.kotlin.pleaseanimate.please
import com.google.android.material.tabs.TabLayout
import rm.com.audiowave.AudioWaveView


const val DEFAULT = "com.crrl.beatplayer.DEFAULT"
const val SUCCESS = "com.crrl.beatplayer.SUCCESS"
const val ERROR = "com.crrl.beatplayer.ERROR"
const val CUSTOM = "com.crrl.beatplayer.CUSTOM"

fun <T : ViewDataBinding> ViewGroup.inflateWithBinding(
    @LayoutRes layoutRes: Int,
    attachToRoot: Boolean = false
): T {
    val layoutInflater = LayoutInflater.from(context)
    return DataBindingUtil.inflate(layoutInflater, layoutRes, this, attachToRoot) as T
}

fun View?.show(animated: Boolean = false) {
    val view = this ?: return
    visibility = VISIBLE
    if (animated) {
        please(200, OvershootInterpolator()) {
            animate(view) {
                scale(1f, 1f)
            }
        }.start()
    }
}

fun View?.hide(animated: Boolean = false) {
    val view = this ?: return
    if (animated) {
        please(200, OvershootInterpolator()) {
            animate(view) {
                scale(0f, 0f)
            }
        }.withEndAction { view.visibility = GONE }.start()
    } else view.visibility = GONE
}

fun MusicVisualizer?.hide(animated: Boolean = false) {
    val view = this ?: return
    val duration = if (animated) 250L else 0L
    please(duration, AccelerateDecelerateInterpolator()) {
        animate(view) {
            originalPosition()
        }
    }.start()
}

fun MusicVisualizer?.show(animated: Boolean = false) {
    val view = this ?: return
    val duration = if (animated) 250L else 0L
    please(duration, AccelerateDecelerateInterpolator()) {
        animate(view) {
            this.leftOfItsParent(marginDp = 25f)
        }
    }.start()
}

fun View?.slideRight(animated: Boolean = false) {
    val view = this ?: return
    val duration = if (animated) 250L else 0L
    please(duration, AccelerateDecelerateInterpolator()) {
        animate(view) {
            leftOfItsParent(65f)
        }
    }.start()
}

fun View?.slideLeft(animated: Boolean = false) {
    val view = this ?: return
    val duration = if (animated) 250L else 0L
    please(duration, AccelerateDecelerateInterpolator()) {
        animate(view) {
            originalPosition()
        }
    }.start()
}

fun View?.scaleUp() {
    val view = this ?: return
    elevation = 100f
    please(250, OvershootInterpolator()) {
        animate(view) {
            originalScale()
        }
    }.start()
}

fun View?.scaleDown() {
    val view = this ?: return
    elevation = 0f
    please(250, OvershootInterpolator()) {
        animate(view) {
            scale(0.95f, 0.95f)
        }
    }.start()
}

fun View?.toggleShow(show: Boolean, animated: Boolean = false) {
    if (show) show(animated) else hide(animated)
}

fun View.setPaddings(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    setPadding(
        left ?: paddingLeft,
        top ?: paddingTop,
        right ?: paddingRight,
        bottom ?: paddingBottom
    )
}

fun View?.animateScale(from: Float, to: Float, dur: Long) {
    this ?: return
    val scaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, from, to)
    val scaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, from, to)
    val animatorSet = AnimatorSet().apply {
        interpolator = OvershootInterpolator()
        duration = dur
        playTogether(scaleX, scaleY)
    }
    animatorSet.start()
}

fun View?.snackbar(
    type: String,
    msg: String,
    dur: Int,
    custom: Int = R.drawable.app_icon,
    action: String? = null,
    clickListener: View.OnClickListener? = null
) {
    this ?: return
    val icon = when (type) {
        DEFAULT -> R.drawable.ic_notification
        SUCCESS -> R.drawable.ic_success
        ERROR -> R.drawable.ic_error
        else -> custom
    }

    val color = when (type) {
        DEFAULT -> R.drawable.background_default
        SUCCESS -> R.drawable.background_success
        ERROR -> R.drawable.background_error
        else -> R.drawable.background_default
    }
    SimpleCustomSnackbar.make(this, msg, dur, clickListener, icon, action, color)?.show()
}

internal fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null
    do {
        if (view is CoordinatorLayout) {
            return view
        } else if (view is FrameLayout) {
            if (view.id == android.R.id.content) {
                return view
            } else {
                fallback = view
            }
        }

        if (view != null) {
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    return fallback
}

fun View?.setCustomColor(color: Int, hasBackground: Boolean = false, opacity: Boolean = false) {
    this ?: return
    val cHex = "#${Integer.toHexString(color).replace("ff", "80")}"
    val c = Color.parseColor(if (cHex != "#0") cHex else "#80000000")
    when (this) {
        is ImageButton -> {
            if (hasBackground) {
                this.apply {
                    if (!opacity) {
                        background =
                            context.getDrawable(R.drawable.btn_play_header_background).apply {
                                this?.setTint(color)
                            }
                    } else {
                        imageTintList =
                            ColorStateList.valueOf(GeneralUtils.getBlackWhiteColor(color))
                    }
                }
            } else {
                imageTintList = ColorStateList.valueOf(color)
            }
        }
        is TabLayout -> {
            val default = (context as Activity).getColorByTheme(R.attr.subTitleTextColor)
            setTabTextColors(default, color)
            setSelectedTabIndicatorColor(color)
        }
        is ProgressBar -> {
            backgroundTintList = ColorStateList.valueOf(c)
        }
        is LinearLayout -> {
            background = if (hasBackground) {
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.btn_ripple_with_stroke
                ).apply { this?.setTint(color) }
            } else AppCompatResources.getDrawable(context, R.drawable.btn_play_header_background)
                .apply { this?.setTint(color) }
        }
        is AudioWaveView -> {
            waveColor = color
        }
        is TextView -> if (hasBackground) {
            setTextColor(GeneralUtils.getBlackWhiteColor(color))
        } else {
            setTextColor(if (opacity) c else color)
        }
    }
}

fun RecyclerView.snapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START, smooth: Boolean = true) {
    if(smooth){
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    } else {
        (layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 20)
    }
}