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
import androidx.viewpager.widget.ViewPager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentLibraryBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.adapters.ViewPagerAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseSongDetailFragment
import com.crrl.beatplayer.utils.AutoClearBinding


class LibraryFragment : BaseSongDetailFragment() {

    private var binding by AutoClearBinding<FragmentLibraryBinding>(this)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_library, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val didPermissionsGrant = (requireActivity() as MainActivity).didPermissionsGrant()
        if (didPermissionsGrant) init()
        binding.apply {
            viewModel = mainViewModel
            this.didPermissionsGrant = didPermissionsGrant
            executePendingBindings()

            lifecycleOwner = this@LibraryFragment
        }
    }

    private fun init() {
        binding.apply {
            initViewPager(binding.pagerSortMode)
            tabsContainer.setupWithViewPager(pagerSortMode)
        }
    }

    private fun initViewPager(viewPager: ViewPager) {
        val listSortModeAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager).apply {
            addFragment(FavoriteFragment(), getString(R.string.favorites))
            addFragment(PlaylistFragment(), getString(R.string.playlists))
            addFragment(SongFragment(), getString(R.string.songs))
            addFragment(AlbumFragment(), getString(R.string.albums))
            addFragment(ArtistFragment(), getString(R.string.artists))
            addFragment(FolderFragment(), getString(R.string.folders))
        }

        viewPager.apply {
            adapter = listSortModeAdapter
            offscreenPageLimit = 1
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(s: Int) = Unit
                override fun onPageScrolled(position: Int, offset: Float, offsetPixel: Int) = Unit
                override fun onPageSelected(p: Int) {
                    settingsUtility.startPageIndexSelected = p
                    if(p == 1){
                        mainViewModel.showCreatePlaylistButton()
                    } else mainViewModel.hideCreatePlaylistButton()
                }
            })
            setCurrentItem(settingsUtility.startPageIndexSelected, false)
        }
    }
}
