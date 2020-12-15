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

package com.crrl.beatplayer.alertdialog

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.enums.AlertType
import com.crrl.beatplayer.alertdialog.enums.AlertType.*
import com.crrl.beatplayer.alertdialog.stylers.base.ItemStyle
import com.crrl.beatplayer.alertdialog.views.BottomSheetDialogAlert
import com.crrl.beatplayer.alertdialog.views.DialogAlert
import com.crrl.beatplayer.alertdialog.views.InputDialog
import com.crrl.beatplayer.alertdialog.views.SongListDialog
import com.crrl.beatplayer.alertdialog.views.base.DialogFragmentBase

class AlertDialog(
    private val title: String = "",
    private val message: String = "",
    private var style: ItemStyle = ItemStyle(),
    private val type: AlertType = BOTTOM_SHEET,
    private val inputText: String = "",
    private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
) {

    private var theme: AlertType? = DIALOG
    private val actions: ArrayList<AlertItemAction> = ArrayList()
    private var alert: DialogFragmentBase? = null

    /**
     * Add Item to AlertDialog
     * If you are using InputDialog, you can only add 2 actions
     * that will appear at the dialog bottom
     * @param item: AlertItemAction
     */
    fun addItem(item: AlertItemAction) {
        actions.add(item)
    }

    /**
     * Receives an Activity (AppCompatActivity), It's is necessary to getContext and show AlertDialog
     * @param activity: AppCompatActivity
     */
    fun show(activity: AppCompatActivity) {
        alert = when (type) {
            BOTTOM_SHEET -> BottomSheetDialogAlert.newInstance(title, message, actions, style)
            DIALOG -> DialogAlert.newInstance(title, message, actions, style)
            INPUT -> InputDialog.newInstance(title, message, actions, style, inputText)
            QUEUE_LIST -> SongListDialog.newInstance(title, message, adapter!!, style)
        }
        alert?.show(activity.supportFragmentManager, alert?.tag)
    }

    /**
     * Set type for alert. Choose between "AlertType.DIALOG" and "AlertType.BOTTOM_SHEET"
     * @param type: AlertType
     */
    fun setType(type: AlertType) {
        this.theme = type
    }

    /**
     * Update all style in the application
     * @param style: AlertType
     */
    fun setStyle(style: AlertItemStyle) {
        this.style = style
    }

    /**
     * Get the style
     * @return style: AlertItemStyle
     */
    fun getStyle(): ItemStyle {
        return this.style
    }
}