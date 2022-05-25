package com.example.architecturesandbox.base

data class BaseError(
    var cause: String = "Operación no realizada",
    var code: Int = -1,
    var exception: Exception? = null
)