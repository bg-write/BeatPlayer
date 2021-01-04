package com.crrl.beatplayer.interfaces

import android.widget.TextView
import androidx.annotation.*
import com.crrl.beatplayer.ui.widgets.doubletap.DoubleTapView

interface DoubleTapListener {
    val secondsTextView: TextView

    fun tapCircleColorRes(@ColorRes resId: Int): DoubleTapView
    fun tapCircleColorInt(@ColorInt color: Int): DoubleTapView
    fun circleBackgroundColorRes(@ColorRes resId: Int): DoubleTapView
    fun circleBackgroundColorInt(@ColorInt color: Int): DoubleTapView
    fun animationDuration(duration: Long): DoubleTapView
    fun arcSize(@DimenRes resId: Int): DoubleTapView
    fun arcSize(px: Float): DoubleTapView
    fun iconAnimationDuration(duration: Long): DoubleTapView
    fun icon(@DrawableRes resId: Int): DoubleTapView
    fun textAppearance(@StyleRes resId: Int): DoubleTapView
    fun keepInDoubleTapMode()
    fun cancelInDoubleTapMode()
    fun setOnActionListener(listener: StateListener)
    fun onDoubleTapProgressUp(posX: Float, posY: Float)
}
