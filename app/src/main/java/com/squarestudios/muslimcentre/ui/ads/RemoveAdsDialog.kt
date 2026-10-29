package com.squarestudios.muslimcentre.ui.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val EmeraldDark = Color(0xFF06382B)
private val EmeraldMain = Color(0xFF0D5C46)
private val GoldAccent = Color(0xFFE5C158)
private val GoldLight = Color(0xFFFFF7DF)

@Composable
fun RemoveAdsDialog(
    onSubscribeSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPlan by remember { mutableIntStateOf(1) } // 1 for Yearly, 0 for Monthly

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Go Ad-Free",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = EmeraldDark
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Enjoy MuslimCentre with zero banner ads & full focus.",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )

                // Benefits List
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GoldLight, shape = RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    BenefitRow("Remove top banner ads permanently")
                    BenefitRow("100% distraction-free experience")
                    BenefitRow("Support future features (Quran, Duas, Stories)")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Choose Subscription Plan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EmeraldMain
                )

                // Plan 1: Yearly
                PlanSelectionCard(
                    title = "Yearly Unlimited Pass",
                    price = "$6.99 / year",
                    badge = "SAVE 40%",
                    isSelected = selectedPlan == 1,
                    onClick = { selectedPlan = 1 }
                )

                // Plan 0: Monthly
                PlanSelectionCard(
                    title = "Monthly Ad-Free Pass",
                    price = "$0.99 / month",
                    badge = null,
                    isSelected = selectedPlan == 0,
                    onClick = { selectedPlan = 0 }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubscribeSuccess()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldMain),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (selectedPlan == 1) "Subscribe Yearly ($6.99)" else "Subscribe Monthly ($0.99)",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Maybe Later", color = Color.Gray)
            }
        }
    )
}

@Composable
fun BenefitRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = EmeraldMain,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = EmeraldDark
        )
    }
}

@Composable
fun PlanSelectionCard(
    title: String,
    price: String,
    badge: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GoldLight else Color(0xFFF6F8F7)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(
                    width = 2.dp,
                    color = GoldAccent,
                    shape = RoundedCornerShape(12.dp)
                ) else Modifier
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = EmeraldMain)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldDark
                    )
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(EmeraldMain, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }
                    }
                }

                Text(
                    text = price,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldMain
                )
            }
        }
    }
}
