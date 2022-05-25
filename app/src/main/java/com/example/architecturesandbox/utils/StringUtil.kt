package com.example.architecturesandbox.utils

import org.json.JSONObject

interface StringUtil {

    fun <T>modelToJson(model: T): JSONObject?

    //fun toJSON(jsonString: String): JSONObject?

}