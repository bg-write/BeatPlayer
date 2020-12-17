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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentFavoriteDetailBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.MediaItemData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.adapters.SongAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.FavoriteViewModel
import com.crrl.beatplayer.ui.viewmodels.PlaylistViewModel
import com.crrl.beatplayer.utils.BeatConstants.FAVORITE_KEY
import com.crrl.beatplayer.utils.BeatConstants.PLAY_ALL_SHUFFLED
import com.crrl.beatplayer.utils.GeneralUtils.getExtraBundle
import org.koin.android.ext.android.inject

class FavoriteDetailFragment : BaseFragment<Song>() {

    private lateinit var binding: FragmentFavoriteDetailBinding
    private lateinit var songAdapter: SongAdapter
    private val favoriteViewModel by inject<FavoriteViewModel>()
    private val playlistViewModel by inject<PlaylistViewModel>()

    private val viewModel by inject<FavoriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_favorite_detail, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        val id = arguments!!.getLong(FAVORITE_KEY)
        binding.favorite = favoriteViewModel.getFavorite(id)

        songAdapter = SongAdapter(songDetailViewModel).apply {
            showHeader = true
            isAlbumDetail = true
            itemClickListener = this@FavoriteDetailFragment
        }

        songDetailViewModel.idsChanged.observe(this) {
            songAdapter.notifyDataSetChanged()
        }

        songDetailViewModel.currentState.observe(this) {
            songAdapter.notifyDataSetChanged()
        }

        viewModel.songListFavorite(id).observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                favoriteViewModel.deleteFavorites(longArrayOf(id))
                requireActivity().onBackPressed()
            } else if (!songAdapter.songList.deepEquals(it)) {
                songAdapter.updateDataSet(it)
                mainViewModel.reloadQueueIds(it.toIDList(), getString(R.string.favorite_music))
            }
        }

        binding.apply {
            list.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = songAdapter
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        val extras =
            getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.favorite_music))
        mainViewModel.mediaItemClicked(item.toMediaItem(), extras)
    }

    override fun onShuffleClick(view: View) {
        val extras =
            getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.favorite_music))
        mainViewModel.transportControls()?.sendCustomAction(PLAY_ALL_SHUFFLED, extras)
    }

    override fun onPlayAllClick(view: View) {
        val extras =
            getExtraBundle(songAdapter.songList.toIDList(), getString(R.string.favorite_music))
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
