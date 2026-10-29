# Changelog

All notable changes to the **MuslimCentre** application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-03-08

### Added
- **Initial Project Setup**:
  - Configured Android application module (`:app`) targeting Android 15 / API 37 with Kotlin 2.2 and Jetpack Compose.
  - Integrated Material 3 design system and Gradle Version Catalog (`libs.versions.toml`).
  - Set up Git version control repository on `main` branch connected to GitHub (`https://github.com/squarestudios89-cyber/MuslimCentre.git`).

- **Salaah (Prayer) Times Feature**:
  - Integrated `com.batoulapps.adhan` library for offline astronomical prayer time calculations (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha).
  - Integrated Google Play Services `FusedLocationProviderClient` (`LocationTracker.kt`) for geolocation detection with reverse-geocoding city lookup.
  - Included automatic fallback to Makkah coordinates when location access is pending or unavailable.
  - Built live countdown timer ticker displaying remaining hours, minutes, and seconds until the next prayer.
  - Added dual date display for Gregorian and Hijri Islamic calendars (`android.icu.util.IslamicCalendar`).
  - Added Calculation Settings Dialog supporting calculation methods (*Muslim World League, ISNA, Umm Al-Qura, Egyptian, Karachi, Moonsighting Committee, etc.*) and Asr Juristic Methods (*Shafi'i/Standard vs. Hanafi*).
  - Designed an Emerald Green & Gold Islamic Material 3 Jetpack Compose UI layout with Compose Preview support.
