package com.example.complant.navigation.model

data class CalendarDTO ( // 물을 준 날을 표시하는 데이터
    var uid : String? = null, // 유저 id
    var wateredYear : Int? = null, // 물을 준 날짜 년도
    var wateredYearMonth : Int? = null, // 물을 준 날짜 월
    var wateredYearDay : Int? = null, // 물을 준 날짜 일
    var watered : String? = null // 물을 준 날짜를 String으로 (구분)
)