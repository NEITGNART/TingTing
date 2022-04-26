package com.example.tingting.utils.Entity

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class User(
    var id: String? = null,
    var address: String? = null,
    var avatar: String? = null,
    var birthDate: String? = null,
    var gender: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var firstTimeLogin: Boolean ?= null,
    var description: String? = null
) : Parcelable {
    constructor() : this("", "", "", "", null, null, null, null, description ="")
}