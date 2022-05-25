package com.example.architecturesandbox.utils

import android.util.Log
import com.example.architecturesandbox.common.di.qualifiers.GsonBase
import com.google.gson.Gson
import org.json.JSONObject
import javax.inject.Inject

class StringUtilImpl @Inject constructor(
    @GsonBase private val gson: Gson
    ): StringUtil {

    override fun <T> modelToJson(model: T): JSONObject? {
        val jsonString = gson.toJson(model)
        return try {
            JSONObject(jsonString)
        } catch (t: Throwable) {
            Log.e("JSON", "Could not parse malformed JSON")
            null
        }
    }

    /*override fun toJSON(jsonString: String): JSONObject? =
        try {
            JSONObject(jsonString)
        } catch (t: Throwable) {
            Log.e("JSON", "Could not parse malformed JSON")
            null
        }*/

}