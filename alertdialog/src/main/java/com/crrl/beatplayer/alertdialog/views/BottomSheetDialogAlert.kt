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
import android.widget.Button
import android.widget.LinearLayout
import com.crrl.beatplayer.alertdialog.R
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.enums.AlertItemTheme
import com.crrl.beatplayer.alertdialog.models.Dialog
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.views.base.DialogFragmentBase

class BottomSheetDialogAlert : DialogFragmentBase<AlertItemStyle>() {

    companion object {
        fun newInstance(dialog: Dialog<AlertItemStyle>): BottomSheetDialogAlert {
            return BottomSheetDialogAlert().apply {
                setArguments(dialog)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    override fun bind() {
        super.bind()
        mainViewModel.binding.apply {
            this.showScroller = true
            this.showBottomControllers = false

            inflateActionsView(itemContainer, mDialog.actions)
        }
    }

    @SuppressLint("InflateParams")
    private fun inflateActionsView(actionsLayout: LinearLayout, items: List<AlertItemAction>) {
        for (item in items) {

            val view = LayoutInflater.from(context).inflate(R.layout.dialog_item, null)
            val action = view.findViewById<Button>(R.id.action)
            val indicator = view.findViewById<View>(R.id.indicator)

            action.apply {
                text = item.title
                if (items.indexOf(item) == items.size - 1)
                    setBackgroundResource(R.drawable.item_ripple_bottom)
            }

            action.setOnClickListener {
                dismiss()

                val oldState = item.selected

                item.root = view
                item.action.invoke(item)

                if (oldState != item.selected) {
                    cleanSelection(items, item)
                    updateItem(view, item)
                }
            }

            updateItem(view, item)
            indicator.setBackgroundColor(mDialog.style.textColor)
            actionsLayout.addView(view)
        }
    }

    /**
     * This method clears the selection states for each item in the array.
     * @param items: java.util.ArrayList<AlertItemAction> All the items that will be modified
     * @param currentItem: AlertItemAction to save current item state
     */
    private fun cleanSelection(items: List<AlertItemAction>, currentItem: AlertItemAction) {
        for (item in items) {
            if (item != currentItem) item.selected = false
        }
    }

    override fun updateItem(view: View, alertItemAction: AlertItemAction) {
        val action = view.findViewById<Button>(R.id.action)

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