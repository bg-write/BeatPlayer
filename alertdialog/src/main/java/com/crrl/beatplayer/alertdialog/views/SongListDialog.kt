package com.crrl.beatplayer.alertdialog.views

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crrl.beatplayer.alertdialog.extensions.setMargins
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.stylers.base.ItemStyle
import com.crrl.beatplayer.alertdialog.utils.ViewUtils
import com.crrl.beatplayer.alertdialog.views.base.DialogFragmentBase
import kotlinx.android.synthetic.main.parent_dialog_layout.view.*

class SongListDialog : DialogFragmentBase() {

    companion object {
        fun newInstance(
                title: String,
                message: String,
                adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                style: ItemStyle
        ): DialogFragmentBase {
            return SongListDialog().apply {
                setArguments(title, message, adapter, style as AlertItemStyle)
            }
        }
    }

    private lateinit var style: AlertItemStyle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        with(view) {
            title.apply {
                if (this@SongListDialog.title.isEmpty()) {
                    visibility = View.GONE
                } else {
                    text = this@SongListDialog.title
                }
                setTextColor(style.textColor)
            }

            sub_title.apply {
                if (message.isEmpty()) {
                    visibility = View.GONE
                } else {
                    text = message
                }
                setTextColor(style.textColor)
            }

            if(!sub_title.isVisible && !title.isVisible){
                title_container.visibility = View.GONE
            }

            scroller.apply {
                background = ViewUtils.drawRoundRectShape(
                        layoutParams.width,
                        layoutParams.height,
                        style.textColor,
                        style.cornerRadius
                )
                visibility = View.VISIBLE
            }

            val background = ViewUtils.drawRoundRectShape(
                    container.layoutParams.width,
                    container.layoutParams.height,
                    style.backgroundColor,
                    style.cornerRadius
            )

            container.background = background
            bottom_container.visibility = View.GONE
            itemScroll.isNestedScrollingEnabled = false

            recycler_view.apply {
                isNestedScrollingEnabled = true
                layoutManager = LinearLayoutManager(context)
                adapter = this@SongListDialog.adapter
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
        }
    }

    fun setArguments(
            title: String,
            message: String,
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            style: AlertItemStyle
    ) {
        this.title = title
        this.message = message
        this.adapter = adapter
        this.style = style
    }
}