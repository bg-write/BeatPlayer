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
import com.crrl.beatplayer.ui.fragments.base.BaseSongDetailFragment
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
import kotlinx.android.synthetic.main.fragment_song_detail.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import kotlin.math.absoluteValue

class SongDetailFragment : BaseSongDetailFragment(), ItemMovedListener {

    private var binding by AutoClearBinding<FragmentSongDetailBinding>(this)
    private val songViewModel by sharedViewModel<SongViewModel>()
    private val playlistViewModel by sharedViewModel<PlaylistViewModel>()
    private val minFlingVelocity = 800

    private lateinit var gestureDetector: GestureDetector

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
        initSwipeGestures()
    }

    private fun init() {
        initViewComponents()

        songDetailViewModel.currentData.observe(viewLifecycleOwner) {
            initNeeded(songViewModel.getSongById(it.id), emptyList(), 0L)
            launch {
                val raw = withContext(IO) {
                    if (it.id == SONG_ID_DEFAULT) {
                        GeneralUtils.audio2Raw(context!!, settingsUtility.intentPath)
                            ?: byteArrayOf()
                    } else GeneralUtils.audio2Raw(context!!, getSongUri(it.id)) ?: byteArrayOf()
                }
                songDetailViewModel.update(raw)
            }
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
            sharedSong.setOnClickListener { shareItem() }
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

    override fun onDetach() {
        super.onDetach()
        songDetailViewModel.update(byteArrayOf())
        songDetailViewModel.update()
    }

    private fun showQueueList() {
        val style = AlertItemStyle().apply {
            textColor = activity?.getColorByTheme(R.attr.titleTextColor)!!
            selectedTextColor = activity?.getColorByTheme(R.attr.colorAccent)!!
            backgroundColor = activity?.getColorByTheme(R.attr.colorPrimarySecondary2)!!
        }

        val queueAdapter = QueueAdapter(viewLifecycleOwner).apply {
            itemClickListener = this@SongDetailFragment
            itemMovedListener = this@SongDetailFragment
        }

        songDetailViewModel.queueData.observeOnce { queue ->
            queueAdapter.updateDataSet(queue.queue.toSongList(get()))
        }

        songDetailViewModel.currentData.observe(viewLifecycleOwner){
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
        }
    }

    private var touchListener: View.OnTouchListener =
        View.OnTouchListener { v: View, motionEvent: MotionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_UP -> v.performClick()
                else -> {
                }
            }
            true
        }

    private fun initSwipeGestures() {
        gestureDetector =
            GestureDetector(activity, object : GestureDetector.OnGestureListener {
                override fun onDown(event: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (velocityX.absoluteValue > minFlingVelocity) {
                        if (velocityX < 0) {
                            mainViewModel.transportControls()?.skipToNext()
                        } else {
                            mainViewModel.transportControls()?.skipToPrevious()
                        }
                    }
                    return true
                }

                override fun onShowPress(e: MotionEvent?) {
                    Timber.e("onShowPress detected")
                }

                override fun onSingleTapUp(e: MotionEvent?): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent?) {
                    Timber.e("onLongPress detected")
                }

                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    return true
                }
            })
        now_playing_cover.setOnTouchListener(touchListener)
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
