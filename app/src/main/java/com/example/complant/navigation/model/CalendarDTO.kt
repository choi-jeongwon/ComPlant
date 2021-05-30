package com.example.complant.navigation.model

data class CalendarDTO ( // 물주기 시작 날짜 년도, 월, 일, 물주는 날짜 간격 일
    var uid : String? = null, // 유저 id
    var wateringStartYear : Int? = null, // 물주기 시작 날짜 년도
    var wateringStartMonth : Int? = null, // 물주기 시작 날짜 월
    var wateringStartDay : Int? = null, // 물주기 시작 날짜 일
    var wateringIntervalDay : Int? = null // 물주는 날짜 간격
)