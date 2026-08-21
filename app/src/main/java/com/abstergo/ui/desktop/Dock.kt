package com.abstergo.ui.desktop

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.abstergo.model.AppSource

@Composable
fun Dock(
    apps: List<AppSource>,
    runningApps: List<AppSource>,
    onAppClick: (AppSource) -> Unit,
    modifier: Modifier = Modifier
) {
    val runningIds = runningApps.map { it.id }

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                Color.Black.copy(alpha = 0.4f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        apps.take(7).forEach { appSource ->  // Limit dock to 7 apps
            val isRunning = runningIds.contains(appSource.id)
            val bitmap = (appSource as? AppSource.Cloned)?.appIconBitmap

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onAppClick(appSource) }
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onAppClick(appSource) },
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = appSource.displayName,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        Icon(
                            imageVector = appSource.icon,
                            contentDescription = appSource.displayName,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                if (isRunning) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}
