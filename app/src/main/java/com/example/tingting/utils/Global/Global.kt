package com.example.tingting.utils.Global

import kotlin.math.*

fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val p = 0.017453292519943295
    val a = 0.5 - cos((lat2 - lat1) * p) / 2 + cos(lat1 * p) * cos(lat2 * p) * (1 - cos((lon2 - lon1) * p)) / 2
    return 12742 * asin(sqrt(a))
}