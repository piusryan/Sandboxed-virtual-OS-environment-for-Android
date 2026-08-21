package com.abstergo.model

import android.graphics.Bitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.ui.graphics.vector.ImageVector
import com.abstergo.data.ClonedAppEntity

/**
 * Unified app identifier — supports both built-in static apps and user-cloned apps.
 */
sealed class AppSource {
    abstract val id: String
    abstract val displayName: String
    abstract val icon: ImageVector
    abstract val launchUrl: String?

    data class BuiltIn(val appType: AppType) : AppSource() {
        override val id: String = "builtin_${appType.name}"
        override val displayName: String = appType.displayName
        override val icon: ImageVector = appType.icon
        override val launchUrl: String? = appType.launchUrl
    }

    data class Cloned(
        val packageName: String,
        override val displayName: String,
        override val launchUrl: String,
        val appIconBitmap: Bitmap? = null
    ) : AppSource() {
        override val id: String = "cloned_$packageName"
        override val icon: ImageVector = Icons.Default.Apps
    }

    companion object {
        fun fromEntity(entity: ClonedAppEntity): Cloned {
            return Cloned(
                packageName = entity.packageName,
                displayName = entity.displayName,
                launchUrl = entity.webUrl
            )
        }
    }
}
