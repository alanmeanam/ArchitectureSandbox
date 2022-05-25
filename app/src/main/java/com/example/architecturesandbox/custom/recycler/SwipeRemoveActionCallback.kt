package com.example.architecturesandbox.custom.recycler

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeRemoveActionCallback(private val background: ColorDrawable,
                                private val icon: Drawable?,
                                private val swipeRemoveActionListenerList: HashSet<SwipeRemoveActionListener>
): ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        //"Implement method for drag & drop"
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        swipeRemoveActionListenerList.forEach { it.removeItem(position = position) }
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var swipeFlags = ItemTouchHelper.ACTION_STATE_IDLE
        swipeRemoveActionListenerList.forEach {
            swipeFlags = it.swipeBehavior(viewHolder = viewHolder)
        }
        val dragFlags = ItemTouchHelper.ACTION_STATE_IDLE
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView
        //swipe left
        if (dX < 0) {
            //calculate position of icon
            if (icon != null) {
                val iconMargin = icon.intrinsicHeight
                val iconTop = itemView.top + (itemView.height - iconMargin) / 2
                val iconBottom = iconTop + iconMargin
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        } else {
            background.setBounds(0, 0, 0, 0)
            icon?.setBounds(0, 0, 0, 0)
        }
        //draw
        background.draw(c)
        icon?.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    interface SwipeRemoveActionListener {
        fun removeItem(position: Int)
        fun swipeBehavior(viewHolder: RecyclerView.ViewHolder): Int
    }

}