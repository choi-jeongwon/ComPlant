package com.example.complant.navigation.model

data class CalendarDTO ( // 물주기 시작 날짜 년도, 월, 일, 물주는 날짜 간격 일
    var uid : String? = null, // 유저 id
    var wateringStartYear : Int? = null,
    var wateringStartMonth : Int? = null,
    var wateringStartDay : Int? = null,
    var wateringIntervalDay : Int? = null
)