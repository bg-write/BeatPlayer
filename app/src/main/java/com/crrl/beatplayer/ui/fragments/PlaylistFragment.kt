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
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentPlaylistBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.ui.adapters.PlaylistAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.PlaylistViewModel
import com.crrl.beatplayer.utils.BeatConstants.PLAY_LIST_DETAIL
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import kotlinx.android.synthetic.main.layout_recyclerview.*
import org.koin.android.ext.android.inject


class PlaylistFragment : BaseFragment<Playlist>() {

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var binding: FragmentPlaylistBinding

    private val playlistViewModel by inject<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflateWithBinding(R.layout.fragment_playlist, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {

        playlistAdapter = PlaylistAdapter().apply {
            itemClickListener = this@PlaylistFragment
        }

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playlistAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        mainViewModel.hideCreatePlaylistButton()
                    }
                    if (dy < 0) {
                        mainViewModel.showCreatePlaylistButton()
                    }
                }
            })
        }

        playlistViewModel.playLists().observe(viewLifecycleOwner) {
            playlistAdapter.updateDataSet(it)
        }

        mainViewModel.binding.createPlayList.setOnClickListener { createPlaylistDialog() }

        binding.let {
            it.viewModel = playlistViewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun onItemClick(view: View, position: Int, item: Playlist) {
        activity!!.addFragment(
            R.id.nav_host_fragment,
            PlaylistDetailFragment(),
            PLAY_LIST_DETAIL,
            extras = bundleOf(PLAY_LIST_DETAIL to item.id)
        )
    }

    override fun onPopupMenuClick(
        view: View,
        position: Int,
        item: Playlist,
        itemList: List<Playlist>
    ) {
        val deleted = playlistViewModel.delete(item.id)
        if (deleted != -1)
            mainViewModel.binding.mainContainer.snackbar(
                SUCCESS,
                getString(R.string.playlist_deleted_success, item.name),
                LENGTH_SHORT
            )
        else
            mainViewModel.binding.mainContainer.snackbar(
                ERROR,
                getString(R.string.playlist_deleted_error, item.name),
                LENGTH_SHORT,
                action = getString(R.string.retry),
                clickListener = View.OnClickListener {
                    onPopupMenuClick(view, position, item, itemList)
                })
    }
}
