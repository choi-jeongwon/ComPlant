package com.example.complant.navigation.model

data class UserInfoDTO (
    var uid : String? = null,
    var users : MutableMap<String, Boolean> = HashMap()) {
    data class UserInfo (
        var uid : String? = null,
        //var imageUrl : String? = null,
        var userId : String? = null,
        var password : String? = null,
        var profileName : String? = null,
        var plantName : String? = null,
        var plantType : String? = null,
        var startYear : Int? = null, // 기르기 시작 날짜 년도
        var startMonth : Int? = null, // 기르기 시작 날짜 월
        var startDay : Int? = null // 기르기 시작 날짜 일
        )
}
