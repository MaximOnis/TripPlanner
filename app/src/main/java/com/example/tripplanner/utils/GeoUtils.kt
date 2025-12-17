package com.example.tripplanner.ui.utils

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

fun geocode(context: Context, address: String): LatLng? {
    if (address.isBlank()) return null

    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val result = geocoder.getFromLocationName(address, 1)

        if (!result.isNullOrEmpty()) {
            LatLng(result[0].latitude, result[0].longitude)
        } else null
    } catch (e: Exception) {
        null
    }
}
