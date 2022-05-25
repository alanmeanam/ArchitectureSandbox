package com.example.architecturesandbox.base

import android.util.Log
import android.widget.Filter
import android.widget.Filterable

abstract class BaseFilterableAdapter<T : Any>(
    private var masterList : MutableList<T>,
    swipeDelegate: SwipeActionDelegate?
) : BaseRecyclerAdapter<T>(masterList = masterList, swipeDelegate = swipeDelegate), Filterable {

    private var customFilter : CustomFilter? = null
    private val tempList: ArrayList<T> = ArrayList()

    abstract fun performFiltering(filtrationText: String): ArrayList<T>

    override fun getFilter(): Filter {
        if (customFilter == null) {
            tempList.addAll(elements = masterList)
            customFilter = CustomFilter()
        }
        return customFilter as CustomFilter
    }

    private inner class CustomFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val fResult = FilterResults()
            val filterList: ArrayList<T>
            if (constraint != null && constraint.isNotEmpty()) {
                filterList = this@BaseFilterableAdapter.performFiltering(filtrationText = "$constraint")
                fResult.values = filterList
            } else {
                fResult.values = masterList
            }
            return fResult
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            masterList.clear()
            if (constraint!!.isNotEmpty() || results?.count != 0) {
                masterList.addAll(results?.values as ArrayList<T>)
            }
            else {
                masterList.addAll(tempList)
            }
            Log.e("publishResults", "${masterList.size}")
            notifyDataSetChanged()
        }
    }

}