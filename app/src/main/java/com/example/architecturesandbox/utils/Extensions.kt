package com.example.architecturesandbox.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.architecturesandbox.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_INDEFINITE, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(actionTitle: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(actionTitle, listener)
    color?.let { setActionTextColor(color) }
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

inline fun <reified T> List<T>.sortByDateWithHeader(dateFieldName: String, headerFieldName: String): List<T> {
    val modelClass = T::class.java
    val dateField = modelClass.getDeclaredField(dateFieldName)
    val headerField = modelClass.getDeclaredField(headerFieldName)
    dateField.isAccessible = true
    headerField.isAccessible = true
    var headerDate = ""
    val newList = mutableListOf<T>()
    this.sortedByDescending {
        val dateValue = dateField.get(it) as String
        if (dateValue.isBlank())
            System.currentTimeMillis()
        else
            dateValue.dateToMillis()
    }.forEach { model ->
        val dateValue = dateField.get(model) as String
        val ms = if (dateValue.isBlank())
            System.currentTimeMillis()
        else
            dateValue.dateToMillis()
        val date = ms.fromMillisToDate()
        if (headerDate != date) {
            headerDate = date
            val header = modelClass.newInstance()
            headerField.setBoolean(header, true)
            dateField.set(header, date)
            newList.add(header)
        }
        newList.add(model)
    }
    return newList
}

@SuppressLint("SimpleDateFormat")
fun Long.fromMillisToDate(limiter: String = "/"): String {
    val formatter = SimpleDateFormat("dd${limiter}MM${limiter}yyyy")
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@fromMillisToDate
        timeZone = TimeZone.getDefault()
    }
    return formatter.format(calendar.time)
}

@SuppressLint("SimpleDateFormat")
fun String.dateToMillis(): Long {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val millis = formatter.parse(this)
    return millis?.time ?: 0L
}

fun String.toJSON(): JSONObject? =
    try {
        JSONObject(this)
    } catch (t: Throwable) {
        Log.e("JSON", "Could not parse malformed JSON")
        null
    }

inline fun <reified T: Fragment> canonicalTag(): String = T::class.java.canonicalName!!

fun Context.showSimpleDialog(title: String? = null, message: String, textBtnPositive: String = "yes", textBtnNegative: String? = "no",
                             listenerPositive: ((DialogInterface, Int) -> Unit)? = null,
                             listenerNegative: ((DialogInterface, Int) -> Unit)? = null,
                             isCancelable: Boolean = true,
                             listenerCancel: ((DialogInterface) -> Unit)? = null) {
    val alertDialogBuilder: AlertDialog = AlertDialog.Builder(this).create()
    alertDialogBuilder.apply {
        when {
            !title.isNullOrBlank() -> { setTitle(title) }
            title != null && title.isBlank() -> { setTitle(context.getString(R.string.app_name)) }
        }
        setMessage(message)
        setCancelable(isCancelable)
        setButton(AlertDialog.BUTTON_POSITIVE, textBtnPositive, listenerPositive)
        setButton(AlertDialog.BUTTON_NEGATIVE, textBtnNegative, listenerNegative)
        if (listenerPositive != null) DialogInterface.OnClickListener { dialogInterface, i -> listenerPositive.invoke(dialogInterface, i) }  else dismiss()
        if (!textBtnNegative.isNullOrBlank()) setButton(AlertDialog.BUTTON_NEGATIVE, textBtnNegative, listenerNegative)
        if (listenerNegative != null) DialogInterface.OnClickListener { dialogInterface, i -> listenerNegative.invoke(dialogInterface, i) }  else dismiss()
        if (listenerCancel != null) setOnCancelListener{ listenerCancel.invoke(it) } else setOnCancelListener { dismiss() }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        show()
    }
}

fun Activity.hideKeyboard() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(window.decorView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun AppCompatActivity.replaceFragmentAnimation(fragment: Fragment, tag: String? = null, container: Int, backStack: String? = null) {
    supportFragmentManager.commit {
        setCustomAnimations(
            R.anim.slide_in, // enter
            R.anim.fade_out, // exit
            R.anim.fade_in, // popEnter
            R.anim.slide_out // popExit
        )
        replace(container, fragment, tag)
        addToBackStack(backStack)
    }
}

inline fun <reified T: Activity> Activity.beginActivity(bundle: Bundle? = null) {
    startActivity(Intent(this, T::class.java).apply {
        bundle?.let { putExtras(it) }
    })
}

fun View.setOnClickListenerButton(onSafeClick: (View) -> Unit) {
    val safeClickListener = ClickListenerButton { onSafeClick.invoke(it) }
    setOnClickListener(safeClickListener)
}

/**
 * Class for the safeClickListener
 */
class ClickListenerButton(private var defaultInterval: Int = 1000, val onSafeCLick: (View) -> Unit) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) return
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}