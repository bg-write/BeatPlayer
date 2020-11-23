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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.ActivitySelectSongBinding
import com.crrl.beatplayer.extensions.delete
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.base.BaseActivity
import com.crrl.beatplayer.ui.adapters.SelectSongAdapter
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.BeatConstants.PLAY_LIST_DETAIL
import com.google.gson.Gson
import org.koin.android.ext.android.inject


class SelectSongActivity : BaseActivity(), ItemClickListener<Song> {

    private lateinit var songAdapter: SelectSongAdapter
    private lateinit var binding: ActivitySelectSongBinding

    private val songViewModel by inject<SongViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        postponeEnterTransition()
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_song)
        songAdapter = SelectSongAdapter(this@SelectSongActivity)

        binding.songList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = songAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        songViewModel.getSongList().observe(this) { list ->
            songAdapter.updateDataSet(list.toMutableList())
            songViewModel.update(mutableListOf())

            (binding.root.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        binding.let {
            it.viewModel = songViewModel
            it.executePendingBindings()

            it.lifecycleOwner = this
        }
    }

    private fun toggleSelect(position: Int, item: Song, select: Boolean) {
        songAdapter.songList[position] = item.apply { isSelected = select }
        songAdapter.notifyItemChanged(position)
    }

    fun doneClick(view: View) {
        val songs = songViewModel.selectedSongs().value!!
        val name = intent.extras?.getString(PLAY_LIST_DETAIL)
        val id = intent.extras?.getLong(PLAY_LIST_DETAIL)
        if(name == null){
            returnResult(id, songs, Activity.RESULT_OK)
        } else returnResult(name, songs, Activity.RESULT_OK)
        finish()
    }

    fun selectAll(view: View) {
        view as CheckBox
        songViewModel.update(songListSelected(view.isChecked))
    }

    private fun returnResult(name: String?, songs: List<Song>, result: Int) {
        val returnIntent = Intent().apply {
            putExtra(PLAY_LIST_DETAIL, name)
            putExtra("SONGS", Gson().toJson(songs))
        }
        setResult(result, returnIntent)
    }

    private fun returnResult(name: Long?, songs: List<Song>, result: Int) {
        val returnIntent = Intent().apply {
            putExtra(PLAY_LIST_DETAIL, name)
            putExtra("SONGS", Gson().toJson(songs))
        }
        setResult(result, returnIntent)
    }

    private fun songListSelected(select: Boolean): MutableList<Song> {
        return songAdapter.songList.mapIndexed { index, item ->
            toggleSelect(index, item, select)
            item.apply { isSelected = select }
        }.filter { it.isSelected }.toMutableList()
    }

    override fun onBackPressed() {
        val name = intent.extras?.getString(PLAY_LIST_DETAIL)
        val id = intent.extras?.getLong(PLAY_LIST_DETAIL)
        if(name == null){
            returnResult(id, emptyList(), Activity.RESULT_CANCELED)
        } else returnResult(name, emptyList(), Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        val songs = songViewModel.selectedSongs().value!!
        if (!item.isSelected) {
            songs.add(item)
            toggleSelect(position, item, true)
        } else {
            toggleSelect(position, item, false)
            songs.delete(item)
        }
        songViewModel.update(songs)
    }

    override fun onShuffleClick(view: View) = Unit
    override fun onSortClick(view: View) = Unit
    override fun onPlayAllClick(view: View) = Unit
    override fun onPopupMenuClick(view: View, position: Int, item: Song, itemList: List<Song>) =
        Unit
}
