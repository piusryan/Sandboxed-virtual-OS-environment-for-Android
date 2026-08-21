package com.abstergo.ui.apps.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abstergo.model.WallpaperOption
import com.abstergo.ui.apps.sandbox.DataFlushManager

@Composable
fun SettingsApp(
    onLockScreen: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val currentWallpaper by viewModel.currentWallpaper.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val pinChangeMessage by viewModel.pinChangeMessage.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showFlushAllDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Settings",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        // PIN section
        SettingsSection(title = "Security") {
            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Change PIN",
                subtitle = "Change your lock screen PIN",
                onClick = { showPinDialog = true }
            )
            SettingsItem(
                icon = Icons.Default.LockReset,
                title = "Lock Screen",
                subtitle = "Return to lock screen",
                onClick = onLockScreen
            )
        }

        // Wallpaper section
        SettingsSection(title = "Appearance") {
            Text(
                text = "Wallpaper",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WallpaperOption.entries.forEach { wallpaper ->
                    val isSelected = currentWallpaper == wallpaper
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) Color(0xFF64B5F6) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.setWallpaper(wallpaper) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = wallpaper.imageResId),
                            contentDescription = wallpaper.displayName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Label overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = wallpaper.displayName,
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dark mode toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Dark Mode",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { viewModel.setDarkMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF64B5F6),
                        checkedTrackColor = Color(0xFF1A73E8).copy(alpha = 0.5f)
                    )
                )
            }
        }

        // Data & Privacy section
        SettingsSection(title = "Data & Privacy") {
            SettingsItem(
                icon = Icons.Default.DeleteForever,
                title = "Flush All Social App Data",
                subtitle = "Wipe all cookies, sessions, and cache from social apps",
                onClick = { showFlushAllDialog = true }
            )
            Text(
                text = "Installed social apps: Instagram, WhatsApp, Telegram, X, Facebook",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // About section
        SettingsSection(title = "About") {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Abstergo OS",
                subtitle = "Version 1.0 - Isolated app cloning environment",
                onClick = { showAboutDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // PIN Change Dialog
    if (showPinDialog) {
        PinChangeDialog(
            onDismiss = {
                showPinDialog = false
                viewModel.clearPinMessage()
            },
            onConfirm = { currentPin, newPin ->
                viewModel.changePin(currentPin, newPin)
            },
            message = pinChangeMessage
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Abstergo OS", color = Color.White) },
            text = {
                Column {
                    Text("Version 1.0", color = Color.White.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "An isolated app cloning environment. Run social apps in sandboxed WebViews, sign in, and flush all data whenever you want.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK", color = Color(0xFF64B5F6))
                }
            },
            containerColor = Color(0xFF2D2D30)
        )
    }

    // Flush All Data Dialog
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
            title = { Text("Flush ALL Social App Data?", color = Color.White) },
            text = {
                Column {
                    Text(
                        "This will permanently delete all data for:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("Instagram", "WhatsApp", "Telegram", "X / Twitter", "Facebook").forEach { app ->
                        Text(
                            text = "  \u2022  $app",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "All login sessions, cookies, and cached data will be permanently wiped.",
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
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64B5F6),
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun PinChangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    message: String
) {
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change PIN", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = currentPin,
                    onValueChange = { currentPin = it.filter { c -> c.isDigit() } },
                    label = { Text("Current PIN") },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF64B5F6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedContainerColor = Color(0xFF2D2D30),
                        unfocusedContainerColor = Color(0xFF2D2D30),
                        cursorColor = Color.White,
                        focusedLabelColor = Color(0xFF64B5F6),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { newPin = it.filter { c -> c.isDigit() } },
                    label = { Text("New PIN") },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF64B5F6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedContainerColor = Color(0xFF2D2D30),
                        unfocusedContainerColor = Color(0xFF2D2D30),
                        cursorColor = Color.White,
                        focusedLabelColor = Color(0xFF64B5F6),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { confirmPin = it.filter { c -> c.isDigit() } },
                    label = { Text("Confirm New PIN") },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF64B5F6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedContainerColor = Color(0xFF2D2D30),
                        unfocusedContainerColor = Color(0xFF2D2D30),
                        cursorColor = Color.White,
                        focusedLabelColor = Color(0xFF64B5F6),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (message.contains("success")) Color(0xFF4CAF50) else Color(0xFFE74C3C),
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newPin == confirmPin) {
                        onConfirm(currentPin, newPin)
                    }
                },
                enabled = newPin.isNotEmpty() && confirmPin.isNotEmpty() && newPin == confirmPin
            ) {
                Text("Change", color = Color(0xFF64B5F6))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.7f))
            }
        },
        containerColor = Color(0xFF2D2D30)
    )
}
