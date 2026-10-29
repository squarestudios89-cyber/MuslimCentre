package com.squarestudios.muslimcentre.ui.qibla

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Qibla
import com.squarestudios.muslimcentre.data.compass.CompassSensorManager
import com.squarestudios.muslimcentre.data.location.LocationTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class QiblaUiState(
    val isLoading: Boolean = true,
    val hasLocationPermission: Boolean = false,
    val hasCompassSensor: Boolean = true,
    val azimuth: Float = 0f,
    val qiblaBearing: Float = 0f,
    val qiblaAngle: Float = 0f,
    val isAligned: Boolean = false,
    val locationName: String = "Location Pending",
    val distanceToMakkahKm: Int = 0
)

class QiblaViewModel(application: Application) : AndroidViewModel(application) {

    private val compassSensorManager = CompassSensorManager(application)
    private val locationTracker = LocationTracker(application)

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private val makkahLat = 21.4225
    private val makkahLng = 39.8262

    init {
        loadLocationAndQibla()
        observeCompass()
    }

    fun startListening() {
        compassSensorManager.startListening()
    }

    fun stopListening() {
        compassSensorManager.stopListening()
    }

    fun checkPermissionAndLoad() {
        val hasPermission = locationTracker.hasLocationPermission()
        _uiState.update { it.copy(hasLocationPermission = hasPermission) }
        loadLocationAndQibla()
    }

    fun loadLocationAndQibla() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val location = locationTracker.getCurrentLocation()
            val coords = Coordinates(location.latitude, location.longitude)
            val qibla = Qibla(coords)
            val bearing = qibla.direction.toFloat()

            val dist = calculateDistanceKm(
                location.latitude, location.longitude,
                makkahLat, makkahLng
            )

            _uiState.update { currentState ->
                val angle = (bearing - currentState.azimuth + 360) % 360
                val aligned = abs(angle) <= 3f || abs(angle - 360f) <= 3f

                currentState.copy(
                    isLoading = false,
                    hasLocationPermission = locationTracker.hasLocationPermission(),
                    qiblaBearing = bearing,
                    qiblaAngle = angle,
                    isAligned = aligned,
                    locationName = location.cityName,
                    distanceToMakkahKm = dist
                )
            }
        }
    }

    private fun observeCompass() {
        viewModelScope.launch {
            compassSensorManager.hasCompassSensor.collect { hasSensor ->
                _uiState.update { it.copy(hasCompassSensor = hasSensor) }
            }
        }

        viewModelScope.launch {
            compassSensorManager.azimuth.collect { newAzimuth ->
                _uiState.update { currentState ->
                    val bearing = currentState.qiblaBearing
                    val angle = (bearing - newAzimuth + 360) % 360
                    val aligned = abs(angle) <= 3.5f || abs(angle - 360f) <= 3.5f

                    currentState.copy(
                        azimuth = newAzimuth,
                        qiblaAngle = angle,
                        isAligned = aligned
                    )
                }
            }
        }
    }

    private fun calculateDistanceKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Int {
        val r = 6371.0 // Earth radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return (r * c).toInt()
    }
}
