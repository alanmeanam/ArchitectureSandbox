package com.example.architecturesandbox.custom.recycler

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SwipeRemoveItemDecoration(val background: ColorDrawable,
                                val icon: Drawable?): RecyclerView.ItemDecoration() {

    private var itemCount = 0

    enum class SwipeDirection {
        LEFT
    }

    private var swipeDirection: SwipeDirection? = null

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        // divider bounds
        val left = 0
        val right = parent.width
        var top = 0
        var bottom = 0

        //items above of below removed item
        var firstItemMovingUp: View? = null
        var lastItemComingDown: View? = null

        //check if recycler animation is running
        if (parent.isAnimating) {
            //draw if item is being removed only
            if(state.itemCount <= itemCount || itemCount == 0) {
                itemCount = state.itemCount
                //loop for all itemView in recycler
                for (i in 0 until parent.childCount) {
                    val child = parent.getChildAt(i)
                    //the remaining list below the removed item is pushed up
                    if (child.translationY > 0) {
                        //only need the first item in the remaning list moving up
                        if (firstItemMovingUp == null) firstItemMovingUp = child
                    }
                    //the remaining list above the removed item is being pushed down
                    else if (child.translationY < 0) {
                        lastItemComingDown = child
                    }

                    //check item view is swiping left
                    if (child.translationX < 0) {
                        swipeDirection = SwipeDirection.LEFT
                    }
                }
                //pinching, list below is pushed up and list above is pushed down
                if (firstItemMovingUp != null && lastItemComingDown != null) {
                    top = lastItemComingDown.bottom + lastItemComingDown.translationY.toInt()
                    bottom = firstItemMovingUp.top + firstItemMovingUp.translationY.toInt()
                }
                // slide up, list below is pushed up (translationY is the view moving up)
                else if (firstItemMovingUp != null) {
                    top = firstItemMovingUp.top
                    bottom = firstItemMovingUp.top + firstItemMovingUp.translationY.toInt()
                }
                //Slide down, list above is pushed down (translationY is the view moving down)
                else if (lastItemComingDown != null) {
                    top = lastItemComingDown.bottom + lastItemComingDown.translationY.toInt()
                    bottom = lastItemComingDown.bottom
                }

                //draw icon
                background.setBounds(left, top, right, bottom)
                background.draw(c)

                //draw icon
                if (icon != null && swipeDirection != null) {
                    when (swipeDirection) {
                        SwipeDirection.LEFT -> {
                            val height = bottom - top
                            val iconMargin = icon.intrinsicHeight
                            val iconTop = top + (height - iconMargin) / 2
                            val iconBottom = iconTop + iconMargin
                            val iconLeft = right - iconMargin - icon.intrinsicWidth
                            val iconRight = right - iconMargin
                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        }
                    }
                }
                icon?.draw(c)
            }
        }

    }

}