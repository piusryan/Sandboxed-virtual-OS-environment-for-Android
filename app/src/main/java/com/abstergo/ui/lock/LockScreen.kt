package com.abstergo.ui.lock

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abstergo.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LockScreen(
    onUnlock: () -> Unit,
    viewModel: LockViewModel = viewModel()
) {
    val enteredPin by viewModel.enteredPin.collectAsState()
    val shakeTrigger by viewModel.shakeTrigger.collectAsState()
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var currentDate by remember { mutableStateOf(getCurrentDate()) }

    // Update clock every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            currentDate = getCurrentDate()
            kotlinx.coroutines.delay(1000)
        }
    }

    // Shake animation
    val shakeOffset by animateFloatAsState(
        targetValue = if (shakeTrigger > 0) 0f else 0f,
        animationSpec = if (shakeTrigger > 0) {
            repeatable(
                iterations = 3,
                animation = tween(50),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            snap()
        },
        label = "shake"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        // Abstergo dark logo as background
        Image(
            painter = painterResource(id = R.drawable.abstergo_logo_dark),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.4f),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Clock and date
            Spacer(modifier = Modifier.height(80.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentTime,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentDate,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 2.sp
                )
            }

            // Middle: PIN dots
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(x = shakeOffset.dp)
            ) {
                Text(
                    text = "Enter PIN",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PIN dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    if (index < enteredPin.length) Color.White
                                    else Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        )
                    }
                }
            }

            // Bottom: Number pad
            Column(
                modifier = Modifier.padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "DEL")
                )

                rows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { key ->
                            PinKey(
                                label = key,
                                onClick = {
                                    when {
                                        key == "DEL" -> viewModel.deleteDigit()
                                        key == "" -> {} // Empty space
                                        else -> {
                                            viewModel.appendDigit(key)
                                            if (enteredPin.length < 3) {
                                                // Will be 4 digits after this
                                            }
                                        }
                                    }
                                    // Check PIN after each digit
                                    if (key != "DEL" && key != "" && enteredPin.length >= 3) {
                                        if (viewModel.checkPin()) {
                                            onUnlock()
                                        }
                                    }
                                },
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun PinKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (label.isEmpty()) {
        Spacer(modifier = modifier)
        return
    }

    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (label == "DEL") {
            Icon(
                imageVector = Icons.Default.Backspace,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = label,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )
        }
    }
}

private fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}

private fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    return sdf.format(Date())
}
