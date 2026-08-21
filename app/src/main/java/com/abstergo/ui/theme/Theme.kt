package com.abstergo.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AbstergoOSBlue,
    onPrimary = AbstergoOSSurface,
    secondary = AbstergoOSLightBlue,
    surface = AbstergoOSSurface,
    onSurface = AbstergoOSOnSurface,
    surfaceVariant = AbstergoOSSurfaceVariant,
    onSurfaceVariant = AbstergoOSOnSurfaceVariant,
    background = AbstergoOSSurface,
    onBackground = AbstergoOSOnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = AbstergoOSLightBlue,
    onPrimary = AbstergoOSDarkSurface,
    secondary = AbstergoOSBlue,
    surface = AbstergoOSDarkSurface,
    onSurface = AbstergoOSDarkOnSurface,
    surfaceVariant = AbstergoOSDarkSurfaceVariant,
    onSurfaceVariant = AbstergoOSDarkOnSurfaceVariant,
    background = AbstergoOSDarkSurface,
    onBackground = AbstergoOSDarkOnSurface
)

@Composable
fun AbstergoOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AbstergoOSTypography,
        content = content
    )
}
