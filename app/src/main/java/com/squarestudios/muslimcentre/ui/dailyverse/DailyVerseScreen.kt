package com.squarestudios.muslimcentre.ui.dailyverse

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.squarestudios.muslimcentre.data.model.QuranVerse
import com.squarestudios.muslimcentre.data.model.VerseBackgroundStyle

private val EmeraldDark = Color(0xFF06382B)
private val EmeraldMain = Color(0xFF0D5C46)
private val GoldAccent = Color(0xFFE5C158)
private val CardBg = Color(0xFFF6F8F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyVerseScreen(
    modifier: Modifier = Modifier,
    viewModel: DailyVerseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.copiedToClipboard) {
        if (uiState.copiedToClipboard) {
            snackbarHostState.showSnackbar("Verse text copied to clipboard!")
            viewModel.resetShareStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Daily Quran Verse",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "آية اليوم • Inspiration of the Day",
                            fontSize = 12.sp,
                            color = GoldAccent
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.nextVerse() }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Random Verse",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EmeraldDark
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF2F4F3),
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Verse Card Canvas Preview
            VerseCardCanvas(
                verse = uiState.currentVerse,
                style = uiState.selectedBackground
            )

            // Background Theme Selector Section
            BackgroundThemeSelector(
                selectedStyle = uiState.selectedBackground,
                onStyleSelected = { viewModel.selectBackgroundStyle(it) }
            )

            // Main Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.onShareClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldMain),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share as Status",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.copyVerseText(context) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = EmeraldDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Copy Text",
                        color = EmeraldDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Verse Navigation Controls (Previous / Next)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { viewModel.previousVerse() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous",
                        tint = EmeraldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Previous Verse", color = EmeraldDark, fontSize = 13.sp)
                }

                Text(
                    text = "${uiState.currentIndex + 1} of ${uiState.allVerses.size}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                TextButton(onClick = { viewModel.nextVerse() }) {
                    Text("Next Verse", color = EmeraldDark, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = EmeraldDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // Share Dialog Placeholder
    if (uiState.isShareInitiated) {
        ShareVerseDialog(
            verse = uiState.currentVerse,
            style = uiState.selectedBackground,
            onDismiss = { viewModel.resetShareStatus() }
        )
    }
}

@Composable
fun VerseCardCanvas(
    verse: QuranVerse,
    style: VerseBackgroundStyle
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = style.accentColor, shape = RoundedCornerShape(20.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.verticalGradient(colors = style.gradientColors))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top Tag & Theme Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = style.accentColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "VERSE OF THE DAY • ${verse.topicTag.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = style.accentColor,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = style.accentColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Arabic Text
                Text(
                    text = verse.arabicText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.textColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Divider Line
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(style.accentColor.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.height(18.dp))

                // English Translation Text
                Text(
                    text = "\"${verse.englishTranslation}\"",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = style.textColor.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Footer: Reference & Watermark
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "— ${verse.referenceText}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = style.accentColor
                    )

                    Text(
                        text = "MuslimCentre",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = style.textColor.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun BackgroundThemeSelector(
    selectedStyle: VerseBackgroundStyle,
    onStyleSelected: (VerseBackgroundStyle) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = EmeraldMain,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Select Background Theme",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EmeraldDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(VerseBackgroundStyle.entries) { style ->
                    val isSelected = (style == selectedStyle)
                    val borderAnimColor by animateColorAsState(
                        targetValue = if (isSelected) GoldAccent else Color.Transparent,
                        label = "borderAnim"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onStyleSelected(style) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.verticalGradient(colors = style.gradientColors)
                                )
                                .border(width = 3.dp, color = borderAnimColor, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = style.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = style.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) EmeraldDark else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShareVerseDialog(
    verse: QuranVerse,
    style: VerseBackgroundStyle,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = EmeraldMain
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share Daily Verse",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = EmeraldDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Your custom verse card with the '${style.title}' theme is ready to share!",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmeraldDark, shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "WhatsApp Status / Instagram Story / Facebook",
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "\"${verse.englishTranslation}\" — ${verse.referenceText}",
                            color = Color.White,
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldMain)
            ) {
                Text("Ready to Share", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DailyVerseScreenPreview() {
    val sampleVerse = QuranVerse(
        id = 1,
        surahName = "Ash-Sharh",
        surahArabicName = "الشرح",
        surahNumber = 94,
        ayahNumber = 5,
        arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا • إِنَّ مَعَ الْعُسْرِ يُسْرًا",
        englishTranslation = "For indeed, with hardship comes ease. Indeed, with hardship comes ease.",
        topicTag = "Hope & Ease"
    )

    com.squarestudios.muslimcentre.ui.theme.MuslimCentreTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F4F3))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VerseCardCanvas(
                verse = sampleVerse,
                style = VerseBackgroundStyle.EMERALD_GEOMETRY
            )

            BackgroundThemeSelector(
                selectedStyle = VerseBackgroundStyle.EMERALD_GEOMETRY,
                onStyleSelected = {}
            )
        }
    }
}
