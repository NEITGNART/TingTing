package com.example.tingting.utils.Entity

data class Chat(

    val id: String? = null,
    var text: String? = null,
    var isSender: Boolean = false,
    var time: String = "Just Now",
    var showProfile: Boolean = true,
    var type: String? = null,
    val fromId: String ?= null,
    val toId: String ?= null,

    ) {
    constructor() : this("", "", false, "", true, "", "", "")
}
