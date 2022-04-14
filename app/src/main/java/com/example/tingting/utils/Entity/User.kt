package com.example.tingting.utils.Entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var address: String? = null,
    var avatar: String? = null,
    var birthDate: String? = null,
    var gender: String? = null,
    var listFavorite: List<String>? = null,
    var listPhoto: List<String>? = null,
    var name: String? = null,
    var phone: String? = null,
    var isFirstTimeLogin: Boolean ?= null
)