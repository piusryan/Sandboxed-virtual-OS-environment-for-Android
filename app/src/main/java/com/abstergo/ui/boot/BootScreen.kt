package com.abstergo.ui.boot

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
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
import com.abstergo.R
import kotlinx.coroutines.delay

@Composable
fun BootScreen(onBootComplete: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    val alphaAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(600)
        showText = true
        for (i in 1..100) {
            progress = i / 100f
            delay(25)
        }
        delay(400)
        onBootComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A)),
        contentAlignment = Alignment.Center
    ) {
        // Abstergo logo image as background
        Image(
            painter = painterResource(id = R.drawable.abstergo_logo_blue),
            contentDescription = "Abstergo Logo",
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnimation * 0.6f),
            contentScale = ContentScale.Crop
        )

        // Overlay content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnimation)
        ) {
            // Abstergo OS title
            Text(
                text = "ABSTERGO",
                fontSize = 42.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                letterSpacing = 12.sp
            )
            Text(
                text = "OS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Thin,
                color = Color(0xFF64B5F6),
                letterSpacing = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (showText) {
                Text(
                    text = "Initializing Animus...",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 3.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .width(220.dp)
                    .height(2.dp),
                color = Color(0xFF64B5F6),
                trackColor = Color.White.copy(alpha = 0.1f),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 11.sp,
                color = Color(0xFF64B5F6).copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )
        }
    }
}
