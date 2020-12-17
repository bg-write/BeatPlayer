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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.crrl.beatplayer.R
import java.util.*

class MusicVisualizer : View {

    private var random = Random()
    private val chunks = mutableListOf<Chunk>()
    var round = false
        set(value) {
            if (value) paint.strokeCap = Paint.Cap.ROUND
            else paint.strokeCap = Paint.Cap.BUTT
            field = value
        }
    var chunkSize = 4.dp()
        set(value) {
            paint.strokeWidth = value
            field = value
        }

    var alignType = 0
    var chunkCount = 5
    var chunkSpace = 1.dp()

    private var paint = Paint()
    private val animateView = object : Runnable {
        override fun run() {
            postDelayed(this, 120)
            invalidate()
        }
    }

    constructor(context: Context) : super(context) {
        MusicVisualizer(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val att = context.obtainStyledAttributes(attrs, R.styleable.MusicVisualizer)
        paint.color =
            att.getColor(R.styleable.MusicVisualizer_tint, context.getColor(R.color.colorPrimary))

        chunkCount = att.getInt(R.styleable.MusicVisualizer_chunkCount, 6)
        chunkSpace = att.getDimension(R.styleable.MusicVisualizer_chunkSpace, 1.dp())
        chunkSize = att.getDimension(R.styleable.MusicVisualizer_chunkSize, 4.dp())
        round = att.getBoolean(R.styleable.MusicVisualizer_chunkRound, false)
        alignType = att.getInteger(R.styleable.MusicVisualizer_alignType, 0)

        initChunks()
        removeCallbacks(animateView)
        post(animateView)
        att.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.style = Paint.Style.FILL

        when (alignType) {
            0 -> drawShapeAlignCenter(canvas)
            1 -> drawShapeAlignBottom(canvas)
        }
    }

    private fun initChunks() {
        for (i in 1 until chunkCount + 1) {
            if (i == 1) {
                chunks.add(Chunk(2 * chunkSpace, chunkSize + (2 * chunkSpace)))
            } else {
                val last = chunks.last()
                chunks.add(
                    Chunk(
                        last.size + chunkSpace,
                        last.size + chunkSpace + chunkSize
                    )
                )
            }
        }

    }

    private fun Int.dp(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        )
    }

    private fun drawShapeAlignCenter(canvas: Canvas) {
        val verticalCenter = height / 2f
        for (i in chunks.indices) {
            val rm = (40 + random.nextInt((height / 2f).toInt() - 25)).toFloat()
            val chunk = chunks[i]

            canvas.drawLine(
                chunk.x,
                verticalCenter - rm / 2,
                chunk.x,
                verticalCenter + rm / 2,
                paint
            )
        }
    }

    private fun drawShapeAlignBottom(canvas: Canvas) {
        for (i in chunks.indices) {
            val chunk = chunks[i]
            canvas.drawLine(
                chunk.x,
                (height - (40 + random.nextInt((height / 2f).toInt() - 25))).toFloat(),
                chunk.x,
                (height - 15).toFloat(),
                paint
            )
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == VISIBLE) {
            removeCallbacks(animateView)
            post(animateView)
        } else if (visibility == GONE) {
            removeCallbacks(animateView)
        }
    }

    fun setTint(color: Int) {
        paint.color = color
    }


}

data class Chunk(val x: Float, val size: Float)

