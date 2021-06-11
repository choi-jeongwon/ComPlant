package com.example.complant.navigation.model

data class UserInfoDTO (
    var uid : String? = null,
    var users : MutableMap<String, Boolean> = HashMap()) {
    data class UserInfo (
        var uid : String? = null,
        var imageUrl : String? = null,
        var userId : String? = null,
        var password : String? = null,
        var profileName : String? = null,
        var plantName : String? = null
        )
}
