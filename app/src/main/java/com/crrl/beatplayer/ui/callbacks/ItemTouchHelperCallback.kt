package com.crrl.beatplayer.ui.callbacks

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.extensions.setAll
import com.crrl.beatplayer.ui.adapters.QueueAdapter

class ItemTouchHelperCallback(
    private val adapter: QueueAdapter
) : ItemTouchHelper.Callback() {

    private val position: MutableList<Int> = mutableListOf()
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (position.isEmpty())
            position.setAll(listOf(viewHolder.adapterPosition, target.adapterPosition))
        else
            position.add(1, target.adapterPosition)

        return adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val itemViewHolder = viewHolder as QueueAdapter.QueueViewHolder
            itemViewHolder.onSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val itemViewHolder = viewHolder as QueueAdapter.QueueViewHolder

        if (position.isEmpty()) return itemViewHolder.onReleased()
        val from = position[0]
        val to = position[1]
        itemViewHolder.onReleased(from, to)
    }

    override fun isItemViewSwipeEnabled() = false
    override fun isLongPressDragEnabled() = false
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
}