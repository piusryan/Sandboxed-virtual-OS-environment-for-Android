package com.abstergo.ui.desktop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abstergo.data.AppDatabase
import com.abstergo.data.ClonedAppDao
import com.abstergo.data.SettingsDataStore
import com.abstergo.model.AppSource
import com.abstergo.model.WallpaperOption
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DesktopViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsDataStore = SettingsDataStore(application)
    private val clonedAppDao: ClonedAppDao = AppDatabase.getInstance(application).clonedAppDao()

    val wallpaper: StateFlow<WallpaperOption> = settingsDataStore.wallpaper
        .map { name ->
            try {
                WallpaperOption.valueOf(name)
            } catch (e: Exception) {
                WallpaperOption.ABSTERGO_BLUE
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WallpaperOption.ABSTERGO_BLUE)

    val isDarkMode: StateFlow<Boolean> = settingsDataStore.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    /** Flow of user-cloned apps from the database */
    val clonedApps: StateFlow<List<AppSource.Cloned>> = clonedAppDao.getAllClonedApps()
        .map { entities ->
            entities.map { entity -> AppSource.fromEntity(entity) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** All apps: built-in social apps + cloned apps */
    val allClonedApps: StateFlow<List<AppSource>> = clonedApps
        .map { cloned -> cloned as List<AppSource> }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeClonedApp(packageName: String) {
        viewModelScope.launch {
            clonedAppDao.deleteByPackageName(packageName)
        }
    }
}
