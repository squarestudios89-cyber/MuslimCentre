# MuslimCentre — As-Built Documentation

> **Document Purpose**: This is a living technical specification and architectural handover document for the **MuslimCentre** Android application. Any developer working on this project should refer to this document to understand the codebase structure, tech stack, core services, hardware integrations, and development workflows.

---

## 1. Project Overview

**MuslimCentre** is an all-in-one Islamic mobile application built for Android using modern native technologies (Kotlin & Jetpack Compose). The application provides essential daily tools for Muslims worldwide, including offline-first **Salaah (Prayer) Times** with automatic geolocation, an interactive real-time **Qibla Direction (Kibla Compass)**, a customizable **Daily Quran Verse & Status Card Generator**, and **AdMob Monetization with Ad-Free Subscriptions**.

- **Package Name**: `com.squarestudios.muslimcentre`
- **Application ID**: `com.squarestudios.muslimcentre`
- **Min SDK**: `24` (Android 7.0 Nougat)
- **Target SDK / Compile SDK**: `37` (Android 15)
- **Primary Language**: Kotlin `2.2.10`
- **UI Framework**: Jetpack Compose (Material 3)

---

## 2. Technical Stack & Infrastructure

| Layer / Category | Technology / Library | Purpose / Details |
| :--- | :--- | :--- |
| **Language** | Kotlin `2.2.10` | Coroutines, StateFlow, Modern Idiomatic Kotlin |
| **UI Toolkit** | Jetpack Compose `2026.02.01` (BOM) | Declarative UI, Material 3, Vector Graphics |
| **Architecture** | MVVM + Repository Pattern | Clean separation of concerns with Unidirectional Data Flow |
| **Ad Engine** | `play-services-ads:25.5.0` | Google Mobile Ads SDK for AdMob banner advertising |
| **Prayer Calculations** | `com.batoulapps.adhan:adhan:1.2.1` | Offline astronomical calculation of prayer times & Qibla bearing |
| **Location Services** | `play-services-location:21.4.0` | `FusedLocationProviderClient` for battery-efficient device location |
| **Geocoding** | `android.location.Geocoder` | Reverse-geocoding latitude/longitude to city & country names |
| **Calendar Engine** | `android.icu.util.IslamicCalendar` | Native API 24+ Hijri calendar conversion |
| **Hardware Sensors** | Android `SensorManager` | Device orientation via `TYPE_ROTATION_VECTOR`, `ACCELEROMETER`, `MAGNETOMETER` |
| **Dependency Catalog** | Gradle Version Catalog | Centralized dependency management in `gradle/libs.versions.toml` |
| **Build Tool** | Android Gradle Plugin `9.4.1` | Automated Android build system |

---

## 3. Core Features & System Architecture

```
+---------------------------------------------------------------------------------------------------+
|                                          MainActivity                                             |
|                                               |                                                   |
|                                           MainScreen                                              |
|                      (Top Permanent Banner Ad Container + Bottom Nav Scaffold)                    |
|                                /              |             \                                     |
|                               /               |              \                                    |
|               PrayerTimesScreen          QiblaScreen       DailyVerseScreen                       |
|                       |                       |                   |                               |
|              PrayerTimesViewModel       QiblaViewModel    DailyVerseViewModel                     |
|                   /       \               /       \               |                               |
|                  /         \             /         \              |                               |
|    PrayerTimesRepository LocationTracker LocationTracker Compass  QuranVerseRepository            |
|              |                  |           |         SensorMgr   (Curated Verses & Daily Engine) |
|         Adhan Engine       GPS/Geocoder GPS/Geocoder Sensors                                      |
+---------------------------------------------------------------------------------------------------+
```

### 3.1 Salaah (Prayer) Times Module
- **Location Processing**: `LocationTracker` requests fine/coarse GPS coordinates via `FusedLocationProviderClient`. If permissions are pending or GPS is disabled, it gracefully falls back to Makkah coordinates (`21.4225° N, 39.8262° E`).
- **Calculation Engine**: `PrayerTimesRepository` feeds location coordinates, date, calculation parameters, and Asr juristic method into `com.batoulapps.adhan.PrayerTimes`.
- **Live Countdown Ticker**: `PrayerTimesViewModel` runs a 1-second coroutine loop calculating remaining time until the next prayer, emitting formatted string updates (*e.g., "In 01h 14m 22s"*).
- **Hijri Date**: Converts Gregorian date to Islamic Hijri date (*e.g., "11 Rabi' al-Thani 1448 AH"*) using `android.icu.util.IslamicCalendar`.
- **Customization Dialog**: Allows users to select calculation authorities (*Muslim World League, ISNA, Umm Al-Qura, Egyptian, Karachi, Moonsighting Committee, etc.*) and Asr Juristic Methods (*Shafi'i/Standard vs Hanafi*).

### 3.2 Qibla Direction (Kibla Compass) Module
- **Hardware Sensor Fusion**: `CompassSensorManager` registers `Sensor.TYPE_ROTATION_VECTOR` (or combined `ACCELEROMETER` + `MAGNETIC_FIELD`).
- **Low-Pass Filter Smoothing**: Uses exponential smoothing on sensor azimuth readings to eliminate needle jitter and deliver smooth compass rose movement.
- **Qibla Bearing Calculation**: `com.batoulapps.adhan.Qibla` computes the initial geodesic bearing angle from the user's location toward Makkah.
- **Distance Calculation**: Uses Haversine formula to compute great-circle distance to Makkah in kilometers.
- **Alignment Detection & Haptic Feedback**: When the top of the device points within ±3.5° of the Qibla bearing, the UI highlights in Emerald Green, displays a *"YOU ARE FACING KAABA!"* badge, and triggers haptic vibration via `LocalHapticFeedback`.

### 3.3 Daily Quran Verse & Status Card Module
- **Daily Rotation Algorithm**: `QuranVerseRepository` deterministic selection picks an inspiring verse based on `dayOfYear` modulo dataset size.
- **Background Style Themes**: `VerseBackgroundStyle` defines 8 curated Islamic aesthetic themes including **Photo Background Images** (*Mosque Twilight, Makkah Kaaba, Medina Green Dome*) and **Color Gradients** (*Emerald, Twilight, Makkah Gold, Ocean Teal, Sunset Crimson*).
- **Card Preview Canvas**: `DailyVerseScreen` renders the verse card with Arabic typography, English translation, Surah & Ayah reference, topic badge, and discrete **MuslimCentre** watermark.
- **Social Sharing Integration**: Includes a **"Share as Status"** button with a confirmation dialog prepared for future WhatsApp Status, Instagram Story, and Facebook sharing, plus a **"Copy Text"** action.

### 3.4 Google AdMob Ads & Ad-Free Subscriptions Module
- **SDK & Initialization**: Google Mobile Ads SDK (`play-services-ads:25.5.0`) initialized via `AdManager.kt`.
- **Top Permanent Banner Ad Container**: [`TopBannerAdContainer`](file:///home/wesleyblock/AndroidStudioProjects/MuslimCentre/app/src/main/java/com/squarestudios/muslimcentre/ui/ads/BannerAdView.kt) is permanently placed at the top of [`MainScreen.kt`](file:///home/wesleyblock/AndroidStudioProjects/MuslimCentre/app/src/main/java/com/squarestudios/muslimcentre/ui/main/MainScreen.kt).
- **Ad-Free Subscription State**: `AdManager.kt` persists the `isAdFree` preference in `SharedPreferences`. When `isAdFree` is `true`, all top banner ads are animated out and completely hidden.
- **Remove Ads Subscription Dialog**: [`RemoveAdsDialog.kt`](file:///home/wesleyblock/AndroidStudioProjects/MuslimCentre/app/src/main/java/com/squarestudios/muslimcentre/ui/ads/RemoveAdsDialog.kt) provides Monthly ($0.99) and Yearly ($6.99) subscription plan options.

---

## 4. Project Directory & Source File Map

```
app/src/main/java/com/squarestudios/muslimcentre/
├── MainActivity.kt                      # App activity host entry point
├── data/
│   ├── ads/
│   │   └── AdManager.kt                 # MobileAds SDK & isAdFree preference manager
│   ├── compass/
│   │   └── CompassSensorManager.kt      # SensorEventListener for device orientation
│   ├── location/
│   │   └── LocationTracker.kt           # FusedLocationProviderClient & Geocoder wrapper
│   ├── model/
│   │   ├── PrayerTimeModel.kt           # Data classes for Prayer items, methods, madhabs
│   │   └── QuranVerseModel.kt           # Data classes for Quran verses & background themes
│   └── repository/
│       ├── PrayerTimesRepository.kt     # Adhan library wrapper & Hijri conversion
│       └── QuranVerseRepository.kt      # Curated Quran verses & daily selection logic
└── ui/
    ├── ads/
    │   ├── BannerAdView.kt              # AdMob Banner AdView Jetpack Compose wrapper
    │   └── RemoveAdsDialog.kt           # Subscription & Remove Ads dialog
    ├── dailyverse/
    │   ├── DailyVerseScreen.kt          # Jetpack Compose UI for Daily Verse & Card Canvas
    │   └── DailyVerseViewModel.kt       # ViewModel for Daily Verse selection & themes
    ├── main/
    │   └── MainScreen.kt                # Top Banner Ad & Bottom Nav Scaffold (3 tabs)
    ├── prayertimes/
    │   ├── PrayerTimesScreen.kt         # Jetpack Compose UI for Salaah Times
    │   └── PrayerTimesViewModel.kt      # ViewModel for Salaah Times state & ticker
    ├── qibla/
    │   ├── QiblaScreen.kt               # Jetpack Compose UI for Qibla Compass
    │   └── QiblaViewModel.kt            # ViewModel for Qibla state & sensor flow
    └── theme/
        ├── Color.kt                     # Application color tokens
        ├── Theme.kt                     # Material 3 MuslimCentre Theme definition
        └── Type.kt                      # Material 3 Typography definitions
```

---

## 5. Connecting Official AdMob Account (Production Steps)

Currently, the project uses **Google AdMob Sample Test IDs** so the app builds and renders test ads immediately. When linking your official AdMob account prior to publishing on Google Play, follow these 2 steps:

1. **Update AdMob Application ID in AndroidManifest.xml**:
   Open [`app/src/main/AndroidManifest.xml`](file:///home/wesleyblock/AndroidStudioProjects/MuslimCentre/app/src/main/AndroidManifest.xml) and replace the value in:
   ```xml
   <meta-data
       android:name="com.google.android.gms.ads.APPLICATION_ID"
       android:value="YOUR_OFFICIAL_ADMOB_APP_ID_HERE" />
   ```

2. **Update Banner Ad Unit ID in AdManager.kt**:
   Open [`app/src/main/java/com/squarestudios/muslimcentre/data/ads/AdManager.kt`](file:///home/wesleyblock/AndroidStudioProjects/MuslimCentre/app/src/main/java/com/squarestudios/muslimcentre/data/ads/AdManager.kt) and replace `TEST_BANNER_AD_UNIT_ID` with your official Ad Unit ID:
   ```kotlin
   const val TEST_BANNER_AD_UNIT_ID = "YOUR_OFFICIAL_BANNER_AD_UNIT_ID_HERE"
   ```

---

## 6. Android Manifest & Permissions

The following permissions are configured in [`app/src/main/AndroidManifest.xml`](file:///home/wesleyblock/AndroidStudioProjects/MuslimCentre/app/src/main/AndroidManifest.xml):

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

- `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`: Required for GPS geolocation to compute exact local prayer times and Qibla bearing.
- `INTERNET`: Required for reverse geocoding city/country name lookup via `Geocoder` and loading AdMob ads.

---

## 7. Developer Guidelines & Maintenance Workflows

### 7.1 Building & Testing the Project
- **Build Debug APK**:
  ```bash
  ./gradlew app:assembleDebug
  ```
- **Run Unit Tests**:
  ```bash
  ./gradlew app:testDebugUnitTest
  ```

### 7.2 Adding New Modules/Features
When introducing future features (e.g. *Quran Reader, Duas, Islamic Stories, Learn Arabic*):
1. **Data Layer**: Add new models in `data/model/` and repositories in `data/repository/`.
2. **UI Layer**: Create a new package under `ui/<feature_name>/` containing `<Feature>Screen.kt` and `<Feature>ViewModel.kt`.
3. **Navigation**: Add a new `NavigationBarItem` in [`MainScreen.kt`](file:///home/wesleyblock/AndroidStudioProjects/MuslimCentre/app/src/main/java/com/squarestudios/muslimcentre/ui/main/MainScreen.kt).
4. **Documentation**: Update [`CHANGELOG.md`](file:///home/wesleyblock/AndroidStudioProjects/MuslimCentre/CHANGELOG.md) with version bump and append the new feature module in this [`AS_BUILT.md`](file:///home/wesleyblock/AndroidStudioProjects/MuslimCentre/AS_BUILT.md) document.
