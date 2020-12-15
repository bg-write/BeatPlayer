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

import android.os.Bundle
import com.crrl.beatplayer.R
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.fragments.LyricFragment
import com.crrl.beatplayer.ui.fragments.SongDetailFragment

open class BaseSongDetailFragment : BaseFragment<Song>() {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showHideBottomSheet()
    }

    override fun onPause() {
        showHideBottomSheet()
        super.onPause()
    }

    override fun onResume() {
        showHideBottomSheet()
        super.onResume()
    }

    private fun showHideBottomSheet() {
        val currentData = songDetailViewModel.currentData.value ?: return
        if (currentData.id == 0L) return
        val fragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (fragment is SongDetailFragment || fragment is LyricFragment) {
            mainViewModel.hideMiniPlayer()
        } else {
            mainViewModel.showMiniPlayer()
        }
    }
}