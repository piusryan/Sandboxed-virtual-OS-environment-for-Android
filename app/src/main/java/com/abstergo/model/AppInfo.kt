package com.abstergo.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppType(
    val displayName: String,
    val icon: ImageVector,
    val isSocialApp: Boolean = false,
    val launchUrl: String? = null
) {
    // System
    SETTINGS("Settings", Icons.Default.Settings),

    // Cloned social apps (sandboxed)
    INSTAGRAM("Instagram", Icons.Default.PhotoCamera, true, "https://www.instagram.com/"),
    WHATSAPP("WhatsApp", Icons.Default.Chat, true, "https://web.whatsapp.com/"),
    TELEGRAM("Telegram", Icons.Default.Send, true, "https://web.telegram.org/"),
    TWITTER("X / Twitter", Icons.Default.Tag, true, "https://x.com/"),
    FACEBOOK("Facebook", Icons.Default.ThumbUp, true, "https://m.facebook.com/");

    companion object {
        val systemApps = entries.filter { !it.isSocialApp }
        val socialApps = entries.filter { it.isSocialApp }
    }
}

data class AppInfo(
    val type: AppType,
    val displayName: String = type.displayName,
    val icon: ImageVector = type.icon
)
