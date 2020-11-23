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

package com.crrl.beatplayer.ui.fragments.base

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.AlertDialog
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.enums.AlertItemTheme
import com.crrl.beatplayer.alertdialog.enums.AlertType
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.stylers.InputStyle
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.SelectSongActivity
import com.crrl.beatplayer.ui.fragments.FavoriteDetailFragment
import com.crrl.beatplayer.ui.fragments.PlaylistDetailFragment
import com.crrl.beatplayer.ui.viewmodels.*
import com.crrl.beatplayer.utils.BeatConstants.FAVORITE_ID
import com.crrl.beatplayer.utils.BeatConstants.PLAY_LIST_DETAIL
import com.crrl.beatplayer.utils.BeatConstants.REMOVE_SONG
import com.crrl.beatplayer.utils.BeatConstants.SONG_KEY
import com.crrl.beatplayer.utils.BeatConstants.SONG_TYPE
import com.crrl.beatplayer.utils.GeneralUtils.addZeros
import com.crrl.beatplayer.utils.GeneralUtils.getExtraBundle
import com.crrl.beatplayer.utils.SettingsUtility
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


open class BaseFragment<T : MediaItem> : CoroutineFragment(), ItemClickListener<T> {

    protected lateinit var dialog: AlertDialog
    protected val mainViewModel by inject<MainViewModel>()
    protected val songDetailViewModel by sharedViewModel<SongDetailViewModel>()
    protected var powerMenu: PowerMenu? = null
    protected val settingsUtility by inject<SettingsUtility>()

    private lateinit var currentItemList: List<T>
    private lateinit var currentItem: T
    private var currentParentId = 0L
    private var alertPlaylists: AlertDialog? = null
    private val favoriteViewModel by inject<FavoriteViewModel>()
    private val playlistViewModel by inject<PlaylistViewModel>()
    private val songViewModel by inject<SongViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        powerMenu = initPopUpMenu().setOnMenuItemClickListener(onMenuItemClickListener).build()
    }

    protected open fun buildPlaylistMenu(playlists: List<Playlist>, song: Song) {
        val style = AlertItemStyle().apply {
            textColor = activity?.getColorByTheme(R.attr.titleTextColor)!!
            selectedTextColor = activity?.getColorByTheme(R.attr.colorAccent)!!
            backgroundColor =
                activity?.getColorByTheme(R.attr.colorPrimarySecondary2)!!
        }
        val alert = AlertDialog(
            getString(R.string.playlists),
            getString(R.string.choose_playlist),
            style,
            AlertType.BOTTOM_SHEET
        ).apply {
            addItem(AlertItemAction(getString(R.string.favorites), false) {
                addFavorite()
            })
            playlists.forEach { playlist ->
                addItem(AlertItemAction(playlist.name, false) {
                    addToList(playlist.id, song)
                })
            }
            addItem(
                AlertItemAction(getString(R.string.new_playlist), false, AlertItemTheme.ACCEPT) {
                    createPlaylistDialog(song)
                })
        }
        alertPlaylists = alert
    }

    protected fun createPlaylistDialog(song: Song? = null, inputText: String? = null) {
        val actions = listOf(
            AlertItemAction(
                getString(R.string.cancel),
                false,
                AlertItemTheme.ACCEPT
            ) {},
            AlertItemAction(
                getString(R.string.create_playlist_btn),
                false,
                AlertItemTheme.ACCEPT
            ) { action ->
                val exists = playlistViewModel.exists(action.input)

                if (exists) mainViewModel.binding.mainContainer.snackbar(
                    ERROR,
                    getString(R.string.playlist_name_error),
                    LENGTH_SHORT,
                    action = getString(R.string.retry),
                    clickListener = View.OnClickListener {
                        createPlaylistDialog(song, action.input)
                    }
                ) else addSongs(action.input, song)
            }
        )
        createInputDialog(
            getString(R.string.new_playlist),
            getString(R.string.create_playlist),
            inputText
                ?: "${requireActivity().getString(R.string.playlist)} ${addZeros(playlistViewModel.count + 1)}",
            getString(R.string.input_hint),
            actions
        )
    }

    private fun addFavorite() {
        val added = favoriteViewModel.addToFavorite(
            FAVORITE_ID,
            listOf(currentItem as Song)
        )
        if (added > 0)
            main_container.snackbar(
                SUCCESS, getString(R.string.song_added_success), LENGTH_SHORT
            )
        else
            main_container.snackbar(
                ERROR,
                getString(R.string.song_added_error),
                LENGTH_SHORT,
                action = getString(R.string.retry),
                clickListener = View.OnClickListener {
                    addFavorite()
                })
    }

    protected fun buildDialog(
        titleText: String,
        subTitleText: String,
        actions: List<AlertItemAction>
    ): AlertDialog {
        val style = AlertItemStyle()
        style.apply {
            textColor = requireActivity().getColorByTheme(R.attr.titleTextColor)
            selectedTextColor = requireActivity().getColorByTheme(R.attr.colorAccent)
            backgroundColor = requireActivity().getColorByTheme(R.attr.colorPrimarySecondary2)
            cornerRadius = resources.getDimension(R.dimen.bottom_panel_radius)
        }
        return AlertDialog(
            titleText,
            subTitleText,
            style,
            AlertType.BOTTOM_SHEET
        ).apply {
            for (action in actions) addItem(action)
        }
    }

    protected open fun createInputDialog(
        titleText: String,
        subTitleText: String,
        inputText: String,
        hintText: String,
        actions: List<AlertItemAction>
    ) {
        val style = InputStyle(
            requireActivity().getColorByTheme(R.attr.colorPrimarySecondary2),
            requireActivity().getColorByTheme(R.attr.colorPrimaryOpacity),
            requireActivity().getColorByTheme(R.attr.titleTextColor),
            requireActivity().getColorByTheme(R.attr.bodyTextColor),
            requireActivity().getColorByTheme(R.attr.colorAccent),
            inputText,
            resources.getDimension(R.dimen.bottom_panel_radius)
        )
        AlertDialog(
            titleText,
            subTitleText,
            style,
            AlertType.INPUT,
            hintText
        ).apply {
            for (action in actions) addItem(action)
        }.show(requireActivity() as AppCompatActivity)
    }

    private fun addSongs(name: String, song: Song? = null) {
        if (song != null) {
            val id = playlistViewModel.create(name, listOf(song))
            if (id != -1L) {
                main_container.snackbar(
                    SUCCESS,
                    getString(R.string.playlist_added_success),
                    LENGTH_SHORT
                )
            } else {
                main_container.snackbar(
                    ERROR,
                    getString(R.string.playlist_added_error, name),
                    LENGTH_LONG
                )
            }
        } else {
            val intent = Intent(requireActivity(), SelectSongActivity::class.java).apply {
                putExtra(PLAY_LIST_DETAIL, name)
            }
            requireActivity().startActivityForResult(intent, 1)
        }
    }

    private fun initPopUpMenu(): PowerMenu.Builder {
        return PowerMenu.Builder(context).apply {
            addItem(PowerMenuItem(getString(R.string.play)))
            if (this@BaseFragment !is PlaylistDetailFragment && this@BaseFragment !is FavoriteDetailFragment)
                addItem(PowerMenuItem(getString(R.string.add)))
            addItem(PowerMenuItem(getString(R.string.share)))
            setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            setMenuRadius(resources.getDimension(R.dimen.popupMenuRadius))
            setOnBackgroundClickListener { powerMenu!!.dismiss() }
            setMenuShadow(5f)
            setShowBackground(false)
            setTextColor(requireActivity().getColorByTheme(R.attr.titleTextColor))
            setTextGravity(Gravity.START)
            setSelectedTextColor(requireActivity().getColorByTheme(R.attr.colorAccent))
            setTextSize(16)
            setTextTypeface(
                Typeface.createFromAsset(
                    context!!.assets,
                    "fonts/product_sans_regular.ttf"
                )
            )
            setMenuColor(requireActivity().getColorByTheme(R.attr.colorPrimarySecondary))
            setSelectedMenuColor(requireActivity().getColorByTheme(R.attr.colorPrimarySecondary))
            if (this@BaseFragment is PlaylistDetailFragment || this@BaseFragment is FavoriteDetailFragment)
                addItem(PowerMenuItem(getString(R.string.remove)))
            else {
                addItem(PowerMenuItem(getString(R.string.delete)))
            }
        }

    }

    private fun createConfDialog(song: Song): AlertDialog {
        val style = AlertItemStyle().apply {
            textColor = requireActivity().getColorByTheme(R.attr.titleTextColor)
            selectedTextColor = requireActivity().getColorByTheme(R.attr.colorAccent)
            selectedBackgroundColor = requireActivity().getColorByTheme(R.attr.colorAccentOpacity)
            backgroundColor =
                activity?.getColorByTheme(R.attr.colorPrimarySecondary2)!!
        }

        return AlertDialog(
            getString(R.string.delete_conf_title),
            getString(R.string.delete_conf, song.title),
            style,
            AlertType.DIALOG
        ).apply {
            addItem(AlertItemAction(getString(R.string.delete), false, AlertItemTheme.ACCEPT) {
                deleteItem(song.id)
            })
        }
    }

    private fun deleteItem(id: Long) {
        val resp = songViewModel.delete(longArrayOf(id))
        favoriteViewModel.update(currentParentId, id)
        if (resp > 0) {
            main_container.snackbar(
                SUCCESS,
                getString(R.string.deleted_ok),
                LENGTH_SHORT
            )
            tidyUp(id)
        } else main_container.snackbar(ERROR,
            getString(R.string.deleted_err),
            LENGTH_SHORT,
            action = getString(R.string.retry),
            clickListener = View.OnClickListener {
                deleteItem(id)
            })
    }

    private val onMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, _ ->
        when (position) {
            0 -> {
                val extras = getExtraBundle(currentItemList.toIDList(), SONG_TYPE)
                mainViewModel.mediaItemClicked((currentItem as Song).toMediaItem(), extras)
            }
            1 -> {
                when (this) {
                    is PlaylistDetailFragment, is FavoriteDetailFragment -> shareItem()
                    else -> alertPlaylists?.show(requireActivity() as AppCompatActivity)
                }
            }
            2 -> {
                when (this) {
                    is PlaylistDetailFragment -> removeFromList(binding.playlist!!.id, currentItem)
                    is FavoriteDetailFragment -> favoriteViewModel.remove(
                        FAVORITE_ID,
                        longArrayOf(currentItem._id)
                    )
                    else -> shareItem()
                }
            }
            3 -> {
                createConfDialog(currentItem as Song).show(requireActivity() as AppCompatActivity)
            }
        }
        powerMenu!!.dismiss()
    }

    private fun addToList(playListId: Long, song: Song) {
        val added = playlistViewModel.addToPlaylist(playListId, listOf(song))
        if (added > 0)
            mainViewModel.binding.mainContainer.snackbar(
                SUCCESS,
                getString(R.string.song_added_success),
                LENGTH_SHORT
            )
        else
            mainViewModel.binding.mainContainer.snackbar(
                ERROR,
                getString(R.string.song_added_error),
                LENGTH_SHORT
            )
    }

    fun initNeeded(item: T, itemList: List<T>, currentParentId: Long) {
        currentItem = item
        currentItemList = itemList
        this.currentParentId = currentParentId
    }

    fun shareItem() {
        val song = currentItem as Song
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, Uri.parse(song.path))
            type = "audio/*"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun tidyUp(id: Long) {
        val currentId = songDetailViewModel.currentData.value?.id ?: return
        if (currentId == id)
            mainViewModel.transportControls()?.skipToNext()
        mainViewModel.transportControls()
            ?.sendCustomAction(REMOVE_SONG, bundleOf(SONG_KEY to id))
    }

    open fun onBackPressed(): Boolean {
        powerMenu ?: return true

        return if (powerMenu!!.isShowing) {
            powerMenu!!.dismiss()
            false
        } else {
            true
        }
    }

    protected open fun showSnackBar(
        view: View?,
        resp: Int,
        @StringRes message: Int
    ) {
        val type = if (resp > 0) SUCCESS else ERROR
        val msg = if (resp > 0) message else R.string.op_err

        view.snackbar(
            type,
            getString(msg),
            BaseTransientBottomBar.LENGTH_SHORT
        )
    }

    open fun removeFromList(playListId: Long, item: T?) {}
    override fun onItemClick(view: View, position: Int, item: T) {}
    override fun onShuffleClick(view: View) {}
    override fun onSortClick(view: View) {}
    override fun onPlayAllClick(view: View) {}

    override fun onPopupMenuClick(view: View, position: Int, item: T, itemList: List<T>) {
        initNeeded(item, itemList, currentParentId)
    }
}

