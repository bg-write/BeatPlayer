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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crrl.beatplayer.alertdialog.extensions.setMargins
import com.crrl.beatplayer.alertdialog.models.Dialog
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.stylers.base.ItemStyle
import com.crrl.beatplayer.alertdialog.utils.ViewUtils
import com.crrl.beatplayer.alertdialog.views.base.DialogFragmentBase
import kotlinx.android.synthetic.main.parent_dialog_layout.view.*

class SongListDialog : DialogFragmentBase<AlertItemStyle>() {

    companion object {
        fun newInstance(dialog: Dialog<AlertItemStyle>): SongListDialog {
            return SongListDialog().apply {
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

            itemScroll.isNestedScrollingEnabled = false
        }

        mainViewModel.binding.recyclerView.apply {
            isNestedScrollingEnabled = true
            layoutManager = LinearLayoutManager(context)
            adapter = mDialog.adapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }
}