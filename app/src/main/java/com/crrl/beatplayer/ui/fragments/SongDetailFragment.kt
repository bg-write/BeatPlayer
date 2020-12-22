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
import android.support.v4.media.session.PlaybackStateCompat
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.AlertDialog
import com.crrl.beatplayer.alertdialog.enums.AlertType
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.databinding.FragmentSongDetailBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.interfaces.ItemMovedListener
import com.crrl.beatplayer.models.MediaItemData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.adapters.QueueAdapter
import com.crrl.beatplayer.ui.adapters.SongDetailAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseSongDetailFragment
import com.crrl.beatplayer.ui.transformers.base.BaseTransformer
import com.crrl.beatplayer.ui.viewmodels.PlaylistViewModel
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.AutoClearBinding
import com.crrl.beatplayer.utils.BeatConstants.BIND_STATE_BOUND
import com.crrl.beatplayer.utils.BeatConstants.FROM_POSITION_KEY
import com.crrl.beatplayer.utils.BeatConstants.SONG_ID_DEFAULT
import com.crrl.beatplayer.utils.BeatConstants.SWAP_ACTION
import com.crrl.beatplayer.utils.BeatConstants.TO_POSITION_KEY
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.GeneralUtils.getSongUri
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_song_detail.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SongDetailFragment : BaseSongDetailFragment(), ItemMovedListener {

    private var binding by AutoClearBinding<FragmentSongDetailBinding>(this)
    private val songViewModel by sharedViewModel<SongViewModel>()
    private val playlistViewModel by sharedViewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflateWithBinding(R.layout.fragment_song_detail, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        initViewComponents()

        songDetailViewModel.currentData.observe(viewLifecycleOwner) {
            initNeeded(songViewModel.getSongById(it.id), emptyList(), 0L)
            calculateSongWaveGraph(it)
        }

        songDetailViewModel.time.observe(viewLifecycleOwner) {
            val total = songDetailViewModel.currentData.value?.duration ?: 0
            binding.seekBar.apply {
                val t = progress.percentToMs(total).fixToStep(1000)
                if (t != it) {
                    progress = it.fixToPercent(total).fixPercentBounds()
                }
            }
        }

        binding.apply {
            descriptionContainer.setOnClickListener { showQueueList() }
        }

        songDetailViewModel.currentState.observe(viewLifecycleOwner) {
            songDetailViewModel.update(it.position)
            if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                songDetailViewModel.update(BIND_STATE_BOUND)
            } else songDetailViewModel.update()
        }

        binding.let {
            it.viewModel = songDetailViewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    private fun initSongDetailView() {
        val songDetailAdapter = SongDetailAdapter()

        binding.songList.apply {
            adapter = songDetailAdapter
            addOnItemChangedListener { _, position ->
                val currentId = songDetailViewModel.currentData.value?.id ?: -1L
                val selectedSong = songDetailAdapter.songList[position]

                if (currentId != selectedSong.id) mainViewModel.mediaItemClicked(selectedSong.toMediaItem())
            }
            setSlideOnFling(false)
            setItemTransitionTimeMillis(150)
            setSlideOnFlingThreshold(5100)
        }

        songDetailViewModel.queueData.observe(viewLifecycleOwner) {
            val currentId = songDetailViewModel.currentData.value?.id ?: -1L
            val currentPosition = it.queue.indexOf(currentId)

            songDetailAdapter.updateData(it.queue.toSongList(get()))
            binding.songList.scrollToPosition(currentPosition)
        }

        songDetailViewModel.currentData.observe(viewLifecycleOwner) { item ->
            val currentPosition = songDetailAdapter.songList.indexOfFirst { it.id == item.id }

            if (-1 != currentPosition)
                binding.songList.smoothScrollToPosition(currentPosition)
        }
    }

    override fun onResume() {
        binding.songList.setItemTransformer(getCurrentTransformer())
        super.onResume()
    }

    override fun onDetach() {
        super.onDetach()
        songDetailViewModel.update(byteArrayOf())
        songDetailViewModel.update()
    }

    private fun getCurrentTransformer(): BaseTransformer {
        return GeneralUtils.getTransformerFromString(settingsUtility.currentItemTransformer)
    }

    private fun calculateSongWaveGraph(item: MediaItemData) {
        launch {
            val raw = withContext(IO) {
                if (item.id == SONG_ID_DEFAULT) {
                    GeneralUtils.audio2Raw(context!!, settingsUtility.intentPath)
                        ?: byteArrayOf()
                } else GeneralUtils.audio2Raw(context!!, getSongUri(item.id)) ?: byteArrayOf()
            }
            songDetailViewModel.update(raw)
            if (raw.isEmpty()) {
                view.snackbar(
                    CUSTOM,
                    getString(R.string.raw_error),
                    Snackbar.LENGTH_LONG,
                    action = getString(R.string.retry)
                ) {
                    calculateSongWaveGraph(item)
                }
            }
        }
    }

    private fun showQueueList() {
        val style = AlertItemStyle().apply {
            textColor = activity?.getColorByTheme(R.attr.titleTextColor)!!
            selectedTextColor = activity?.getColorByTheme(R.attr.colorAccent)!!
            backgroundColor = activity?.getColorByTheme(R.attr.colorPrimarySecondary2)!!
        }

        val queueAdapter = QueueAdapter(viewLifecycleOwner, songDetailViewModel).apply {
            itemClickListener = this@SongDetailFragment
            itemMovedListener = this@SongDetailFragment
        }

        songDetailViewModel.currentState.observe(this) {
            val mediaItemData = songDetailViewModel.currentData.value ?: MediaItemData()
            val position = queueAdapter.songList.indexOfFirst { it.id == mediaItemData.id } + 1
            queueAdapter.notifyItemChanged(position)
        }

        songDetailViewModel.queueData.observeOnce { queue ->
            queueAdapter.updateDataSet(queue.queue.toSongList(get()))
        }

        songDetailViewModel.currentData.observe(viewLifecycleOwner) {
            queueAdapter.scrollToPosition(it.id)
        }

        AlertDialog(
            title = getString(R.string.queue_title),
            message = getString(R.string.queue_msg),
            style = style,
            type = AlertType.QUEUE_LIST,
            adapter = queueAdapter
        ).show(activity as AppCompatActivity)
    }

    private fun initViewComponents() {
        binding.apply {
            nextBtn.setOnClickListener {
                mainViewModel.transportControls()?.skipToNext()
            }
            previousBtn.setOnClickListener {
                mainViewModel.transportControls()?.skipToPrevious()
            }
            seekBar.apply {
                onStartTracking = {
                    songDetailViewModel.update()
                }
                onStopTracking = {
                    val mediaItemData = songDetailViewModel.currentData.value ?: MediaItemData()
                    mainViewModel.transportControls()
                        ?.seekTo((it * mediaItemData.duration / 100).toLong())
                }
                onProgressChanged = { position, byUser ->
                    if (byUser) {
                        val mediaItemData = songDetailViewModel.currentData.value ?: MediaItemData()
                        songDetailViewModel.update((position * mediaItemData.duration / 100).toInt())
                    }
                }
            }

            sharedSong.setOnClickListener { shareItem() }
        }

        initSongDetailView()
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        mainViewModel.mediaItemClicked(item.toMediaItem())
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song, itemList: List<Song>) {
        super.onPopupMenuClick(view, position, item, itemList)
        powerMenu?.showAsAnchorRightTop(view)
        playlistViewModel.playLists().observe(viewLifecycleOwner) {
            buildPlaylistMenu(it, item)
        }
    }

    override fun itemMoved(from: Int, to: Int) {
        mainViewModel.transportControls()?.sendCustomAction(
            SWAP_ACTION,
            bundleOf(FROM_POSITION_KEY to from, TO_POSITION_KEY to to)
        )
    }
}
