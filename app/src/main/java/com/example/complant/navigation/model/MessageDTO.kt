package com.example.complant.navigation.model

data class MessageDTO (
var uid : String? = null,
var users : MutableMap<String, Boolean> = HashMap()) {
    data class Messages(
        var uid : String? = null,
        var timestamp : Long? = null,
        var date : String? = null,
        var content : String? = null
    )
}
