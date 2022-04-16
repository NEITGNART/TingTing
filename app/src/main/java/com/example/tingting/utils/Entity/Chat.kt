package com.example.tingting.utils.Entity

data class Chat(
    var people: User? = null,
    var chat: String? = null,
    var isSender: Boolean = false,
    var time: String = "Just Now",
    var showProfile: Boolean = true,
    var type: String? = null,
    var img: Int? = null,
)