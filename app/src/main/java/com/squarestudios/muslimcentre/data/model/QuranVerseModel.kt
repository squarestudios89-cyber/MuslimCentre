package com.squarestudios.muslimcentre.data.model

import androidx.compose.ui.graphics.Color

data class QuranVerse(
    val id: Int,
    val surahName: String,
    val surahArabicName: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val arabicText: String,
    val englishTranslation: String,
    val topicTag: String
) {
    val referenceText: String
        get() = "Surah $surahName ($surahNumber:$ayahNumber)"
}

enum class VerseBackgroundStyle(
    val id: String,
    val title: String,
    val gradientColors: List<Color>,
    val textColor: Color,
    val accentColor: Color,
    val isDark: Boolean = true
) {
    EMERALD_GEOMETRY(
        id = "emerald",
        title = "Emerald Islamic",
        gradientColors = listOf(Color(0xFF04291F), Color(0xFF0D5C46), Color(0xFF148565)),
        textColor = Color.White,
        accentColor = Color(0xFFE5C158)
    ),
    TWILIGHT_MOSQUE(
        id = "twilight",
        title = "Twilight Midnight",
        gradientColors = listOf(Color(0xFF0B1B3D), Color(0xFF1B365D), Color(0xFF2C4D75)),
        textColor = Color.White,
        accentColor = Color(0xFF82B1FF)
    ),
    MAKKAH_GOLD(
        id = "gold",
        title = "Makkah Gold",
        gradientColors = listOf(Color(0xFF1F1A0F), Color(0xFF3B3019), Color(0xFF5C4B27)),
        textColor = Color(0xFFFFF7DF),
        accentColor = Color(0xFFE5C158)
    ),
    OCEAN_SERENITY(
        id = "ocean",
        title = "Ocean Teal",
        gradientColors = listOf(Color(0xFF003333), Color(0xFF004D4D), Color(0xFF008080)),
        textColor = Color.White,
        accentColor = Color(0xFF80CBC4)
    ),
    SUNSET_WARMTH(
        id = "sunset",
        title = "Sunset Crimson",
        gradientColors = listOf(Color(0xFF2B0B0E), Color(0xFF4A151B), Color(0xFF7A202A)),
        textColor = Color.White,
        accentColor = Color(0xFFFFB74D)
    )
}
