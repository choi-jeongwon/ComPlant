package com.example.complant.navigation.model

data class Message11 (
    var uid : String? = null,
    var users : MutableMap<String, Boolean> = HashMap()) {
    data class Messages(
        var timestamp : Long? = null,
        var date : String? = null,
        var startTime : String? = null,
        var endTime : String? = null,
        var content : String? = null,
        var setMain : Boolean? = false)
}
