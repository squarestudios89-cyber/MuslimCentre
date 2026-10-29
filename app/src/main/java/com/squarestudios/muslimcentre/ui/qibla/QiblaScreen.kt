package com.squarestudios.muslimcentre.ui.qibla

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalLifecycleOwner
import kotlin.math.cos
import kotlin.math.sin

private val EmeraldDark = Color(0xFF06382B)
private val EmeraldMain = Color(0xFF0D5C46)
private val GoldAccent = Color(0xFFE5C158)
private val GoldLight = Color(0xFFFFF7DF)
private val CardBg = Color(0xFFF6F8F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    modifier: Modifier = Modifier,
    viewModel: QiblaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptic = LocalHapticFeedback.current

    // Register sensor listener during lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.startListening()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.stopListening()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopListening()
        }
    }

    // Trigger subtle haptic vibration when user aligns phone with Qibla
    LaunchedEffect(uiState.isAligned) {
        if (uiState.isAligned) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Qibla Direction",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Kibla Compass",
                            fontSize = 12.sp,
                            color = GoldAccent
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadLocationAndQibla() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Location",
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
                            text = "Locating Qibla Direction...",
                            color = EmeraldDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Location & Bearing Card
                    LocationAndBearingCard(
                        locationName = uiState.locationName,
                        qiblaBearing = uiState.qiblaBearing,
                        distanceKm = uiState.distanceToMakkahKm
                    )

                    // Alignment Banner
                    AlignmentStatusCard(isAligned = uiState.isAligned)

                    // Qibla Compass Visual
                    QiblaCompassWidget(
                        azimuth = uiState.azimuth,
                        qiblaBearing = uiState.qiblaBearing,
                        isAligned = uiState.isAligned
                    )

                    // Calibration Info Card
                    CompassCalibrationCard(hasSensor = uiState.hasCompassSensor)
                }
            }
        }
    }
}

@Composable
fun LocationAndBearingCard(
    locationName: String,
    qiblaBearing: Float,
    distanceKm: Int
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = EmeraldMain,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = locationName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EmeraldDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Qibla Bearing",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "%.1f° N".format(qiblaBearing),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = EmeraldMain
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Distance to Makkah",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "%,d km".format(distanceKm),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = EmeraldDark
                    )
                }
            }
        }
    }
}

@Composable
fun AlignmentStatusCard(isAligned: Boolean) {
    val bgColor = if (isAligned) EmeraldMain else GoldLight
    val contentColor = if (isAligned) Color.White else Color(0xFF6B5300)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(
                imageVector = if (isAligned) Icons.Default.CheckCircle else Icons.Default.Explore,
                contentDescription = null,
                tint = if (isAligned) GoldAccent else Color(0xFF997A00),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = if (isAligned) "YOU ARE FACING KAABA!" else "Rotate your phone to align with Qibla",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = contentColor
                )
                Text(
                    text = if (isAligned) "Your phone is accurately aligned towards Makkah." else "Point top of phone towards the Kaaba pointer.",
                    fontSize = 12.sp,
                    color = contentColor.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun QiblaCompassWidget(
    azimuth: Float,
    qiblaBearing: Float,
    isAligned: Boolean
) {
    val animatedAzimuth by animateFloatAsState(
        targetValue = -azimuth,
        animationSpec = spring(stiffness = 150f),
        label = "compassRotation"
    )

    val ringBorderColor = if (isAligned) GoldAccent else EmeraldMain
    val glowAlpha = if (isAligned) 0.3f else 0.05f

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(300.dp)
            .padding(12.dp)
    ) {
        // Outer Glow Background
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ringBorderColor.copy(alpha = glowAlpha), Color.Transparent)
                    )
                )
        )

        // Outer Ring Dial
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
                .background(EmeraldDark)
                .border(width = 4.dp, color = ringBorderColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Rotating Compass Rose & Degrees
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatedAzimuth)
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2 - 20.dp.toPx()

                // Draw Graduation Ticks
                for (i in 0 until 360 step 15) {
                    val angleRad = Math.toRadians(i.toDouble() - 90)
                    val lineLength = if (i % 90 == 0) 16.dp.toPx() else 8.dp.toPx()
                    val strokeWidth = if (i % 90 == 0) 3.dp.toPx() else 1.5.dp.toPx()

                    val start = Offset(
                        (center.x + (radius - lineLength) * cos(angleRad)).toFloat(),
                        (center.y + (radius - lineLength) * sin(angleRad)).toFloat()
                    )
                    val end = Offset(
                        (center.x + radius * cos(angleRad)).toFloat(),
                        (center.y + radius * sin(angleRad)).toFloat()
                    )

                    drawLine(
                        color = if (i == 0) Color.Red else Color.White.copy(alpha = 0.6f),
                        start = start,
                        end = end,
                        strokeWidth = strokeWidth
                    )
                }

                // Draw N, E, S, W text labels
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 36f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }

                val labelRadius = radius - 28.dp.toPx()
                val labels = listOf("N" to 0, "E" to 90, "S" to 180, "W" to 270)
                labels.forEach { (label, angle) ->
                    val angleRad = Math.toRadians(angle.toDouble() - 90)
                    val x = (center.x + labelRadius * cos(angleRad)).toFloat()
                    val y = (center.y + labelRadius * sin(angleRad)).toFloat() + 12f
                    paint.color = if (label == "N") android.graphics.Color.RED else android.graphics.Color.WHITE
                    drawContext.canvas.nativeCanvas.drawText(label, x, y, paint)
                }
            }

            // Rotating Qibla Needle Pointer towards Makkah
            val qiblaNeedleAngle = animatedAzimuth + qiblaBearing

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(qiblaNeedleAngle),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 110.dp)
                ) {
                    // Kaaba Badge Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldAccent)
                            .border(2.dp, EmeraldDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🕋",
                            fontSize = 18.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Qibla Pointer",
                        tint = GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Center Pin
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(GoldAccent)
                    .border(2.dp, EmeraldDark, CircleShape)
            )
        }
    }
}

@Composable
fun CompassCalibrationCard(hasSensor: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = EmeraldMain,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hasSensor) {
                    "Tip: Move your phone in a figure-8 pattern if compass accuracy needs calibration."
                } else {
                    "Note: Compass hardware sensor not detected on device. Map bearing displayed."
                },
                fontSize = 12.sp,
                color = EmeraldDark
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QiblaScreenPreview() {
    com.squarestudios.muslimcentre.ui.theme.MuslimCentreTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F4F3))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LocationAndBearingCard(
                locationName = "Cape Town, South Africa",
                qiblaBearing = 58.4f,
                distanceKm = 6820
            )

            AlignmentStatusCard(isAligned = true)

            QiblaCompassWidget(
                azimuth = 58.4f,
                qiblaBearing = 58.4f,
                isAligned = true
            )

            CompassCalibrationCard(hasSensor = true)
        }
    }
}
