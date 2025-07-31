package com.example.libbook.utlis


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import java.util.Locale

object LocationUtils {

    fun getAddressFromLocation(context: Context, onAddressFound: (Address?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // This is handled by the permission request on the screen.
            // If we get here without permission, we can't proceed.
            onAddressFound(null)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                            onAddressFound(addresses.firstOrNull())
                        }
                    } else {
                        // For older APIs
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        onAddressFound(addresses?.firstOrNull())
                    }
                } catch (e: Exception) {
                    onAddressFound(null)
                }
            } else {
                onAddressFound(null)
            }
        }
    }
}