package com.ocpp.sample.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ocpp.core.transport.ConnectionState
import com.ocpp.sample.ChargingState
import com.ocpp.sample.ui.theme.ElectricGreen
import com.ocpp.sample.ui.theme.ElectricGreenDark
import com.ocpp.sample.ui.theme.ChargingYellow

@Composable
fun HomeScreen(
    connectionState: ConnectionState,
    chargingState: ChargingState,
    url: String,
    onUrlChange: (String) -> Unit,
    chargePointId: String,
    onChargePointIdChange: (String) -> Unit,
    idToken: String,
    onIdTokenChange: (String) -> Unit,
    powerKw: String,
    onPowerKwChange: (String) -> Unit,
    isAcCharging: Boolean,
    onChargingTypeChange: (Boolean) -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onBootNotification: () -> Unit,
    onAuthorize: () -> Unit,
    onStartCharging: () -> Unit,
    onNavigateToCharging: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = connectionState == ConnectionState.Connected
    val isCharging = chargingState.isCharging
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(ElectricGreen, ElectricGreenDark)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OCPP Charger",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = chargePointId,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // Status Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isCharging -> ChargingYellow
                        isConnected -> Color.White
                        else -> Color.White.copy(alpha = 0.3f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                isCharging -> Icons.Default.BatteryChargingFull
                                isConnected -> Icons.Default.CheckCircle
                                else -> Icons.Default.CloudOff
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isConnected || isCharging) ElectricGreenDark else Color.White
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = when {
                                isCharging -> "Charging"
                                isConnected -> "Online"
                                else -> "Offline"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isConnected || isCharging) ElectricGreenDark else Color.White
                        )
                    }
                }
            }
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Session Card
            if (isCharging) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ElectricGreen)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Session",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatItem("${String.format("%.2f", chargingState.energyKwh)}", "kWh", Color.White)
                            StatItem("₹${String.format("%.0f", chargingState.energyKwh * 12)}", "Cost", Color.White)
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToCharging,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = ElectricGreen
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("View Details", color = ElectricGreen)
                        }
                    }
                }
            }
            
            // Connection Card
            SectionCard(
                title = "Server Connection",
                icon = Icons.Default.Cloud
            ) {
                OutlinedTextField(
                    value = url,
                    onValueChange = onUrlChange,
                    label = { Text("CSMS URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isConnected,
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = chargePointId,
                    onValueChange = onChargePointIdChange,
                    label = { Text("Charger ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isConnected,
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onConnect,
                        modifier = Modifier.weight(1f),
                        enabled = !isConnected,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen)
                    ) {
                        Icon(Icons.Default.Power, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Connect")
                    }
                    OutlinedButton(
                        onClick = onDisconnect,
                        modifier = Modifier.weight(1f),
                        enabled = isConnected,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Disconnect")
                    }
                }
            }
            
            // Charger Settings Card
            SectionCard(
                title = "Charger Settings",
                icon = Icons.Default.Tune
            ) {
                Text("Charging Type", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = isAcCharging,
                        onClick = { onChargingTypeChange(true) },
                        label = { Text("AC Charging") },
                        enabled = !isCharging,
                        leadingIcon = if (isAcCharging) {
                            { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                        } else null
                    )
                    FilterChip(
                        selected = !isAcCharging,
                        onClick = { onChargingTypeChange(false) },
                        label = { Text("DC Fast") },
                        enabled = !isCharging,
                        leadingIcon = if (!isAcCharging) {
                            { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                        } else null
                    )
                }
                
                Spacer(Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = powerKw,
                    onValueChange = onPowerKwChange,
                    label = { Text("Power (kW)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isCharging,
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        Text(if (isAcCharging) "AC: 3.3-22 kW" else "DC: 25-350 kW")
                    }
                )
                
                Spacer(Modifier.height(8.dp))
                
                Button(
                    onClick = onBootNotification,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isConnected && !isCharging,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AppRegistration, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Register Charger")
                }
            }
            
            // Customer Card
            SectionCard(
                title = "Customer",
                icon = Icons.Default.Person
            ) {
                OutlinedTextField(
                    value = idToken,
                    onValueChange = onIdTokenChange,
                    label = { Text("ID Token (RFID/App)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isCharging,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.CreditCard, null) }
                )
                
                Spacer(Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onAuthorize,
                        modifier = Modifier.weight(1f),
                        enabled = isConnected && !isCharging,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Authorize")
                    }
                    Button(
                        onClick = onStartCharging,
                        modifier = Modifier.weight(1f),
                        enabled = isConnected && !isCharging,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Start")
                    }
                }
            }
            
            // Pricing Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ChargingYellow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyRupee,
                                contentDescription = null,
                                tint = ChargingYellow
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Pricing", style = MaterialTheme.typography.titleMedium)
                            Text("Standard Rate", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                    Text(
                        text = "₹12/kWh",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElectricGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}
