package com.squarestudios.muslimcentre.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squarestudios.muslimcentre.data.ads.AdManager
import com.squarestudios.muslimcentre.ui.ads.RemoveAdsDialog
import com.squarestudios.muslimcentre.ui.ads.TopBannerAdContainer
import com.squarestudios.muslimcentre.ui.dailyverse.DailyVerseScreen
import com.squarestudios.muslimcentre.ui.prayertimes.PrayerTimesScreen
import com.squarestudios.muslimcentre.ui.qibla.QiblaScreen

private val EmeraldDark = Color(0xFF06382B)
private val GoldAccent = Color(0xFFE5C158)

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val adManager = remember { AdManager(context) }
    val isAdFree by adManager.isAdFree.collectAsState()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showRemoveAdsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            // Permanent Top Banner Ad (shown unless user is Ad-Free subscriber)
            TopBannerAdContainer(
                isAdFree = isAdFree,
                onRemoveAdsClick = { showRemoveAdsDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = EmeraldDark,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Salaah Times"
                        )
                    },
                    label = {
                        Text(
                            text = "Salaah Times",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Qibla Direction"
                        )
                    },
                    label = {
                        Text(
                            text = "Qibla",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Daily Verse"
                        )
                    },
                    label = {
                        Text(
                            text = "Daily Verse",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> PrayerTimesScreen()
                1 -> QiblaScreen()
                2 -> DailyVerseScreen()
            }
        }
    }

    // Subscription / Remove Ads Dialog
    if (showRemoveAdsDialog) {
        RemoveAdsDialog(
            onSubscribeSuccess = {
                adManager.setAdFree(true)
                Toast.makeText(context, "Congratulations! Ad-Free Pass Activated.", Toast.LENGTH_LONG).show()
            },
            onDismiss = { showRemoveAdsDialog = false }
        )
    }
}
