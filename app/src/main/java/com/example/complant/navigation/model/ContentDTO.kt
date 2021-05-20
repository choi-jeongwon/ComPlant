package com.example.complant.navigation.model

// explain -> content 의 설명을 관리
// imageUrl -> 이미지 주소 관리
// uid -> (어느 유저가 올렸는지)유저 아이디를 관리
// userId -> 올린 유저의 이메일을 관리
// timestamp -> 언제 content 를 올렸는지 관리
// favoriteCount -> '좋아요'를 몇개 눌렀는지 관리
// favorites -> '중복 좋아요'를 방지할 수 있도록 '좋아요'를 누른 유저를 관리할 수 있는 Map

// Comment -> 덧글을 관리하는 data class

data class ContentDTO(var explain : String? = null,
                      var imageUrl : String? = null,
                      var uid : String? = null,
                      var userId : String? = null,
                      var timestamp : Long? = null,
                      var favoriteCount : Int = 0,
                      var favorites : MutableMap<String, Boolean> = HashMap()){
    data class Comment(var uid : String? = null,
                       var userId : String? = null,
                       var comment : String? = null,
                       var timestamp: Long? = null)
}