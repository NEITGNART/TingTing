package com.example.tingting.utils.Global

import kotlin.math.acos
import kotlin.math.roundToInt
import kotlin.math.sin

fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val theta = lon1 - lon2
    var dist = sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
    dist = acos(dist)
    dist = rad2deg(dist)
    dist *= 60 * 1.1515
    return     (dist * 10.0).roundToInt() / 10.0

}

fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}

fun rad2deg(rad: Double): Double {
    return rad * 180.0 / Math.PI
}