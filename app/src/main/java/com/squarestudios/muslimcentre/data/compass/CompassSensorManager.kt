package com.squarestudios.muslimcentre.data.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

class CompassSensorManager(context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val _azimuth = MutableStateFlow(0f)
    val azimuth: StateFlow<Float> = _azimuth.asStateFlow()

    private val _hasCompassSensor = MutableStateFlow(true)
    val hasCompassSensor: StateFlow<Boolean> = _hasCompassSensor.asStateFlow()

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null

    private var currentAzimuth = 0f

    fun startListening() {
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI)
            _hasCompassSensor.value = true
        } else {
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

            if (accelerometer != null && magnetometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
                sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
                _hasCompassSensor.value = true
            } else {
                _hasCompassSensor.value = false
            }
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            val azimuthInRadians = orientationAngles[0]
            val azimuthInDegrees = (Math.toDegrees(azimuthInRadians.toDouble()).toFloat() + 360) % 360
            updateAzimuthSmoothly(azimuthInDegrees)
        } else {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                gravity = event.values
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                geomagnetic = event.values
            }

            val g = gravity
            val m = geomagnetic
            if (g != null && m != null) {
                val R = FloatArray(9)
                val I = FloatArray(9)
                if (SensorManager.getRotationMatrix(R, I, g, m)) {
                    SensorManager.getOrientation(R, orientationAngles)
                    val azimuthInRadians = orientationAngles[0]
                    val azimuthInDegrees = (Math.toDegrees(azimuthInRadians.toDouble()).toFloat() + 360) % 360
                    updateAzimuthSmoothly(azimuthInDegrees)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    private fun updateAzimuthSmoothly(newAzimuth: Float) {
        // Low pass filter smoothing to prevent needle jitter
        val alpha = 0.25f
        var diff = newAzimuth - currentAzimuth
        if (diff > 180) diff -= 360
        if (diff < -180) diff += 360

        currentAzimuth += alpha * diff
        currentAzimuth = (currentAzimuth + 360) % 360

        _azimuth.value = (currentAzimuth * 10).roundToInt() / 10f
    }
}
