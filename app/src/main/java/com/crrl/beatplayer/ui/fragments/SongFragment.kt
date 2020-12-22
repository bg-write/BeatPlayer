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

package com.crrl.beatplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.enums.AlertItemTheme
import com.crrl.beatplayer.databinding.FragmentSongBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.MediaItemData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.adapters.SongAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.PlaylistViewModel
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.AutoClearBinding
import com.crrl.beatplayer.utils.BeatConstants.PLAY_ALL_SHUFFLED
import com.crrl.beatplayer.utils.GeneralUtils.getExtraBundle
import com.crrl.beatplayer.utils.SortModes
import kotlinx.android.synthetic.main.layout_recyclerview.*
import org.koin.android.ext.android.inject

class SongFragment : BaseFragment<Song>() {

    private lateinit var songAdapter: SongAdapter
    private var binding by AutoClearBinding<FragmentSongBinding>(this)

    private val playlistViewModel by inject<PlaylistViewModel>()
    private val viewModel by inject<SongViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_song, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        songAdapter = SongAdapter(this).apply {
            showHeader = true
            itemClickListener = this@SongFragment
        }

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = songAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        viewModel.getSongList()
            .filter { !songAdapter.songList.deepEquals(it) }
            .observe(viewLifecycleOwner) {
                songAdapter.updateDataSet(it)
                if (songAdapter.songList.isNotEmpty())
                    mainViewModel.reloadQueueIds(it.toIDList(), getString(R.string.all_songs))
            }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }

        createDialog()
    }

    private fun createDialog() {
        dialog = buildDialog(
            getString(R.string.sort_title),
            getString(R.string.sort_msg),
            listOf(
                AlertItemAction(
                    context!!.getString(R.string.sort_az),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_A_Z,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_A_Z
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_za),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_Z_A,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_Z_A
                },
                AlertItemAction(
                    context!!.getString(R.string.album),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_ALBUM,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_ALBUM
                },
                AlertItemAction(
                    context!!.getString(R.string.artist),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_ARTIST,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_ARTIST
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_duration),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_DURATION,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_DURATION
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_year),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_YEAR,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_YEAR
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_last_added),
                    settingsUtility.songSortOrder == SortModes.SongModes.SONG_LAST_ADDED,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.songSortOrder = SortModes.SongModes.SONG_LAST_ADDED
                }
            ))
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.all_songs))
        mainViewModel.mediaItemClicked(item.toMediaItem(), extras)
    }

    override fun onShuffleClick(view: View) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.all_songs))
        mainViewModel.transportControls()?.sendCustomAction(PLAY_ALL_SHUFFLED, extras)
    }

    override fun onSortClick(view: View) {
        dialog.show(activity as AppCompatActivity)
    }

    override fun onPlayAllClick(view: View) {
        if (songAdapter.songList.isEmpty()) return
        val extras = getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.all_songs))
        mainViewModel.mediaItemClicked(songAdapter.songList.first().toMediaItem(), extras)
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song, itemList: List<Song>) {
        super.onPopupMenuClick(view, position, item, itemList)
        powerMenu!!.showAsAnchorRightTop(view)
        playlistViewModel.playLists().observe(viewLifecycleOwner) {
            buildPlaylistMenu(it, item)
        }
    }
}