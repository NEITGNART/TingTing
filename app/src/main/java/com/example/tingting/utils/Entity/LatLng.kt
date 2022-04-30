package com.example.tingting.utils.Entity

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class LatLng(val latitude: Double, val longitude: Double) : Parcelable {
    constructor() : this(0.0, 0.0)
}