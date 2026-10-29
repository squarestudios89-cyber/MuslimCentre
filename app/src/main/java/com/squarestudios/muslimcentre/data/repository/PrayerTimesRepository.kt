package com.squarestudios.muslimcentre.data.repository

import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Prayer
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.squarestudios.muslimcentre.data.model.AsrMadhab
import com.squarestudios.muslimcentre.data.model.CalculationMethodConfig
import com.squarestudios.muslimcentre.data.model.PrayerItem
import com.squarestudios.muslimcentre.data.model.PrayerTimesData
import com.squarestudios.muslimcentre.data.model.PrayerType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PrayerTimesRepository {

    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        locationName: String,
        calculationMethod: CalculationMethodConfig = CalculationMethodConfig.MUSLIM_WORLD_LEAGUE,
        asrMadhab: AsrMadhab = AsrMadhab.SHAFI,
        targetDate: Date = Date()
    ): PrayerTimesData {
        val coordinates = Coordinates(latitude, longitude)
        val dateComponents = DateComponents.from(targetDate)
        
        val params = calculationMethod.adhanMethod.parameters.apply {
            madhab = asrMadhab.adhanMadhab
        }

        val prayerTimes = PrayerTimes(coordinates, dateComponents, params)

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())

        val nowMillis = System.currentTimeMillis()

        val fajrTime = prayerTimes.fajr.time
        val sunriseTime = prayerTimes.sunrise.time
        val dhuhrTime = prayerTimes.dhuhr.time
        val asrTime = prayerTimes.asr.time
        val maghribTime = prayerTimes.maghrib.time
        val ishaTime = prayerTimes.isha.time

        // Determine current/next prayer
        val rawNext = prayerTimes.nextPrayer()
        val currentType = mapAdhanPrayerToType(prayerTimes.currentPrayer())
        val nextType = if (rawNext == Prayer.NONE) {
            // After Isha, next prayer is tomorrow's Fajr
            PrayerType.FAJR
        } else {
            mapAdhanPrayerToType(rawNext)
        }

        val prayerItems = listOf(
            PrayerItem(
                type = PrayerType.FAJR,
                name = PrayerType.FAJR.englishName,
                arabicName = PrayerType.FAJR.arabicName,
                formattedTime = timeFormat.format(prayerTimes.fajr),
                timeMillis = fajrTime,
                isNext = nextType == PrayerType.FAJR,
                isCurrent = currentType == PrayerType.FAJR
            ),
            PrayerItem(
                type = PrayerType.SUNRISE,
                name = PrayerType.SUNRISE.englishName,
                arabicName = PrayerType.SUNRISE.arabicName,
                formattedTime = timeFormat.format(prayerTimes.sunrise),
                timeMillis = sunriseTime,
                isNext = nextType == PrayerType.SUNRISE,
                isCurrent = currentType == PrayerType.SUNRISE
            ),
            PrayerItem(
                type = PrayerType.DHUHR,
                name = PrayerType.DHUHR.englishName,
                arabicName = PrayerType.DHUHR.arabicName,
                formattedTime = timeFormat.format(prayerTimes.dhuhr),
                timeMillis = dhuhrTime,
                isNext = nextType == PrayerType.DHUHR,
                isCurrent = currentType == PrayerType.DHUHR
            ),
            PrayerItem(
                type = PrayerType.ASR,
                name = PrayerType.ASR.englishName,
                arabicName = PrayerType.ASR.arabicName,
                formattedTime = timeFormat.format(prayerTimes.asr),
                timeMillis = asrTime,
                isNext = nextType == PrayerType.ASR,
                isCurrent = currentType == PrayerType.ASR
            ),
            PrayerItem(
                type = PrayerType.MAGHRIB,
                name = PrayerType.MAGHRIB.englishName,
                arabicName = PrayerType.MAGHRIB.arabicName,
                formattedTime = timeFormat.format(prayerTimes.maghrib),
                timeMillis = maghribTime,
                isNext = nextType == PrayerType.MAGHRIB,
                isCurrent = currentType == PrayerType.MAGHRIB
            ),
            PrayerItem(
                type = PrayerType.ISHA,
                name = PrayerType.ISHA.englishName,
                arabicName = PrayerType.ISHA.arabicName,
                formattedTime = timeFormat.format(prayerTimes.isha),
                timeMillis = ishaTime,
                isNext = nextType == PrayerType.ISHA,
                isCurrent = currentType == PrayerType.ISHA
            )
        )

        val nextPrayerItem = prayerItems.find { it.isNext } ?: prayerItems.first()

        // Calculate time until next prayer
        val timeUntilNext = if (nextPrayerItem.timeMillis > nowMillis) {
            nextPrayerItem.timeMillis - nowMillis
        } else {
            // Tomorrow's Fajr
            val tomorrowCalendar = Calendar.getInstance().apply {
                time = targetDate
                add(Calendar.DAY_OF_YEAR, 1)
            }
            val tomorrowPrayerTimes = PrayerTimes(
                coordinates,
                DateComponents.from(tomorrowCalendar.time),
                params
            )
            tomorrowPrayerTimes.fajr.time - nowMillis
        }

        // Hijri date using android.icu.util.IslamicCalendar (API 24+)
        val hijriFormatted = try {
            val islamicCalendar = android.icu.util.IslamicCalendar()
            islamicCalendar.time = targetDate
            val day = islamicCalendar.get(android.icu.util.IslamicCalendar.DAY_OF_MONTH)
            val monthIndex = islamicCalendar.get(android.icu.util.IslamicCalendar.MONTH)
            val year = islamicCalendar.get(android.icu.util.IslamicCalendar.YEAR)

            val monthNames = arrayOf(
                "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
                "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
                "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
            )
            val monthName = if (monthIndex in monthNames.indices) monthNames[monthIndex] else ""
            "$day $monthName $year AH"
        } catch (_: Exception) {
            ""
        }

        return PrayerTimesData(
            locationName = locationName,
            latitude = latitude,
            longitude = longitude,
            dateFormatted = dateFormat.format(targetDate),
            hijriDateFormatted = hijriFormatted,
            prayers = prayerItems,
            nextPrayer = nextPrayerItem,
            timeUntilNextMillis = timeUntilNext.coerceAtLeast(0L),
            calculationMethod = calculationMethod,
            asrMadhab = asrMadhab
        )
    }

    private fun mapAdhanPrayerToType(prayer: Prayer): PrayerType? {
        return when (prayer) {
            Prayer.FAJR -> PrayerType.FAJR
            Prayer.SUNRISE -> PrayerType.SUNRISE
            Prayer.DHUHR -> PrayerType.DHUHR
            Prayer.ASR -> PrayerType.ASR
            Prayer.MAGHRIB -> PrayerType.MAGHRIB
            Prayer.ISHA -> PrayerType.ISHA
            else -> null
        }
    }
}
