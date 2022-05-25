package com.example.architecturesandbox.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.architecturesandbox.R
import com.example.architecturesandbox.utils.canonicalTag
import org.jetbrains.anko.support.v4.toast

class BaseLoadingDialog : DialogFragment() {

    private var mDismissListener: DismissListener? = null
    private var isBackEnabled = false

    companion object {
        val TAG = canonicalTag<BaseLoadingDialog>()
        fun show(fm: FragmentManager): BaseLoadingDialog {
            Log.i("Tag:", TAG)
            var loadingDialog = fm.findFragmentByTag(TAG) as BaseLoadingDialog?
            if (loadingDialog == null) {
                loadingDialog = BaseLoadingDialog()
            }
            loadingDialog.isBackEnabled = false
            if (!loadingDialog.isAdded) {
                val ft = fm.beginTransaction()
                ft.add(loadingDialog, TAG)
                ft.commitAllowingStateLoss()
            }
            return loadingDialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context
        val inflater = LayoutInflater.from(context)
        val rootView: View = inflater.inflate(R.layout.dialog_fragment_loading, null, false)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(rootView).setCancelable(false)
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, event: KeyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (isBackEnabled) {
                    dismiss()
                } else {
                    toast("Wait a moment…")
                }
            }
            true
        }
        alertDialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.navigationBarColor = ContextCompat.getColor(context, R.color.white)
        return alertDialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (mDismissListener != null) {
            mDismissListener!!.onDismiss()
        }
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
    }

    interface DismissListener {
        fun onDismiss()
    }

}