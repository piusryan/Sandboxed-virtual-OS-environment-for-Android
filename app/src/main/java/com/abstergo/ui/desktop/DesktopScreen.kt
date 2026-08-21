package com.abstergo.ui.desktop

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abstergo.model.AppSource
import com.abstergo.model.AppType
import com.abstergo.ui.apps.sandbox.SandboxedWebApp
import com.abstergo.ui.apps.settings.SettingsApp
import com.abstergo.ui.window.FloatingWindow
import com.abstergo.ui.window.WindowViewModel

@Composable
fun DesktopScreen(
    onLockScreen: () -> Unit,
    desktopViewModel: DesktopViewModel = viewModel(),
    windowViewModel: WindowViewModel = viewModel()
) {
    val wallpaper by desktopViewModel.wallpaper.collectAsState()
    val clonedApps by desktopViewModel.clonedApps.collectAsState()
    var showAppDrawer by remember { mutableStateOf(false) }
    var showAppPicker by remember { mutableStateOf(false) }

    // All displayable apps: built-in social + user-cloned
    val builtInSocial = AppType.socialApps.map { AppSource.BuiltIn(it) }
    val allApps = builtInSocial + clonedApps

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Wallpaper image background
        Image(
            painter = painterResource(id = wallpaper.imageResId),
            contentDescription = "Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Status bar at top
        StatusBar(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(100f)
                .align(Alignment.TopCenter)
        )

        // Desktop icons grid
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 100.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {},
                        onDragStart = {},
                        onDragCancel = {}
                    ) { _, dragAmount ->
                        if (dragAmount < -50f) {
                            showAppDrawer = true
                        }
                    }
                },
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header
            Text(
                text = "Abstergo OS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 3.sp
            )

            Text(
                text = "Cloned Apps",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // All apps grid (built-in + cloned)
            val rows = allApps.chunked(3)
            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { appSource ->
                        AppSourceIcon(
                            appSource = appSource,
                            onClick = { windowViewModel.openApp(appSource) }
                        )
                    }
                    // Fill remaining slots to maintain alignment
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.width(72.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // "+" Add App button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { showAppPicker = true }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1A73E8).copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add App",
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add App",
                    fontSize = 11.sp,
                    color = Color(0xFF64B5F6),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Settings (small, tucked away at bottom)
            AppSourceIcon(
                appSource = AppSource.BuiltIn(AppType.SETTINGS),
                onClick = { windowViewModel.openApp(AppType.SETTINGS) }
            )
        }

        // Floating windows
        windowViewModel.windows
            .filter { !it.isMinimized }
            .sortedBy { it.zIndex }
            .forEach { windowState ->
                FloatingWindow(
                    windowState = windowState,
                    onDrag = { x, y ->
                        windowViewModel.updateWindowOffset(windowState.id, x, y)
                    },
                    onResize = { w, h ->
                        windowViewModel.updateWindowSize(windowState.id, w, h)
                    },
                    onClose = { windowViewModel.closeWindow(windowState.id) },
                    onMinimize = { windowViewModel.minimizeWindow(windowState.id) },
                    onFocus = { windowViewModel.bringToFront(windowState.id) },
                    modifier = Modifier.zIndex(windowState.zIndex)
                ) {
                    when (val source = windowState.appSource) {
                        is AppSource.BuiltIn -> {
                            when (source.appType) {
                                AppType.SETTINGS -> SettingsApp(onLockScreen = onLockScreen)
                                else -> {
                                    if (source.appType.isSocialApp) {
                                        SandboxedWebApp(appSource = source)
                                    }
                                }
                            }
                        }
                        is AppSource.Cloned -> {
                            SandboxedWebApp(appSource = source)
                        }
                    }
                }
            }

        // App drawer overlay
        AppDrawer(
            isVisible = showAppDrawer,
            onDismiss = { showAppDrawer = false },
            onAppClick = { appSource -> windowViewModel.openApp(appSource) },
            clonedApps = clonedApps,
            onAddApp = { showAppPicker = true },
            modifier = Modifier.zIndex(200f)
        )

        // Dock at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .zIndex(150f)
        ) {
            Dock(
                apps = allApps,
                runningApps = windowViewModel.getRunningAppSources(),
                onAppClick = { appSource -> windowViewModel.openApp(appSource) }
            )
        }

        // App picker dialog
        AppPickerDialog(
            isVisible = showAppPicker,
            onDismiss = { showAppPicker = false },
            onAppCloned = { /* clonedApps flow will auto-update */ },
            modifier = Modifier.zIndex(300f)
        )
    }
}

@Composable
fun AppSourceIcon(
    appSource: AppSource,
    onClick: () -> Unit,
    iconBitmap: Bitmap? = null
) {
    val bitmap = iconBitmap ?: (appSource as? AppSource.Cloned)?.appIconBitmap

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = appSource.displayName,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                Icon(
                    imageVector = appSource.icon,
                    contentDescription = appSource.displayName,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = appSource.displayName,
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
