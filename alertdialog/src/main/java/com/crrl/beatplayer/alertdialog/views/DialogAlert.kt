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

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.crrl.beatplayer.alertdialog.R
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.enums.AlertItemTheme
import com.crrl.beatplayer.alertdialog.extensions.addOnWindowFocusChangeListener
import com.crrl.beatplayer.alertdialog.models.Dialog
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.views.base.DialogFragmentBase

class DialogAlert : DialogFragmentBase<AlertItemStyle>() {

    companion object {
        fun newInstance(dialog: Dialog<AlertItemStyle>): DialogAlert {
            return DialogAlert().apply {
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
            showScroller = true
            showBottomControllers = true

            ok.setOnClickListener { onOkClicked(ok) }
            cancel.setOnClickListener { onCancelClicked(cancel) }
        }
    }

    private fun onOkClicked(view: TextView) {
        val item = mDialog.actions[0]
        view.text = item.title

        updateItem(view, item)

        dismiss()
        item.action(item)
    }

    private fun onCancelClicked(view: TextView) {
        val item =
            AlertItemAction(getString(R.string.cancel), false, AlertItemTheme.DEFAULT) {}
        view.text = item.title

        updateItem(view, item)

        dismiss()
        item.root = view
        item.action(item)
    }

    override fun updateItem(view: View, alertItemAction: AlertItemAction) {
        val action = view as Button
        if (context != null) {
            when (alertItemAction.theme) {
                AlertItemTheme.DEFAULT -> {
                    if (alertItemAction.selected) {
                        action.setTextColor(mDialog.style.selectedTextColor)
                    } else {
                        action.setTextColor(mDialog.style.textColor)
                    }
                }
                AlertItemTheme.CANCEL -> {
                    action.setTextColor(mDialog.style.backgroundColor)
                }
                AlertItemTheme.ACCEPT -> {
                    action.setTextColor(mDialog.style.selectedTextColor)
                }
            }
        }
    }
}