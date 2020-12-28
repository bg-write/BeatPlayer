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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.alertdialog.R
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.extensions.inflateWithBinding
import com.crrl.beatplayer.alertdialog.interfaces.ItemListener
import com.crrl.beatplayer.alertdialog.models.Dialog
import com.crrl.beatplayer.alertdialog.stylers.base.ItemStyle
import com.crrl.beatplayer.alertdialog.utils.ViewUtils
import com.crrl.beatplayer.alertdialog.viewModels.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class DialogFragmentBase<T: ItemStyle> : BottomSheetDialogFragment(), ItemListener {

    protected lateinit var mDialog: Dialog<T>
    protected lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetAlertTheme)
        retainInstance = true
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel.binding = inflater.inflateWithBinding(R.layout.parent_dialog_layout, container, false)
        return mainViewModel.binding.root
    }

    override fun updateItem(view: View, alertItemAction: AlertItemAction) {}

    protected open fun bind(){
        mainViewModel.binding.apply {
            this.dialog = mDialog

            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()

            title.setTextColor(mDialog.style.textColor)
            subTitle.setTextColor(mDialog.style.textColor)

            ok.setTextColor(mDialog.style.textColor)
            cancel.setTextColor(mDialog.style.textColor)

            container.apply {
                background = ViewUtils.drawRoundRectShape(
                    layoutParams.width,
                    layoutParams.height,
                    mDialog.style.backgroundColor,
                    mDialog.style.cornerRadius
                )
            }
        }
    }

    fun setArguments(
        dialog: Dialog<T>
    ) {
        this.mDialog = dialog
    }
}