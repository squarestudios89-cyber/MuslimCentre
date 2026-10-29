package com.squarestudios.muslimcentre.ui.prayertimes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.squarestudios.muslimcentre.data.location.LocationResult
import com.squarestudios.muslimcentre.data.location.LocationTracker
import com.squarestudios.muslimcentre.data.model.AsrMadhab
import com.squarestudios.muslimcentre.data.model.CalculationMethodConfig
import com.squarestudios.muslimcentre.data.model.PrayerTimesData
import com.squarestudios.muslimcentre.data.repository.PrayerTimesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class PrayerTimesUiState(
    val isLoading: Boolean = true,
    val hasLocationPermission: Boolean = false,
    val prayerTimesData: PrayerTimesData? = null,
    val countdownText: String = "",
    val error: String? = null,
    val selectedMethod: CalculationMethodConfig = CalculationMethodConfig.MUSLIM_WORLD_LEAGUE,
    val selectedMadhab: AsrMadhab = AsrMadhab.SHAFI
)

class PrayerTimesViewModel(application: Application) : AndroidViewModel(application) {

    private val locationTracker = LocationTracker(application)
    private val prayerTimesRepository = PrayerTimesRepository()

    private val _uiState = MutableStateFlow(PrayerTimesUiState())
    val uiState: StateFlow<PrayerTimesUiState> = _uiState.asStateFlow()

    private var currentLocationResult: LocationResult? = null
    private var countdownJob: Job? = null

    init {
        checkPermissionAndLoad()
    }

    fun checkPermissionAndLoad() {
        val hasPermission = locationTracker.hasLocationPermission()
        _uiState.update { it.copy(hasLocationPermission = hasPermission) }
        loadPrayerTimes()
    }

    fun onPermissionResult(isGranted: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = isGranted) }
        loadPrayerTimes()
    }

    fun loadPrayerTimes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val location = locationTracker.getCurrentLocation()
                currentLocationResult = location

                calculateAndEmit(location)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Failed to load prayer times"
                    )
                }
            }
        }
    }

    fun updateCalculationMethod(method: CalculationMethodConfig) {
        _uiState.update { it.copy(selectedMethod = method) }
        currentLocationResult?.let { calculateAndEmit(it) }
    }

    fun updateAsrMadhab(madhab: AsrMadhab) {
        _uiState.update { it.copy(selectedMadhab = madhab) }
        currentLocationResult?.let { calculateAndEmit(it) }
    }

    private fun calculateAndEmit(location: LocationResult) {
        val currentState = _uiState.value
        val data = prayerTimesRepository.calculatePrayerTimes(
            latitude = location.latitude,
            longitude = location.longitude,
            locationName = location.cityName,
            calculationMethod = currentState.selectedMethod,
            asrMadhab = currentState.selectedMadhab
        )

        _uiState.update {
            it.copy(
                isLoading = false,
                prayerTimesData = data
            )
        }

        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val data = _uiState.value.prayerTimesData
                if (data == null || data.nextPrayer == null) break

                val now = System.currentTimeMillis()
                val target = data.nextPrayer.timeMillis

                val diff = if (target > now) {
                    target - now
                } else {
                    // Recalculate prayer times if next prayer time has passed
                    currentLocationResult?.let { calculateAndEmit(it) }
                    break
                }

                val seconds = (diff / 1000) % 60
                val minutes = (diff / (1000 * 60)) % 60
                val hours = (diff / (1000 * 60 * 60)) % 24

                val formattedCountdown = if (hours > 0) {
                    String.format(Locale.getDefault(), "%02dh %02dm %02ds", hours, minutes, seconds)
                } else {
                    String.format(Locale.getDefault(), "%02dm %02ds", minutes, seconds)
                }

                _uiState.update { it.copy(countdownText = formattedCountdown) }
                delay(1000L)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
