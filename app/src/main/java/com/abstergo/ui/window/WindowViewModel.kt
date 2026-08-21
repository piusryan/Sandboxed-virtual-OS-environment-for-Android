package com.abstergo.ui.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.abstergo.model.AppSource
import com.abstergo.model.AppType
import com.abstergo.model.WindowState

class WindowViewModel : ViewModel() {

    private val _windows = mutableStateListOf<WindowState>()
    val windows: List<WindowState> get() = _windows

    var topZIndex by mutableStateOf(1f)
        private set

    fun openApp(appSource: AppSource) {
        // Check if app is already open but minimized
        val existing = _windows.find { it.appSource.id == appSource.id }
        if (existing != null) {
            if (existing.isMinimized) {
                existing.isMinimized = false
            }
            bringToFront(existing.id)
            return
        }

        topZIndex += 1f
        val newWindow = WindowState(
            appSource = appSource,
            zIndex = topZIndex
        )
        _windows.add(newWindow)
    }

    fun openApp(appType: AppType) {
        openApp(AppSource.BuiltIn(appType))
    }

    fun closeWindow(windowId: String) {
        _windows.removeAll { it.id == windowId }
    }

    fun minimizeWindow(windowId: String) {
        _windows.find { it.id == windowId }?.isMinimized = true
    }

    fun bringToFront(windowId: String) {
        topZIndex += 1f
        _windows.find { it.id == windowId }?.zIndex = topZIndex
    }

    fun updateWindowOffset(windowId: String, offsetX: Float, offsetY: Float) {
        _windows.find { it.id == windowId }?.let { window ->
            window.offset = window.offset.copy(x = offsetX, y = offsetY)
        }
    }

    fun updateWindowSize(windowId: String, widthDp: Float, heightDp: Float) {
        _windows.find { it.id == windowId }?.let { window ->
            window.size = DpSize(
                width = widthDp.coerceAtLeast(250f).dp,
                height = heightDp.coerceAtLeast(200f).dp
            )
        }
    }

    fun isAppRunning(appSource: AppSource): Boolean {
        return _windows.any { it.appSource.id == appSource.id && !it.isMinimized }
    }

    fun getRunningAppSources(): List<AppSource> {
        return _windows.filter { !it.isMinimized }.map { it.appSource }
    }

    fun getRunningAppTypes(): List<AppType> {
        return _windows
            .filter { !it.isMinimized && it.appSource is AppSource.BuiltIn }
            .map { (it.appSource as AppSource.BuiltIn).appType }
    }
}
