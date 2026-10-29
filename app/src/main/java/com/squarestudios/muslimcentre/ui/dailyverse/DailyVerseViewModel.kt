package com.squarestudios.muslimcentre.ui.dailyverse

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.squarestudios.muslimcentre.data.model.QuranVerse
import com.squarestudios.muslimcentre.data.model.VerseBackgroundStyle
import com.squarestudios.muslimcentre.data.repository.QuranVerseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DailyVerseUiState(
    val currentVerse: QuranVerse,
    val selectedBackground: VerseBackgroundStyle = VerseBackgroundStyle.EMERALD_GEOMETRY,
    val allVerses: List<QuranVerse> = emptyList(),
    val currentIndex: Int = 0,
    val copiedToClipboard: Boolean = false,
    val isShareInitiated: Boolean = false
)

class DailyVerseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuranVerseRepository()

    private val _uiState: MutableStateFlow<DailyVerseUiState>

    val uiState: StateFlow<DailyVerseUiState>

    init {
        val today = repository.getTodayVerse()
        val all = repository.getAllVerses()
        val idx = all.indexOfFirst { it.id == today.id }.coerceAtLeast(0)

        _uiState = MutableStateFlow(
            DailyVerseUiState(
                currentVerse = today,
                allVerses = all,
                currentIndex = idx
            )
        )
        uiState = _uiState.asStateFlow()
    }

    fun selectBackgroundStyle(style: VerseBackgroundStyle) {
        _uiState.update { it.copy(selectedBackground = style) }
    }

    fun nextVerse() {
        val state = _uiState.value
        val nextIdx = (state.currentIndex + 1) % state.allVerses.size
        _uiState.update {
            it.copy(
                currentIndex = nextIdx,
                currentVerse = state.allVerses[nextIdx],
                copiedToClipboard = false,
                isShareInitiated = false
            )
        }
    }

    fun previousVerse() {
        val state = _uiState.value
        val prevIdx = if (state.currentIndex - 1 < 0) state.allVerses.size - 1 else state.currentIndex - 1
        _uiState.update {
            it.copy(
                currentIndex = prevIdx,
                currentVerse = state.allVerses[prevIdx],
                copiedToClipboard = false,
                isShareInitiated = false
            )
        }
    }

    fun copyVerseText(context: Context) {
        val verse = _uiState.value.currentVerse
        val shareText = "\"${verse.arabicText}\"\n\n${verse.englishTranslation}\n\n— ${verse.referenceText}\n(MuslimCentre)"

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Quran Verse", shareText)
        clipboard.setPrimaryClip(clip)

        _uiState.update { it.copy(copiedToClipboard = true) }
    }

    fun onShareClick() {
        _uiState.update { it.copy(isShareInitiated = true) }
    }

    fun resetShareStatus() {
        _uiState.update { it.copy(isShareInitiated = false, copiedToClipboard = false) }
    }
}
