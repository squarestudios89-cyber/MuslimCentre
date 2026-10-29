package com.squarestudios.muslimcentre.data.model

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Madhab

enum class PrayerType(
    val englishName: String,
    val arabicName: String,
) {
    FAJR("Fajr", "الفجر"),
    SUNRISE("Sunrise", "الشروق"),
    DHUHR("Dhuhr", "الظهر"),
    ASR("Asr", "العصر"),
    MAGHRIB("Maghrib", "المغرب"),
    ISHA("Isha", "العشاء")
}

data class PrayerItem(
    val type: PrayerType,
    val name: String,
    val arabicName: String,
    val formattedTime: String,
    val timeMillis: Long,
    val isNext: Boolean = false,
    val isCurrent: Boolean = false
)

data class PrayerTimesData(
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val dateFormatted: String,
    val hijriDateFormatted: String,
    val prayers: List<PrayerItem>,
    val nextPrayer: PrayerItem?,
    val timeUntilNextMillis: Long = 0L,
    val calculationMethod: CalculationMethodConfig = CalculationMethodConfig.MUSLIM_WORLD_LEAGUE,
    val asrMadhab: AsrMadhab = AsrMadhab.SHAFI
)

enum class CalculationMethodConfig(
    val displayName: String,
    val adhanMethod: CalculationMethod
) {
    MUSLIM_WORLD_LEAGUE("Muslim World League", CalculationMethod.MUSLIM_WORLD_LEAGUE),
    NORTH_AMERICA("ISNA (North America)", CalculationMethod.NORTH_AMERICA),
    EGYPTIAN("Egyptian General Authority", CalculationMethod.EGYPTIAN),
    UMM_AL_QURA("Umm Al-Qura (Makkah)", CalculationMethod.UMM_AL_QURA),
    KARACHI("University of Islamic Sciences, Karachi", CalculationMethod.KARACHI),
    DUBAI("Dubai", CalculationMethod.DUBAI),
    MOON_SIGHTING_COMMITTEE("Moonsighting Committee", CalculationMethod.MOON_SIGHTING_COMMITTEE),
    KUWAIT("Kuwait", CalculationMethod.KUWAIT),
    QATAR("Qatar", CalculationMethod.QATAR),
    SINGAPORE("Singapore", CalculationMethod.SINGAPORE),
    OTHER("Other", CalculationMethod.OTHER)
}

enum class AsrMadhab(
    val displayName: String,
    val adhanMadhab: Madhab
) {
    SHAFI("Standard (Shafi'i, Maliki, Hanbali)", Madhab.SHAFI),
    HANAFI("Hanafi", Madhab.HANAFI)
}
