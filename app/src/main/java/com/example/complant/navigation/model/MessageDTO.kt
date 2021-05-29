package com.example.complant.navigation.model

import java.sql.Timestamp

data class MessageDTO (
    var timestamp : Long? = null,
    var date : String? = null,
    var startTime : String? = null,
    var endTime : String? = null,
    var content : String? = null
)