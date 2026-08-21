package com.abstergo.ui.apps.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abstergo.data.SettingsDataStore
import com.abstergo.model.WallpaperOption
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsDataStore = SettingsDataStore(application)

    val currentPin: StateFlow<String> = settingsDataStore.pin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsDataStore.DEFAULT_PIN)

    val currentWallpaper: StateFlow<WallpaperOption> = settingsDataStore.wallpaper
        .map { name ->
            try { WallpaperOption.valueOf(name) } catch (e: Exception) { WallpaperOption.ABSTERGO_BLUE }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WallpaperOption.ABSTERGO_BLUE)

    val isDarkMode: StateFlow<Boolean> = settingsDataStore.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _pinChangeMessage = MutableStateFlow("")
    val pinChangeMessage: StateFlow<String> = _pinChangeMessage.asStateFlow()

    fun changePin(currentPinInput: String, newPin: String) {
        if (currentPinInput == currentPin.value) {
            if (newPin.length >= 4) {
                viewModelScope.launch {
                    settingsDataStore.setPin(newPin)
                    _pinChangeMessage.value = "PIN changed successfully"
                }
            } else {
                _pinChangeMessage.value = "PIN must be at least 4 digits"
            }
        } else {
            _pinChangeMessage.value = "Current PIN is incorrect"
        }
    }

    fun setWallpaper(wallpaper: WallpaperOption) {
        viewModelScope.launch {
            settingsDataStore.setWallpaper(wallpaper.name)
        }
    }

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(isDark)
        }
    }

    fun clearPinMessage() {
        _pinChangeMessage.value = ""
    }
}
