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

package com.crrl.beatplayer.ui.activities

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.AlertDialog
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.enums.AlertItemTheme
import com.crrl.beatplayer.alertdialog.enums.AlertType
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.databinding.ActivitySettingsBinding
import com.crrl.beatplayer.extensions.getColorByTheme
import com.crrl.beatplayer.ui.activities.base.BaseActivity
import com.crrl.beatplayer.utils.BeatConstants
import com.crrl.beatplayer.utils.SettingsUtility
import org.koin.android.ext.android.inject
import timber.log.Timber

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val settingsUtility by inject<SettingsUtility>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        init()
    }

    private fun init() {
        binding.apply {
            extraActionsContainer.setOnClickListener {
                extraActions.isChecked = !extraActions.isChecked
            }
            extraActions.setOnCheckedChangeListener { _, isChecked -> extraActionsChange(isChecked) }

            extraInfoContainer.setOnClickListener { extraInfo.isChecked = !extraInfo.isChecked }
            extraInfo.setOnCheckedChangeListener { _, isChecked -> extraInfoChange(isChecked) }
        }

        binding.let {
            it.settings = settingsUtility

            it.executePendingBindings()
            it.lifecycleOwner = this
        }
    }

    private fun buildSelectionDialog(
        @StringRes title: Int,
        @StringRes msg: Int,
        options: List<AlertItemAction>
    ): AlertDialog {
        val style = AlertItemStyle()
        style.apply {
            textColor = getColorByTheme(R.attr.titleTextColor)
            selectedTextColor = getColorByTheme(R.attr.colorAccent)
            backgroundColor = getColorByTheme(R.attr.colorPrimarySecondary2)
        }

        return AlertDialog(
            getString(title),
            getString(msg),
            style,
            AlertType.BOTTOM_SHEET
        ).apply {
            options.forEach {
                this.addItem(it)
            }
        }
    }

    fun showCoverTransformers(view: View) {
        try {
            val options = listOf(
                AlertItemAction(
                    getString(R.string.normal_cover_transition),
                    settingsUtility.currentItemTransformer == BeatConstants.NORMAL_TRANSFORMER,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.currentItemTransformer = BeatConstants.NORMAL_TRANSFORMER
                },
                AlertItemAction(
                    getString(R.string.depth_page_cover_transition),
                    settingsUtility.currentItemTransformer == BeatConstants.DEPTH_PAGE_TRANSFORMER,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.currentItemTransformer = BeatConstants.DEPTH_PAGE_TRANSFORMER
                },
                AlertItemAction(
                    getString(R.string.cube_cover_transition),
                    settingsUtility.currentItemTransformer == BeatConstants.CUBE_TRANSFORMER,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.currentItemTransformer = BeatConstants.CUBE_TRANSFORMER
                }
            )

            buildSelectionDialog(
                R.string.cover_transition,
                R.string.cover_transition_msg,
                options
            ).show(this)
        } catch (ex: IllegalStateException) {
            Timber.e(ex)
        }
    }

    fun showThemes(view: View) {
        try {
            val options = listOf(
                AlertItemAction(
                    getString(R.string.default_theme),
                    settingsUtility.currentTheme == BeatConstants.AUTO_THEME,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.currentTheme = BeatConstants.AUTO_THEME
                    recreateActivity()
                },
                AlertItemAction(
                    getString(R.string.light_theme),
                    settingsUtility.currentTheme == BeatConstants.LIGHT_THEME,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.currentTheme =
                        BeatConstants.LIGHT_THEME
                    recreateActivity()
                },
                AlertItemAction(
                    getString(R.string.dark_theme),
                    settingsUtility.currentTheme == BeatConstants.DARK_THEME,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.currentTheme = BeatConstants.DARK_THEME
                    recreateActivity()
                },
                AlertItemAction(
                    getString(R.string.black_theme),
                    settingsUtility.currentTheme == BeatConstants.BLACK_THEME,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.currentTheme = BeatConstants.BLACK_THEME
                    recreateActivity()
                }
            )

            buildSelectionDialog(R.string.theme_title, R.string.theme_description, options).show(
                this
            )
        } catch (ex: IllegalStateException) {
            Timber.e(ex)
        }
    }

    private fun extraInfoChange(state: Boolean) {
        settingsUtility.isExtraInfo = state
        // binding.showExtraInfoContainer.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun extraActionsChange(state: Boolean) {
        settingsUtility.isExtraAction = state
        // binding.showExtraActionContainer.visibility = if (state) View.VISIBLE else View.GONE
    }
}
