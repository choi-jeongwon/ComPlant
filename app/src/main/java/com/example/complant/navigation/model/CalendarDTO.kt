package com.example.complant.navigation.model

data class CalendarDTO ( // 물을 준 날을 표시하는 데이터
    var uid : String? = null, // 유저 id
    var users : MutableMap<String, Boolean> = HashMap()) {
    data class Watered(
        var uid : String? = null,
        var timestamp : Long? = null,
        var wateredYear : Int? = null, // 물을 준 날짜 년도
        var wateredMonth : Int? = null, // 물을 준 날짜 월
        var wateredDay : Int? = null, // 물을 준 날짜 일
        var wateredString : String? = null // 물을 준 날짜 문자열
    )
}


