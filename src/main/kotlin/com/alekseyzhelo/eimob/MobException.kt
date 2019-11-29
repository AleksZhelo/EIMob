package com.alekseyzhelo.eimob

class MobException(message: String, cause: Exception?) : Exception(message, cause) {
    constructor(message: String) : this(message, null)
}