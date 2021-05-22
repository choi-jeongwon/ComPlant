package com.example.complant.navigation.model

data class FollowDTO (
    // 중복 follower, follwing 방지 위해 HashMap 생성
    var followerCount : Int = 0,
    var followers : MutableMap<String, Boolean> = HashMap(),

    var followingCount : Int = 0,
    var followings : MutableMap<String, Boolean> = HashMap()
)