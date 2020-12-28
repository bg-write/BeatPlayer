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

package com.crrl.beatplayer.alertdialog.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.crrl.beatplayer.alertdialog.R
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.enums.AlertItemTheme
import com.crrl.beatplayer.alertdialog.extensions.addOnWindowFocusChangeListener
import com.crrl.beatplayer.alertdialog.extensions.setMargins
import com.crrl.beatplayer.alertdialog.models.Dialog
import com.crrl.beatplayer.alertdialog.stylers.InputStyle
import com.crrl.beatplayer.alertdialog.utils.ViewUtils.dip2px
import com.crrl.beatplayer.alertdialog.utils.ViewUtils.drawRoundRectShape
import com.crrl.beatplayer.alertdialog.views.base.DialogFragmentBase
import kotlinx.android.synthetic.main.input_dialog_item.*
import kotlinx.android.synthetic.main.input_dialog_item.view.*
import kotlinx.android.synthetic.main.parent_dialog_layout.view.*

class InputDialog : DialogFragmentBase<InputStyle>() {

    companion object {
        fun newInstance(dialog: Dialog<InputStyle>): InputDialog {
            return InputDialog().apply {
                setArguments(dialog)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
        addOnWindowFocusChangeListener {
            if (!it) dismiss()
        }
    }

    override fun bind() {
        super.bind()
        mainViewModel.binding.apply {
            this.showScroller = false
            this.showBottomControllers = true

            inflateActionsView(itemContainer)

            itemScroll.apply {
                setMargins(left = dip2px(context, 12), right = dip2px(context, 12))
                setBackgroundResource(R.drawable.search_text_view_frame)
                clipToOutline = true
            }

            ok.setOnClickListener { onOkClicked(ok) }
            cancel.setOnClickListener { onCancelClicked(cancel) }
        }
    }

    private fun onOkClicked(view: TextView) {
        val action = mDialog.actions[1]
        view.text = action.title

        updateItem(view, action)

        action.input = text.text.toString()

        dismiss()

        action.action(action)
    }

    private fun onCancelClicked(view: TextView) {
        val action = mDialog.actions[0]
        view.text = action.title

        updateItem(view, action)

        action.input = text.text.toString()

        dismiss()

        action.root = view
        action.action(action)
    }

    @SuppressLint("InflateParams")
    private fun inflateActionsView(actionsLayout: LinearLayout) {
        val view = LayoutInflater.from(context).inflate(R.layout.input_dialog_item, null).apply {
            text.apply {
                hint = mDialog.inputText
                setTextColor(mDialog.style.textColor)
                setHintTextColor(mDialog.style.hintTextColor)
                background = drawRoundRectShape(
                    layoutParams.width,
                    layoutParams.height,
                    mDialog.style.inputColor
                )
                requestFocus()
                setText(mDialog.style.text)
                selectAll()
            }
        }
        actionsLayout.addView(view)
        dialog?.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    override fun updateItem(view: View, alertItemAction: AlertItemAction) {
        val action = view as Button

        context ?: return

        when (alertItemAction.theme) {
            AlertItemTheme.DEFAULT -> {
                action.setTextColor(mDialog.style.hintTextColor)
            }
            AlertItemTheme.CANCEL -> {
                action.setTextColor(context!!.getColor(R.color.red))
            }
            AlertItemTheme.ACCEPT -> {
                action.setTextColor(mDialog.style.acceptColor)
            }
        }
    }
}
