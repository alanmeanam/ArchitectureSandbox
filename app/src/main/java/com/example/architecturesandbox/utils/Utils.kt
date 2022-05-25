package com.example.architecturesandbox.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Utils {

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

}