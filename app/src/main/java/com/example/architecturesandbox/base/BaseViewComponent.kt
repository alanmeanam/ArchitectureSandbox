package com.example.architecturesandbox.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager

open class BaseViewComponent<LISTENER_TYPE>(
    private val layoutInflater: LayoutInflater,
    private val parent: ViewGroup?,
    @LayoutRes private val layoutId: Int
) {

    val rootView: View = layoutInflater.inflate(layoutId, parent, false)
    private var loadingDialog: BaseLoadingDialog? = null

    protected val context: Context get() = rootView.context

    protected val listeners = HashSet<LISTENER_TYPE>()

    fun registerListener(listener: LISTENER_TYPE) {
        listeners.add(element = listener)
    }

    fun unregisterListener(listener: LISTENER_TYPE) {
        listeners.remove(element = listener)
    }

    protected fun <T : View?> bindView(@IdRes id: Int): T = rootView.findViewById<T>(id)

    protected fun loadingStatus(fragManager: FragmentManager, tag: String = "", isLoading: Boolean) {
        if (isLoading) {
            loadingDialog?.dismiss()
            loadingDialog = BaseLoadingDialog()
            loadingDialog?.show(fragManager, tag)
        } else {
            loadingDialog?.dismiss()
        }
    }

}