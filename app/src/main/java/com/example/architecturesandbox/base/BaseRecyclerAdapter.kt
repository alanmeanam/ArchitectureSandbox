package com.example.architecturesandbox.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.architecturesandbox.R
import com.example.architecturesandbox.custom.recycler.SwipeRemoveActionCallback
import com.example.architecturesandbox.custom.recycler.SwipeRemoveItemDecoration
import com.example.architecturesandbox.utils.action
import com.example.architecturesandbox.utils.snack
import com.google.android.material.snackbar.Snackbar

abstract class BaseRecyclerAdapter<T: Any>(
    private val masterList: MutableList<T> =  mutableListOf(),
    private val swipeDelegate: SwipeActionDelegate? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), SwipeRemoveActionCallback.SwipeRemoveActionListener {

    private lateinit var swipeItemTouchHelper: ItemTouchHelper

    internal var selectedItems = HashSet<T>()
    internal var isItemSelected = selectedItems.isEmpty()
    private var recentlyDeleted = HashSet<T>()


    override fun getItemCount(): Int = masterList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //tracker?.let {
        (holder as BaseViewHolder<T>).onBind(data = masterList[position], position = position)//, isActivated = it.isSelected(position.toLong()))
        //}
    }

    abstract class BaseViewHolder<E>(internal val view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(data: E, position: Int, isActivated: Boolean = false)
    }

    internal fun getItemAtPosition(position: Int): T = masterList[position]

    internal fun getItems(): MutableList<T> = masterList

    @SuppressLint("NotifyDataSetChanged")
    internal fun updateList(list: List<T>, returnList: Boolean = false) {
        masterList.clear()
        masterList.addAll(elements = list)
        notifyDataSetChanged()
        if (returnList) returnUpdatedList(list = list)
    }

    open fun returnUpdatedList(list: List<T>) {}

    protected fun recentlyDeleteHeaderSwipe(position: Int) {
        recentlyDeleted.add(element = masterList[position])
    }

    private fun deleteItemSwipe(position: Int) {
        notifyItemRemoved(position)
        recentlyDeleted.add(element = masterList[position])
        masterList.removeAt(position)
    }

    internal fun deleteOneItem(position: Int) {
        notifyItemRemoved(position)
        masterList.removeAt(position)
    }

    internal fun deleteItemCheckbox(action: () -> Unit) {
        recentlyDeleted.clear()
        recentlyDeleted.addAll(elements = selectedItems)
        action.invoke()
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun undoDeleteSingle(item: T, position: Int, action: (() -> Unit)?) {
        masterList.add(index = position, element = item)
        recentlyDeleted.clear()
        notifyItemInserted(position)
        action?.invoke()
    }

    private fun undoDeleteWithHeader(itemAndHeader: List<T>, position: Int, action: (() -> Unit)?) {
        val pos = position-1
        masterList.addAll(index = pos, elements = itemAndHeader)
        notifyItemRangeInserted(pos, itemAndHeader.size)
        recentlyDeleted.clear()
        action?.invoke()
    }

    private fun undoDeleteMultiple(action: ((HashSet<T>) -> Unit)?) {
        action?.invoke(recentlyDeleted)
        masterList.addAll(recentlyDeleted)
        recentlyDeleted.clear()
        notifyDataSetChanged()
    }

    internal fun addItem(item: T, position: Int) {
        selectedItems.add(element = item)
        notifyItemChanged(position)
    }

    internal fun addAllItems() {
        selectedItems.addAll(elements = masterList)
        notifyDataSetChanged()
    }

    internal fun removeAllItems() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    internal fun showUndoSnackbar(view: View, item: List<T>? = null, undoType: UndoType = UndoType.SINGLE, position: Int = 0,
                                  message: String, actionTitle: String = "Undo", actionSingle: (() -> Unit)? =  null,
                                  actionMultiple: ((HashSet<T>) -> Unit)? = null) {
        view.snack(message = message, length = Snackbar.LENGTH_LONG) {
            action(actionTitle = actionTitle) {
                when (undoType) {
                    UndoType.SINGLE -> undoDeleteSingle(item = item!!.single(), position = position, action = actionSingle)
                    UndoType.MULTIPLE -> undoDeleteMultiple(action = actionMultiple)
                    UndoType.HEADER -> undoDeleteWithHeader(itemAndHeader = item!!, position = position, action = actionSingle)
                }

                //if (singleItem) undoDeleteSingle(item = item!!.single(), position = position, action = actionSingle)
                //else undoDeleteMultiple(action = actionMultiple)
            }
        }
    }

    internal fun numberOfSelectedItems(): Int = selectedItems.size

    internal fun selectedItemsText(singularItemText: String, pluralItemText: String): String =
        selectedItems.run { if (size > 1) pluralItemText else singularItemText }

    internal fun deletedItemsText(singularItemText: String, pluralItemText: String): String =
        recentlyDeleted.run { if (size > 1) "$size $pluralItemText deleted" else "$size $singularItemText deleted" }

    override fun removeItem(position: Int) {
        val item = getItemAtPosition(position = position)
        deleteItemSwipe(position = position)
        swipeDelegate?.swipeDelete(item = item, position = position)
    }

    /**
     * If no swipe is required the returned value should be "ItemTouchHelper.ACTION_STATE_IDLE"
     */
    abstract fun viewHolderSwipeBehavior(viewHolder: RecyclerView.ViewHolder): Int

    override fun swipeBehavior(viewHolder: RecyclerView.ViewHolder): Int =
        viewHolderSwipeBehavior(viewHolder = viewHolder)


    internal fun swipeSettings(
        recyclerView: RecyclerView,
        context: Context,
        @ColorRes backgroundColor: Int = R.color.faded_red,
        iconRes: Int = R.drawable.ic_delete_white_24dp,
        withAnimation: Boolean = false
    ) {
        val background = ColorDrawable(ContextCompat.getColor(context, backgroundColor))
        val icon = ContextCompat.getDrawable(context, iconRes)
        if (withAnimation) recyclerView.addItemDecoration(SwipeRemoveItemDecoration(background, icon))
        swipeItemTouchHelper = ItemTouchHelper(
            SwipeRemoveActionCallback(
                background,
                icon,
                hashSetOf(this)
            )
        )
        swipeItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    interface SwipeActionDelegate {
        fun <T: Any> swipeDelete(item: T, position: Int)
    }

    enum class UndoType {
        SINGLE,
        MULTIPLE,
        HEADER
    }

}