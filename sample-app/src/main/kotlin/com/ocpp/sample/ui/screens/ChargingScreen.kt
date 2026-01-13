package com.ocpp.sample.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ocpp.sample.ChargingState
import com.ocpp.sample.ui.components.*
import com.ocpp.sample.ui.theme.*

@Composable
fun ChargingScreen(
    chargingState: ChargingState,
    powerKw: Float,
    onSendMeterValues: () -> Unit,
    onStopCharging: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCharging = chargingState.isCharging
    val energyKwh = chargingState.energyKwh.toFloat()
    val costInr = energyKwh * 12f
    val sessionDuration = chargingState.durationMinutes
    val batteryProgress = (energyKwh / 50f).coerceIn(0f, 1f)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = if (isCharging) 
                            listOf(ElectricGreen, ElectricGreenDark)
                        else 
                            listOf(Color.Gray, Color.DarkGray)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = if (isCharging) "Charging in Progress" else "Ready to Charge",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (isCharging && chargingState.transactionId != null) {
                    Text(
                        text = "Transaction: ${chargingState.transactionId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            
            // Main charging indicator
            AnimatedVisibility(
                visible = isCharging,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ChargingProgressIndicator(
                        progress = batteryProgress,
                        isCharging = true,
                        size = 200.dp
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    EnergyFlowIndicator(powerKw = powerKw)
                }
            }
            
            AnimatedVisibility(
                visible = !isCharging,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EvStation,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color.Gray
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No Active Session",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Start a charging session from Home",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Stats Grid
            if (isCharging) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        icon = Icons.Default.Bolt,
                        value = "${String.format("%.1f", powerKw)}",
                        unit = "kW",
                        label = "Power",
                        color = ElectricGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Default.BatteryFull,
                        value = "${String.format("%.2f", energyKwh)}",
                        unit = "kWh",
                        label = "Energy",
                        color = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        icon = Icons.Default.Schedule,
                        value = formatDuration(sessionDuration),
                        unit = "",
                        label = "Duration",
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Default.CurrencyRupee,
                        value = "₹${String.format("%.0f", costInr)}",
                        unit = "",
                        label = "Cost",
                        color = ChargingYellow,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Pricing Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rate", color = Color.Gray)
                            Text("₹12.00/kWh", fontWeight = FontWeight.SemiBold)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Est. Full (50 kWh)", color = Color.Gray)
                            Text("₹600", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onSendMeterValues,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Speed, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Send Meter")
                    }
                    Button(
                        onClick = onStopCharging,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Icon(Icons.Default.Stop, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Stop")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    unit: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (unit.isNotEmpty()) {
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

private fun formatDuration(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
}
