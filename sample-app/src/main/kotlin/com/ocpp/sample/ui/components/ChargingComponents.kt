package com.ocpp.sample.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ocpp.sample.ui.theme.ElectricGreen
import com.ocpp.sample.ui.theme.ElectricGreenDark
import com.ocpp.sample.ui.theme.ChargingYellow

/**
 * Animated circular charging progress indicator with pulsing effect
 */
@Composable
fun ChargingProgressIndicator(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    strokeWidth: Dp = 12.dp,
    isCharging: Boolean = true
) {
    // Pulsing animation for the glow effect
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    // Sweeping animation for charging effect
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "progress"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
            val topLeft = Offset(strokePx / 2, strokePx / 2)
            
            // Background track
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            
            // Progress arc with gradient
            if (animatedProgress > 0) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(ElectricGreen, ElectricGreenDark, ChargingYellow, ElectricGreen)
                    ),
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
            
            // Pulsing glow effect when charging
            if (isCharging) {
                drawArc(
                    color = ElectricGreen.copy(alpha = pulseAlpha * 0.3f),
                    startAngle = -90f + sweepAngle,
                    sweepAngle = 30f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx + 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        
        // Center content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (isCharging) ElectricGreen else MaterialTheme.colorScheme.onSurface
            )
            if (isCharging) {
                Text(
                    text = "⚡ Charging",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricGreen
                )
            }
        }
    }
}

/**
 * Animated energy flow indicator showing power transfer
 */
@Composable
fun EnergyFlowIndicator(
    powerKw: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flow")
    val flowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flowOffset"
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated flow dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔌", style = MaterialTheme.typography.headlineMedium)
            
            // Flow animation
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(5) { index ->
                    val dotAlpha = ((flowOffset + index * 0.2f) % 1f)
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(
                            color = ElectricGreen.copy(alpha = dotAlpha),
                            radius = size.minDimension / 2
                        )
                    }
                }
            }
            
            Text("🚗", style = MaterialTheme.typography.headlineMedium)
        }
        
        Spacer(Modifier.height(8.dp))
        
        Text(
            text = "${String.format("%.1f", powerKw)} kW",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ElectricGreen
        )
        Text(
            text = "Power Flow",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Animated stat counter that smoothly animates number changes
 */
@Composable
fun AnimatedStatCounter(
    value: Float,
    label: String,
    unit: String,
    modifier: Modifier = Modifier,
    format: String = "%.2f"
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = tween(500),
        label = "stat"
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = String.format(format, animatedValue),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = unit,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
