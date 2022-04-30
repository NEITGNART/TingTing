package com.example.tingting

import android.location.Geocoder
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.tingting", appContext.packageName)
    }

    fun getLatLngFromAddress(address: String?): LatLng {
        val geocoder = Geocoder(InstrumentationRegistry.getInstrumentation().targetContext)
        val list = geocoder.getFromLocationName(address, 3)
        if (list.size > 0) {
            return LatLng(list[0].latitude, list[0].longitude)
        }
        throw Exception("Can't get latlng from address")
    }

}