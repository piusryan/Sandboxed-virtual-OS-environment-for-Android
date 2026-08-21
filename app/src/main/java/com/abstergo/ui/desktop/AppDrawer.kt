package com.abstergo.ui.desktop

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abstergo.model.AppSource
import com.abstergo.model.AppType
import com.abstergo.ui.apps.sandbox.DataFlushManager

@Composable
fun AppDrawer(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onAppClick: (AppSource) -> Unit,
    clonedApps: List<AppSource.Cloned>,
    onAddApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFlushAllDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Combine built-in social apps + cloned apps
    val builtInSocial = AppType.socialApps.map { AppSource.BuiltIn(it) }
    val allApps = builtInSocial + clonedApps

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { onDismiss() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp, start = 20.dp, end = 20.dp, bottom = 110.dp)
                    .verticalScroll(rememberScrollState())
                    .clickable(enabled = false) { }
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "All Apps",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${allApps.size} apps \u2022 Tap to open. Flush anytime.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }

                    Row {
                        // Add app button
                        TextButton(
                            onClick = {
                                onDismiss()
                                onAddApp()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF64B5F6)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clone", fontSize = 12.sp)
                        }

                        // Flush All Data button
                        TextButton(
                            onClick = { showFlushAllDialog = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFE74C3C)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Flush All", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Built-in apps section
                if (builtInSocial.isNotEmpty()) {
                    Text(
                        text = "Built-in",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.4f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val builtInRows = builtInSocial.chunked(3)
                    builtInRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { appSource ->
                                DrawerAppIcon(
                                    appSource = appSource,
                                    onClick = {
                                        onAppClick(appSource)
                                        onDismiss()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(3 - row.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Cloned apps section
                if (clonedApps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cloned from your phone",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.4f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val clonedRows = clonedApps.chunked(3)
                    clonedRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { appSource ->
                                DrawerAppIcon(
                                    appSource = appSource,
                                    onClick = {
                                        onAppClick(appSource)
                                        onDismiss()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(3 - row.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Add app card
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A73E8).copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onAddApp()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1A73E8).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF64B5F6),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Clone More Apps",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF64B5F6)
                            )
                            Text(
                                text = "Browse installed apps with web versions",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Settings entry
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAppClick(AppSource.BuiltIn(AppType.SETTINGS))
                            onDismiss()
                        }
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppType.SETTINGS.icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Settings",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "PIN, wallpaper, flush all data",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.35f)
                        )
                    }
                }
            }
        }
    }

    // Flush All Data dialog
    if (showFlushAllDialog) {
        AlertDialog(
            onDismissRequest = { showFlushAllDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = Color(0xFFE74C3C)
                )
            },
            title = {
                Text("Flush ALL App Data?", color = Color.White)
            },
            text = {
                Column {
                    Text(
                        "This will wipe data for ALL cloned apps:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    allApps.forEach { app ->
                        Text(
                            text = "  \u2022  ${app.displayName}",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "All login sessions, cookies, and cached data will be permanently deleted. " +
                                "You will be signed out of all accounts.",
                        color = Color(0xFFE74C3C).copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    DataFlushManager.flushAllData(context)
                    showFlushAllDialog = false
                }) {
                    Text("FLUSH ALL", color = Color(0xFFE74C3C), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFlushAllDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF2D2D30)
        )
    }
}

@Composable
fun DrawerAppIcon(
    appSource: AppSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bitmap = (appSource as? AppSource.Cloned)?.appIconBitmap

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = appSource.displayName,
                    modifier = Modifier.size(42.dp)
                )
            } else {
                Icon(
                    imageVector = appSource.icon,
                    contentDescription = appSource.displayName,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = appSource.displayName,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1
        )
    }
}
