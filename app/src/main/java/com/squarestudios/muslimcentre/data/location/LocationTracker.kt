package com.squarestudios.muslimcentre.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val isDefault: Boolean = false
)

class LocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    suspend fun getCurrentLocation(): LocationResult {
        if (!hasLocationPermission()) {
            return getDefaultLocation("Location Permission Required")
        }

        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()

            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val cityName = getCityName(location.latitude, location.longitude)
                        continuation.resume(
                            LocationResult(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                cityName = cityName,
                                isDefault = false
                            )
                        )
                    } else {
                        // Fallback to last location
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                            if (lastLocation != null) {
                                val cityName = getCityName(lastLocation.latitude, lastLocation.longitude)
                                continuation.resume(
                                    LocationResult(
                                        latitude = lastLocation.latitude,
                                        longitude = lastLocation.longitude,
                                        cityName = cityName,
                                        isDefault = false
                                    )
                                )
                            } else {
                                continuation.resume(getDefaultLocation("Makkah (Default)"))
                            }
                        }.addOnFailureListener {
                            continuation.resume(getDefaultLocation("Makkah (Default)"))
                        }
                    }
                }.addOnFailureListener {
                    continuation.resume(getDefaultLocation("Makkah (Default)"))
                }
            } catch (_: SecurityException) {
                continuation.resume(getDefaultLocation("Makkah (Default)"))
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }

    private fun getCityName(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Synchronous fallback for simplicity in background coroutine
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                formatAddress(addresses?.firstOrNull())
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                formatAddress(addresses?.firstOrNull())
            }
        } catch (e: Exception) {
            "Lat: %.2f, Lon: %.2f".format(latitude, longitude)
        }
    }

    private fun formatAddress(address: android.location.Address?): String {
        if (address == null) return "Unknown Location"
        val city = address.locality ?: address.subAdminArea ?: address.adminArea
        val country = address.countryName
        return when {
            city != null && country != null -> "$city, $country"
            city != null -> city
            country != null -> country
            else -> "Unknown Location"
        }
    }

    private fun getDefaultLocation(name: String): LocationResult {
        // Makkah al-Mukarramah coordinates as default
        return LocationResult(
            latitude = 21.4225,
            longitude = 39.8262,
            cityName = name,
            isDefault = true
        )
    }
}
