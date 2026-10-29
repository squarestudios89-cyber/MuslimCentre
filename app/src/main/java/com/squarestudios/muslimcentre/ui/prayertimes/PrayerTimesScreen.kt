package com.squarestudios.muslimcentre.ui.prayertimes

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.squarestudios.muslimcentre.data.model.AsrMadhab
import com.squarestudios.muslimcentre.data.model.CalculationMethodConfig
import com.squarestudios.muslimcentre.data.model.PrayerItem
import com.squarestudios.muslimcentre.data.model.PrayerTimesData
import com.squarestudios.muslimcentre.data.model.PrayerType

// Emerald Islamic Theme Colors
private val EmeraldDark = Color(0xFF06382B)
private val EmeraldMain = Color(0xFF0D5C46)
private val EmeraldLight = Color(0xFF148565)
private val GoldAccent = Color(0xFFE5C158)
private val GoldLight = Color(0xFFFFF7DF)
private val CardBg = Color(0xFFF6F8F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    modifier: Modifier = Modifier,
    viewModel: PrayerTimesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Location Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.any { it }
        viewModel.onPermissionResult(isGranted)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Muslim Centre",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Salaah Times",
                            fontSize = 12.sp,
                            color = GoldAccent
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadPrayerTimes() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Location",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Prayer Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EmeraldDark
                )
            )
        },
        containerColor = Color(0xFFF2F4F3),
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = EmeraldMain)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Calculating Salaah Times...",
                            color = EmeraldDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                val data = uiState.prayerTimesData
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    // Location Banner
                    item {
                        LocationHeader(
                            cityName = data?.locationName ?: "Unknown Location",
                            hasPermission = uiState.hasLocationPermission,
                            onRequestPermission = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        )
                    }

                    // Next Prayer Hero Card
                    item {
                        data?.let { prayerData ->
                            NextPrayerHeroCard(
                                prayerData = prayerData,
                                countdownText = uiState.countdownText
                            )
                        }
                    }

                    // Dates Card (Gregorian + Hijri)
                    item {
                        data?.let { prayerData ->
                            DateHeaderCard(
                                dateFormatted = prayerData.dateFormatted,
                                hijriDateFormatted = prayerData.hijriDateFormatted
                            )
                        }
                    }

                    // Section Title
                    item {
                        Text(
                            text = "Today's Prayer Schedule",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = EmeraldDark,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    // List of Prayer Times
                    data?.prayers?.let { prayers ->
                        items(prayers) { prayerItem ->
                            PrayerTimeCard(prayer = prayerItem)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    // Settings Dialog for Calculation Method & Madhab
    if (showSettingsDialog) {
        CalculationSettingsDialog(
            currentMethod = uiState.selectedMethod,
            currentMadhab = uiState.selectedMadhab,
            onMethodSelected = { viewModel.updateCalculationMethod(it) },
            onMadhabSelected = { viewModel.updateAsrMadhab(it) },
            onDismiss = { showSettingsDialog = false }
        )
    }
}

@Composable
fun LocationHeader(
    cityName: String,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = EmeraldMain,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = cityName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = EmeraldDark,
                    modifier = Modifier.weight(1f)
                )

                if (!hasPermission) {
                    TextButton(onClick = onRequestPermission) {
                        Text("Enable GPS", color = EmeraldLight, fontSize = 12.sp)
                    }
                }
            }

            AnimatedVisibility(visible = !hasPermission) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .background(GoldLight, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Using default location (Makkah). Grant GPS permission for exact local times.",
                        fontSize = 11.sp,
                        color = Color(0xFF6B5300)
                    )
                }
            }
        }
    }
}

@Composable
fun NextPrayerHeroCard(
    prayerData: PrayerTimesData,
    countdownText: String
) {
    val nextPrayer = prayerData.nextPrayer

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(EmeraldDark, EmeraldMain, EmeraldLight)
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top Tag
                Box(
                    modifier = Modifier
                        .background(GoldAccent, shape = RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "UPCOMING SALAAH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Prayer Names (English & Arabic)
                if (nextPrayer != null) {
                    Text(
                        text = "${nextPrayer.name} • ${nextPrayer.arabicName}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = nextPrayer.formattedTime,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Countdown Timer Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (countdownText.isNotEmpty()) "In $countdownText" else "Calculating...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeaderCard(
    dateFormatted: String,
    hijriDateFormatted: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dateFormatted,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = EmeraldDark
                )
                if (hijriDateFormatted.isNotEmpty()) {
                    Text(
                        text = hijriDateFormatted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = EmeraldLight
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerTimeCard(prayer: PrayerItem) {
    val isHighlight = prayer.isNext

    val cardBgColor = if (isHighlight) EmeraldMain else CardBg
    val textColor = if (isHighlight) Color.White else EmeraldDark
    val subTextColor = if (isHighlight) GoldAccent else Color.Gray

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHighlight) 4.dp else 1.dp
        ),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isHighlight) Modifier.border(
                    width = 2.dp,
                    color = GoldAccent,
                    shape = RoundedCornerShape(16.dp)
                ) else Modifier
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Prayer Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isHighlight) GoldAccent.copy(alpha = 0.2f)
                        else EmeraldMain.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getPrayerIcon(prayer.type),
                    contentDescription = prayer.name,
                    tint = if (isHighlight) GoldAccent else EmeraldMain,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Prayer Name & Arabic Name
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = prayer.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${prayer.arabicName})",
                        fontSize = 13.sp,
                        color = subTextColor
                    )
                }

                if (isHighlight) {
                    Text(
                        text = "Next Prayer",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldAccent
                    )
                }
            }

            // Time
            Text(
                text = prayer.formattedTime,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = textColor
            )
        }
    }
}

@Composable
fun getPrayerIcon(type: PrayerType): ImageVector {
    return when (type) {
        PrayerType.FAJR -> Icons.Default.WbTwilight
        PrayerType.SUNRISE -> Icons.Default.WbSunny
        PrayerType.DHUHR -> Icons.Default.WbSunny
        PrayerType.ASR -> Icons.Default.WbSunny
        PrayerType.MAGHRIB -> Icons.Default.WbTwilight
        PrayerType.ISHA -> Icons.Default.NightsStay
    }
}

@Composable
fun CalculationSettingsDialog(
    currentMethod: CalculationMethodConfig,
    currentMadhab: AsrMadhab,
    onMethodSelected: (CalculationMethodConfig) -> Unit,
    onMadhabSelected: (AsrMadhab) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Prayer Calculation Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = EmeraldDark
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "Calculation Method",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EmeraldMain,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(CalculationMethodConfig.entries) { method ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMethodSelected(method) }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (method == currentMethod),
                            onClick = { onMethodSelected(method) },
                            colors = RadioButtonDefaults.colors(selectedColor = EmeraldMain)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = method.displayName,
                            fontSize = 13.sp,
                            color = EmeraldDark
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Asr Juristic Method",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EmeraldMain,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(AsrMadhab.entries) { madhab ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMadhabSelected(madhab) }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (madhab == currentMadhab),
                            onClick = { onMadhabSelected(madhab) },
                            colors = RadioButtonDefaults.colors(selectedColor = EmeraldMain)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = madhab.displayName,
                            fontSize = 13.sp,
                            color = EmeraldDark
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
                Text("Done", color = Color.White)
            }
        }
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PrayerTimesScreenPreview() {
    val samplePrayers = listOf(
        PrayerItem(PrayerType.FAJR, "Fajr", "الفجر", "05:12 AM", 0L),
        PrayerItem(PrayerType.SUNRISE, "Sunrise", "الشروق", "06:34 AM", 0L),
        PrayerItem(PrayerType.DHUHR, "Dhuhr", "الظهر", "01:15 PM", 0L, isNext = true),
        PrayerItem(PrayerType.ASR, "Asr", "العصر", "04:30 PM", 0L),
        PrayerItem(PrayerType.MAGHRIB, "Maghrib", "المغرب", "07:05 PM", 0L),
        PrayerItem(PrayerType.ISHA, "Isha", "العشاء", "08:25 PM", 0L)
    )

    val sampleData = PrayerTimesData(
        locationName = "Cape Town, South Africa",
        latitude = -33.9249,
        longitude = 18.4241,
        dateFormatted = "Monday, 23 October 2026",
        hijriDateFormatted = "11 Rabi' al-Thani 1448 AH",
        prayers = samplePrayers,
        nextPrayer = samplePrayers[2]
    )

    com.squarestudios.muslimcentre.ui.theme.MuslimCentreTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F4F3))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LocationHeader(
                cityName = sampleData.locationName,
                hasPermission = true,
                onRequestPermission = {}
            )
            NextPrayerHeroCard(
                prayerData = sampleData,
                countdownText = "01h 14m 22s"
            )
            DateHeaderCard(
                dateFormatted = sampleData.dateFormatted,
                hijriDateFormatted = sampleData.hijriDateFormatted
            )
            samplePrayers.forEach { prayer ->
                PrayerTimeCard(prayer = prayer)
            }
        }
    }
}

