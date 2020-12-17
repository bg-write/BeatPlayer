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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentSearchBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.adapters.SearchAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.PlaylistViewModel
import com.crrl.beatplayer.ui.viewmodels.SearchViewModel
import com.crrl.beatplayer.utils.BeatConstants.ALBUM_DETAIL
import com.crrl.beatplayer.utils.BeatConstants.ALBUM_KEY
import com.crrl.beatplayer.utils.BeatConstants.ARTIST_DETAIL
import com.crrl.beatplayer.utils.BeatConstants.ARTIST_KEY
import com.crrl.beatplayer.utils.BeatConstants.PLAY_LIST_TYPE
import com.crrl.beatplayer.utils.GeneralUtils.PORTRAIT
import com.crrl.beatplayer.utils.GeneralUtils.getExtraBundle
import com.crrl.beatplayer.utils.GeneralUtils.getOrientation
import com.crrl.beatplayer.utils.GeneralUtils.toggleShowKeyBoard
import org.koin.android.ext.android.inject

class SearchFragment : BaseFragment<MediaItem>(), TextWatcher {

    private lateinit var binding: FragmentSearchBinding

    private lateinit var searchAdapter: SearchAdapter

    private val searchViewModel by inject<SearchViewModel>()
    private val playlistViewModel by inject<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_search, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        val sc = if (getOrientation(requireActivity()) == PORTRAIT) 2 else 5

        searchAdapter = SearchAdapter(requireActivity(), songDetailViewModel, searchViewModel, this, sc)

        binding.apply {
            searchSrcText.apply {
                addTextChangedListener(this@SearchFragment)
                toggleShowKeyBoard(context, this, true)
            }

            back.setOnClickListener {
                toggleShowKeyBoard(requireActivity(), searchSrcText, false)
                activity!!.onBackPressed()
            }

            binding.searchList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = searchAdapter
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }

            close.setOnClickListener { searchSrcText.text.clear() }
        }

        searchViewModel.searchLiveData().observe(viewLifecycleOwner) {
            searchAdapter.updateDataSet(it)
        }

        binding.let {
            it.viewModel = searchViewModel
            it.lifecycleOwner = this
            it.status = false
            it.executePendingBindings()
        }
    }

    override fun onItemClick(view: View, position: Int, item: MediaItem) {
        super.onItemClick(view, position, item)
        when (item) {
            is Song -> songClicked(item)
            is Album -> albumClicked(item)
            is Artist -> artistClicked(item)
        }
        toggleShowKeyBoard(requireActivity(), binding.searchSrcText, false)
    }

    override fun onPopupMenuClick(
        view: View,
        position: Int,
        item: MediaItem,
        itemList: List<MediaItem>
    ) {
        super.onPopupMenuClick(view, position, item, itemList)
        if (item is Song) songPopup(item, view)
    }

    private fun songPopup(song: Song, view: View) {
        powerMenu!!.showAsAnchorRightTop(view)
        playlistViewModel.playLists().observe(viewLifecycleOwner) {
            buildPlaylistMenu(it, song)
        }
    }

    private fun songClicked(item: Song) {
        val extras = getExtraBundle(longArrayOf(item.id), PLAY_LIST_TYPE)
        mainViewModel.mediaItemClicked(item.toMediaItem(), extras)
    }

    private fun albumClicked(item: Album) {
        activity!!.addFragment(
            R.id.nav_host_fragment,
            AlbumDetailFragment(),
            ALBUM_DETAIL,
            true,
            bundleOf(ALBUM_KEY to item.id)
        )
    }

    private fun artistClicked(item: Artist) {
        activity!!.addFragment(
            R.id.nav_host_fragment,
            ArtistDetailFragment(),
            ARTIST_DETAIL,
            true,
            bundleOf(ARTIST_KEY to item.id)
        )
    }

    override fun onTextChanged(src: CharSequence?, start: Int, before: Int, count: Int) {
        searchViewModel.search(src.toString())
        binding.status = src?.isNotEmpty()
    }

    override fun afterTextChanged(s: Editable?) = Unit
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
}
