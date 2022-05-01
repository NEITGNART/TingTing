package com.example.tingting.utils.Entity

data class Notification(
    var content: String? = null,
    var isSender: Boolean = false,
    var time: String = "Just Now",
    var day: String? = null,
    var id: String? = null
    ) {
    constructor() : this("", false, "", "", null)
}
