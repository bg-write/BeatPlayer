package com.crrl.beatplayer.ui.widgets.doubletap.views

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.crrl.beatplayer.R
import kotlinx.android.synthetic.main.second_view.view.*

class SecondsView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    var tint: Int = Color.WHITE
        set(value) {
            icon_1.imageTintList = ColorStateList.valueOf(value)
            icon_2.imageTintList = ColorStateList.valueOf(value)
            icon_3.imageTintList = ColorStateList.valueOf(value)

            field = value
        }

    var cycleDuration: Long = 750L
        set(value) {
            firstAnimator.duration = value / 5
            secondAnimator.duration = value / 5
            thirdAnimator.duration = value / 5
            fourthAnimator.duration = value / 5
            fifthAnimator.duration = value / 5
            field = value
        }

    var seconds: Int = 0
        set(value) {
            forward_text.text = context.resources.getQuantityString(
                R.plurals.number_of_seconds, value, value
            )
            field = value
        }

    var isForward: Boolean = true
        set(value) {
            forward_container.rotation = if (value) 0f else 180f
            field = value
        }

    val textView: TextView
        get() = forward_text

    @DrawableRes
    var icon: Int = R.drawable.ic_play
        set(value) {
            if (value > 0) {
                icon_1.setImageResource(value)
                icon_2.setImageResource(value)
                icon_3.setImageResource(value)
            }
            field = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.second_view, this, true)
    }

    fun start() {
        stop()
        firstAnimator.start()
    }

    fun stop() {
        firstAnimator.cancel()
        secondAnimator.cancel()
        thirdAnimator.cancel()
        fourthAnimator.cancel()
        fifthAnimator.cancel()
        reset()
    }

    fun update() {
        visibility = GONE
    }

    private fun reset() {
        icon_1.alpha = 0f
        icon_2.alpha = 0f
        icon_3.alpha = 0f
    }

    private val firstAnimator: ValueAnimator = CustomValueAnimator(
        {
            icon_1.alpha = 0f
            icon_2.alpha = 0f
            icon_3.alpha = 0f
        }, {
            icon_1.alpha = it
        }, {
            secondAnimator.start()
        }
    )

    private val secondAnimator: ValueAnimator = CustomValueAnimator(
        {
            icon_1.alpha = 1f
            icon_2.alpha = 0f
            icon_3.alpha = 0f
        }, {
            icon_2.alpha = it
        }, {
            thirdAnimator.start()
        }
    )

    private val thirdAnimator: ValueAnimator = CustomValueAnimator(
        {
            icon_1.alpha = 1f
            icon_2.alpha = 1f
            icon_3.alpha = 0f
        }, {
            icon_1.alpha =
                1f - icon_3.alpha
            icon_3.alpha = it
        }, {
            fourthAnimator.start()
        }
    )

    private val fourthAnimator: ValueAnimator = CustomValueAnimator(
        {
            icon_1.alpha = 0f
            icon_2.alpha = 1f
            icon_3.alpha = 1f
        }, {
            icon_2.alpha = 1f - it
        }, {
            fifthAnimator.start()
        }
    )

    private val fifthAnimator: ValueAnimator = CustomValueAnimator(
        {
            icon_1.alpha = 0f
            icon_2.alpha = 0f
            icon_3.alpha = 1f
        }, {
            icon_3.alpha = 1f - it
        }, {
            firstAnimator.start()
        }
    )

    private inner class CustomValueAnimator(
        start: () -> Unit, update: (value: Float) -> Unit, end: () -> Unit
    ) : ValueAnimator() {

        init {
            duration = cycleDuration / 5
            setFloatValues(0f, 1f)

            addUpdateListener { update(it.animatedValue as Float) }
            doOnStart { start() }
            doOnEnd { end() }
        }
    }
}