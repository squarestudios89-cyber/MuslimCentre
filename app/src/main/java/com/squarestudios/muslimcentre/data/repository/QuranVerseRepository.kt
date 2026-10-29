package com.squarestudios.muslimcentre.data.repository

import com.squarestudios.muslimcentre.data.model.QuranVerse
import java.util.Calendar

class QuranVerseRepository {

    private val verses = listOf(
        QuranVerse(
            id = 1,
            surahName = "Ash-Sharh",
            surahArabicName = "الشرح",
            surahNumber = 94,
            ayahNumber = 5,
            arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا • إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            englishTranslation = "For indeed, with hardship comes ease. Indeed, with hardship comes ease.",
            topicTag = "Hope & Ease"
        ),
        QuranVerse(
            id = 2,
            surahName = "Al-Baqarah",
            surahArabicName = "البقرة",
            surahNumber = 2,
            ayahNumber = 286,
            arabicText = "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا",
            englishTranslation = "Allah does not charge a soul except with that within its capacity.",
            topicTag = "Patience & Trust"
        ),
        QuranVerse(
            id = 3,
            surahName = "Ar-Ra'd",
            surahArabicName = "الرعد",
            surahNumber = 13,
            ayahNumber = 28,
            arabicText = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            englishTranslation = "Unquestionably, by the remembrance of Allah do hearts find rest.",
            topicTag = "Peace & Remembrance"
        ),
        QuranVerse(
            id = 4,
            surahName = "Az-Zumar",
            surahArabicName = "الزمر",
            surahNumber = 39,
            ayahNumber = 53,
            arabicText = "لَا تَقْنَطُوا مِن رَّحْمَةِ اللَّهِ ۚ إِنَّ اللَّهَ يَغْفِرُ الذُّنُوبَ جَمِيعًا",
            englishTranslation = "Do not despair of the mercy of Allah. Indeed, Allah forgives all sins.",
            topicTag = "Forgiveness & Mercy"
        ),
        QuranVerse(
            id = 5,
            surahName = "Al-Baqarah",
            surahArabicName = "البقرة",
            surahNumber = 2,
            ayahNumber = 186,
            arabicText = "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ ۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ",
            englishTranslation = "When My servants ask you concerning Me, indeed I am near. I respond to the call of the caller when he calls upon Me.",
            topicTag = "Dua & Closeness"
        ),
        QuranVerse(
            id = 6,
            surahName = "Al-Baqarah",
            surahArabicName = "البقرة",
            surahNumber = 2,
            ayahNumber = 152,
            arabicText = "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ",
            englishTranslation = "So remember Me; I will remember you. And be grateful to Me and do not deny Me.",
            topicTag = "Gratitude & Zikr"
        ),
        QuranVerse(
            id = 7,
            surahName = "Al-Anbiya",
            surahArabicName = "الأنبياء",
            surahNumber = 21,
            ayahNumber = 87,
            arabicText = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            englishTranslation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            topicTag = "Supplication"
        ),
        QuranVerse(
            id = 8,
            surahName = "Ali 'Imran",
            surahArabicName = "آل عمران",
            surahNumber = 3,
            ayahNumber = 139,
            arabicText = "وَلَا تَهِنُوا وَلَا تَحْزَنُوا وَأَنتُمُ الْأَعْلَوْنَ إِن كُنتُم مُّؤْمِنِينَ",
            englishTranslation = "Do not weaken and do not grieve, and you will be superior if you are true believers.",
            topicTag = "Strength & Faith"
        ),
        QuranVerse(
            id = 9,
            surahName = "Ar-Rahman",
            surahArabicName = "الرحمن",
            surahNumber = 55,
            ayahNumber = 13,
            arabicText = "فَبِأَيِّ آلَاءِ رَبِّكُمَا تُكَذِّبَانِ",
            englishTranslation = "So which of the favors of your Lord would you deny?",
            topicTag = "Blessings & Reflection"
        ),
        QuranVerse(
            id = 10,
            surahName = "At-Tawbah",
            surahArabicName = "التوبة",
            surahNumber = 9,
            ayahNumber = 51,
            arabicText = "قُل لَّن يُصِيبَنَا إِلَّا مَا كَتَبَ اللَّهُ لَنَا هُوَ مَوْلَانَا ۚ وَعَلَى اللَّهِ فَلْيَتَوَكَّلِ الْمُؤْمِنُونَ",
            englishTranslation = "Say, 'Never will we be struck except by what Allah has decreed for us; He is our protector.' And upon Allah let the believers rely.",
            topicTag = "Reliance on Allah"
        )
    )

    fun getTodayVerse(): QuranVerse {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % verses.size
        return verses[index]
    }

    fun getAllVerses(): List<QuranVerse> = verses

    fun getVerseById(id: Int): QuranVerse {
        return verses.find { it.id == id } ?: verses.first()
    }
}
