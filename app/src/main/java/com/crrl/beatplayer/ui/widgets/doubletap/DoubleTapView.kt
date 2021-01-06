package com.crrl.beatplayer.ui.widgets.doubletap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.widget.TextViewCompat
import com.crrl.beatplayer.R
import com.crrl.beatplayer.enums.State
import com.crrl.beatplayer.interfaces.DoubleTapListener
import com.crrl.beatplayer.interfaces.StateListener
import com.crrl.beatplayer.utils.GeneralUtils.addColorOpacity
import com.crrl.beatplayer.utils.SettingsUtility
import kotlinx.android.synthetic.main.double_tap_view.view.*

class DoubleTapView(context: Context, private val attrs: AttributeSet?) :
    ConstraintLayout(context, attrs), DoubleTapListener {

    private var stateListener: StateListener? = null
    private val gestureDetector: GestureDetectorCompat
    private val gestureListener: DoubleTapGestureListener


    var iconColor: Int = Color.WHITE
        set(value) {
            seconds_view.tint = value

            field = value
        }

    var colorOpacity: Float = DEFAULT_COLOR_OPACITY
        set(value) {
            tapCircleColor = addColorOpacity(tapCircleColor, value)
            circleBackgroundColor = addColorOpacity(circleBackgroundColor, value)

            field = value
        }

    val seekSeconds: Int
        get() = SettingsUtility(context).forwardRewindTime / 1000

    var tapCircleColor: Int
        get() = arc_view.circleColor
        private set(value) {
            arc_view.circleColor = value
        }

    var circleBackgroundColor: Int
        get() = arc_view.circleBackgroundColor
        private set(value) {
            arc_view.circleBackgroundColor = value
        }

    var animationDuration: Long
        get() = arc_view.animationDuration
        private set(value) {
            arc_view.animationDuration = value
        }

    var doubleTapDelay: Long = DEFAULT_DOUBLE_TAP_DELAY.toLong()
        set(value) {
            gestureListener.doubleTapDelay = value

            field = value
        }

    var arcSize: Float
        get() = arc_view.arcSize
        internal set(value) {
            arc_view.arcSize = value
        }

    var iconAnimationDuration: Long = DEFAULT_ICON_ANIMATION_DURATION.toLong()
        get() = seconds_view.cycleDuration
        private set(value) {
            seconds_view.cycleDuration = value
            field = value
        }

    @DrawableRes
    var icon: Int = 0
        get() = seconds_view.icon
        private set(value) {
            seconds_view.stop()
            seconds_view.icon = value
            field = value
        }

    @StyleRes
    var textAppearance: Int = 0
        private set(value) {
            TextViewCompat.setTextAppearance(seconds_view.textView, value)
            field = value
        }

    override val secondsTextView: TextView
        get() = seconds_view.textView

    override fun tapCircleColorRes(@ColorRes resId: Int) = apply {
        tapCircleColor = ContextCompat.getColor(context, resId)
    }

    override fun tapCircleColorInt(@ColorInt color: Int) = apply {
        tapCircleColor = color
    }

    override fun circleBackgroundColorRes(@ColorRes resId: Int) = apply {
        circleBackgroundColor = ContextCompat.getColor(context, resId)
    }


    override fun circleBackgroundColorInt(@ColorInt color: Int) = apply {
        circleBackgroundColor = color
    }

    override fun animationDuration(duration: Long) = apply {
        animationDuration = duration
    }

    override fun arcSize(@DimenRes resId: Int) = apply {
        arcSize = context.resources.getDimension(resId)
    }

    override fun arcSize(px: Float) = apply {
        arcSize = px
    }

    override fun iconAnimationDuration(duration: Long) = apply {
        iconAnimationDuration = duration
    }

    override fun icon(@DrawableRes resId: Int) = apply {
        icon = resId
    }

    override fun textAppearance(@StyleRes resId: Int) = apply {
        textAppearance = resId
    }

    override fun keepInDoubleTapMode() {
        gestureListener.keepInDoubleTapMode()
    }

    override fun cancelInDoubleTapMode() {
        gestureListener.cancelInDoubleTapMode()
    }

    override fun setOnActionListener(listener: StateListener) {
        stateListener = listener
    }

    override fun onDoubleTapProgressUp(posX: Float, posY: Float) {
        if (arc_view.visibility != View.VISIBLE) {
            if (posX < width * LEFT_OFFSET || posX > width * RIGHT_OFFSET) {
                arc_view.visibility = VISIBLE
                seconds_view.visibility = View.VISIBLE
                seconds_view.start()
            } else
                return
        }

        when {
            posX < width * LEFT_OFFSET -> {
                if (seconds_view.isForward) {
                    changeConstraints(false)
                    seconds_view.apply {
                        isForward = false
                        seconds = 0
                    }
                }

                arc_view.resetAnimation {
                    arc_view.updatePosition(posX, posY, true)
                }
                rewinding()
            }
            posX > width * RIGHT_OFFSET -> {
                if (!seconds_view.isForward) {
                    changeConstraints(true)
                    seconds_view.apply {
                        isForward = true
                        seconds = 0
                    }
                }
                arc_view.resetAnimation {
                    arc_view.updatePosition(posX, posY, false)
                }
                forwarding()
            }
            else -> {
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(ev)
        return true
    }

    private fun initializeAttributes() {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DoubleTapView, 0, 0)

            animationDuration = a.getInt(
                R.styleable.DoubleTapView_animationDuration, DEFAULT_ANIMATION_DURATION
            ).toLong()

            iconAnimationDuration = a.getInt(
                R.styleable.DoubleTapView_iconAnimationDuration, DEFAULT_ICON_ANIMATION_DURATION
            ).toLong()

            arcSize = a.getDimensionPixelSize(
                R.styleable.DoubleTapView_arcSize,
                context.resources.getDimensionPixelSize(R.dimen.arc_size)
            ).toFloat()

            tapCircleColor = a.getColor(
                R.styleable.DoubleTapView_tapCircleColor,
                ContextCompat.getColor(context, R.color.tap_circle_color)
            )

            circleBackgroundColor = a.getColor(
                R.styleable.DoubleTapView_backgroundCircleColor,
                ContextCompat.getColor(context, R.color.background_circle_color)
            )

            textAppearance = a.getResourceId(
                R.styleable.DoubleTapView_textAppearance,
                R.style.SecondsTextAppearance
            )

            icon = a.getResourceId(
                R.styleable.DoubleTapView_icon,
                R.drawable.ic_play
            )

            iconColor = a.getColor(R.styleable.DoubleTapView_iconColor, Color.WHITE)

            colorOpacity = a.getFloat(R.styleable.DoubleTapView_colorOpacity, DEFAULT_COLOR_OPACITY)

            doubleTapDelay =
                a.getInt(R.styleable.DoubleTapView_doubleTapDelay, DEFAULT_DOUBLE_TAP_DELAY)
                    .toLong()

            a.recycle()

        } else {
            arcSize = context.resources.getDimensionPixelSize(R.dimen.arc_size).toFloat()
            tapCircleColor = ContextCompat.getColor(context, R.color.tap_circle_color)
            circleBackgroundColor = ContextCompat.getColor(context, R.color.background_circle_color)
            animationDuration = DEFAULT_ANIMATION_DURATION.toLong()
            iconAnimationDuration = DEFAULT_ICON_ANIMATION_DURATION.toLong()
            colorOpacity = DEFAULT_COLOR_OPACITY
            textAppearance = R.style.SecondsTextAppearance
            doubleTapDelay = DEFAULT_DOUBLE_TAP_DELAY.toLong()
        }
    }

    private fun forwarding() {
        seconds_view.seconds += seekSeconds
        stateListener?.onStateChanged(State.STATE_FORWARD, seconds_view.seconds)
    }

    private fun rewinding() {
        seconds_view.seconds += seekSeconds
        stateListener?.onStateChanged(State.STATE_REWIND, seconds_view.seconds)
    }

    private fun changeConstraints(forward: Boolean) {
        val constraintSet = ConstraintSet()
        with(constraintSet) {
            clone(root_constraint_layout)
            if (forward) {
                clear(seconds_view.id, ConstraintSet.START)
                connect(
                    seconds_view.id, ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END
                )
            } else {
                clear(seconds_view.id, ConstraintSet.END)
                connect(
                    seconds_view.id, ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START
                )
            }
            seconds_view.start()
            applyTo(root_constraint_layout)
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.double_tap_view, this, true)

        gestureListener = DoubleTapGestureListener(this)
        gestureDetector = GestureDetectorCompat(context, gestureListener)

        initializeAttributes()
        arc_view.visibility = INVISIBLE
        seconds_view.visibility = View.INVISIBLE
        seconds_view.isForward = true
        changeConstraints(true)

        arc_view.performAtEnd = {
            arc_view.visibility = INVISIBLE
            seconds_view.visibility = View.INVISIBLE
            seconds_view.seconds = 0
            seconds_view.stop()
        }
    }

    companion object {
        private const val DEFAULT_ANIMATION_DURATION = 450
        private const val DEFAULT_ICON_ANIMATION_DURATION = 550
        private const val DEFAULT_DOUBLE_TAP_DELAY = 650
        private const val LEFT_OFFSET = 0.3F
        private const val RIGHT_OFFSET = 0.7F
        private const val DEFAULT_COLOR_OPACITY = 0.3F
    }

    private inner class DoubleTapGestureListener(private val rootView: View) :
        GestureDetector.SimpleOnGestureListener() {

        private val mHandler = Handler(Looper.getMainLooper())
        private val mRunnable = Runnable {
            isDoubleTapping = false
            stateListener?.onStateChanged(State.STATE_END, seekSeconds)
        }

        var isDoubleTapping = false
        var doubleTapDelay: Long = DEFAULT_DOUBLE_TAP_DELAY.toLong()

        fun keepInDoubleTapMode() {
            isDoubleTapping = true
            mHandler.removeCallbacks(mRunnable)
            mHandler.postDelayed(mRunnable, doubleTapDelay)
        }

        fun cancelInDoubleTapMode() {
            mHandler.removeCallbacks(mRunnable)
            isDoubleTapping = false
            stateListener?.onStateChanged(State.STATE_END, seekSeconds)
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (isDoubleTapping) {
                onDoubleTapProgressUp(e.x, e.y)
                return true
            }
            return super.onSingleTapUp(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (isDoubleTapping) return true
            return rootView.performClick()
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (!isDoubleTapping) {
                isDoubleTapping = true
                keepInDoubleTapMode()
                stateListener?.onStateChanged(State.STATE_START, seekSeconds)
            }
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            if (e.actionMasked == MotionEvent.ACTION_UP && isDoubleTapping) {
                onDoubleTapProgressUp(e.x, e.y)
                return true
            }
            return super.onDoubleTapEvent(e)
        }
    }
}