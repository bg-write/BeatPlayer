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

package com.crrl.beatplayer.alertdialog.views.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.alertdialog.R
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.interfaces.ItemListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class DialogFragmentBase : BottomSheetDialogFragment(), ItemListener {

    protected lateinit var title: String
    protected lateinit var message: String
    protected lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    protected lateinit var itemList: List<AlertItemAction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetAlertTheme)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.parent_dialog_layout, container, false)
    }

    override fun updateItem(view: View, alertItemAction: AlertItemAction) {}
}