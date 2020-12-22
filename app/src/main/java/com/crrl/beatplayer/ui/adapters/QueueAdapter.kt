package com.crrl.beatplayer.ui.adapters

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.QueueSongItemBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.interfaces.ItemMovedListener
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.callbacks.ItemTouchHelperCallback
import com.crrl.beatplayer.ui.viewmodels.SongDetailViewModel

class QueueAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val songDetailViewModel: SongDetailViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var queueViewLiveData = MutableLiveData<RecyclerView>()
    private var isFirstTime = true
    private var currentPosition = -1

    val songList = mutableListOf<Song>()
    var itemClickListener: ItemClickListener<Song>? = null
    var itemMovedListener: ItemMovedListener? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(this))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        queueViewLiveData.postValue(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBinding = parent.inflateWithBinding<QueueSongItemBinding>(R.layout.queue_song_item)
        return QueueViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as QueueAdapter.QueueViewHolder).bind(songList[position])
    }

    override fun getItemCount() = songList.size

    fun updateDataSet(newList: List<Song>) {
        songList.setAll(newList.toMutableList())
        notifyDataSetChanged()
        initObservers()
    }

    fun onItemMove(from: Int, to: Int): Boolean {
        if (from < songList.size && to < songList.size) {
            if (from < to) {
                for (i in from until to) {
                    songList.setAll(songList.moveElement(i, i + 1))
                }
            } else {
                for (i in from downTo to + 1) {
                    songList.setAll(songList.moveElement(i, i - 1))
                }
            }
            notifyItemMoved(from, to)
        }
        return true
    }

    fun scrollToPosition(id: Long) {
        queueViewLiveData
            .filter { it != null }
            .filter { !it.isAnimating }
            .observe(lifecycleOwner) { view ->
                view.snapToPosition(songList.indexOfFirst { it.id == id }, smooth = !isFirstTime)
                isFirstTime = false
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class QueueViewHolder(private val binding: QueueSongItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnTouchListener {
        fun bind(song: Song) {
            binding.apply {
                this.song = song
                this.viewModel = songDetailViewModel

                executePendingBindings()

                container.setOnClickListener(this@QueueViewHolder)
                itemMenu.setOnClickListener(this@QueueViewHolder)
                selected.setOnTouchListener(this@QueueViewHolder)
            }
        }

        override fun onClick(view: View) {
            when (view.id) {
                R.id.item_menu -> itemClickListener?.onPopupMenuClick(
                    view,
                    adapterPosition,
                    songList[adapterPosition],
                    songList
                )
                R.id.container -> itemClickListener?.onItemClick(
                    view,
                    adapterPosition,
                    songList[adapterPosition]
                )
            }
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> itemTouchHelper.startDrag(this)
                MotionEvent.ACTION_BUTTON_PRESS -> v.performClick()
            }
            return true
        }

        fun onReleased(from: Int = 0, to: Int = 0) {
            itemMovedListener?.itemMoved(from, to)
            binding.root.elevation = 0.0f
            binding.root.setBackgroundResource(R.drawable.list_item_ripple)
        }

        fun onSelected() {
            binding.root.elevation = 100.0f
            binding.root.setBackgroundResource(R.drawable.list_item_ripple_background)
        }
    }

    private fun initObservers() {
        songDetailViewModel.currentData.observe(lifecycleOwner) { itemData ->
            val lastPosition = currentPosition
            currentPosition = songList.indexOfFirst { itemData.id == it.id }

            if (lastPosition != -1)
                notifyItemChanged(lastPosition)

            if (currentPosition != -1)
                notifyItemChanged(currentPosition)
        }


        songDetailViewModel.currentState.observe(lifecycleOwner){
            if (currentPosition != -1)
                notifyItemChanged(currentPosition)
        }
    }
}