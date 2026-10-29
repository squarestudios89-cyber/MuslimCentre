package com.squarestudios.muslimcentre.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.squarestudios.muslimcentre.R

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

enum class BackgroundType {
    GRADIENT,
    IMAGE
}

enum class VerseBackgroundStyle(
    val id: String,
    val title: String,
    val type: BackgroundType,
    val gradientColors: List<Color>,
    val textColor: Color,
    val accentColor: Color,
    @param:DrawableRes val imageResId: Int? = null
) {
    EMERALD_GEOMETRY(
        id = "emerald",
        title = "Emerald",
        type = BackgroundType.GRADIENT,
        gradientColors = listOf(Color(0xFF04291F), Color(0xFF0D5C46), Color(0xFF148565)),
        textColor = Color.White,
        accentColor = Color(0xFFE5C158)
    ),
    MOSQUE_SILHOUETTE(
        id = "mosque_image",
        title = "Mosque Twilight",
        type = BackgroundType.IMAGE,
        gradientColors = listOf(Color(0xFF1A0B2E), Color(0xFF38154C)),
        textColor = Color.White,
        accentColor = Color(0xFFFFD54F),
        imageResId = R.drawable.bg_mosque_silhouette
    ),
    MAKKAH_KAABA(
        id = "makkah_image",
        title = "Makkah Kaaba",
        type = BackgroundType.IMAGE,
        gradientColors = listOf(Color(0xFF1A120B), Color(0xFF3E2B15)),
        textColor = Color(0xFFFFF7DF),
        accentColor = Color(0xFFE5C158),
        imageResId = R.drawable.bg_makkah_kaaba
    ),
    MEDINA_DOME(
        id = "medina_image",
        title = "Medina Dome",
        type = BackgroundType.IMAGE,
        gradientColors = listOf(Color(0xFF031F17), Color(0xFF135E48)),
        textColor = Color.White,
        accentColor = Color(0xFFE5C158),
        imageResId = R.drawable.bg_medina_dome
    ),
    TWILIGHT_MOSQUE(
        id = "twilight",
        title = "Twilight",
        type = BackgroundType.GRADIENT,
        gradientColors = listOf(Color(0xFF0B1B3D), Color(0xFF1B365D), Color(0xFF2C4D75)),
        textColor = Color.White,
        accentColor = Color(0xFF82B1FF)
    ),
    MAKKAH_GOLD(
        id = "gold",
        title = "Gold Minimal",
        type = BackgroundType.GRADIENT,
        gradientColors = listOf(Color(0xFF1F1A0F), Color(0xFF3B3019), Color(0xFF5C4B27)),
        textColor = Color(0xFFFFF7DF),
        accentColor = Color(0xFFE5C158)
    ),
    OCEAN_SERENITY(
        id = "ocean",
        title = "Ocean Teal",
        type = BackgroundType.GRADIENT,
        gradientColors = listOf(Color(0xFF003333), Color(0xFF004D4D), Color(0xFF008080)),
        textColor = Color.White,
        accentColor = Color(0xFF80CBC4)
    ),
    SUNSET_WARMTH(
        id = "sunset",
        title = "Sunset Crimson",
        type = BackgroundType.GRADIENT,
        gradientColors = listOf(Color(0xFF2B0B0E), Color(0xFF4A151B), Color(0xFF7A202A)),
        textColor = Color.White,
        accentColor = Color(0xFFFFB74D)
    )
}
