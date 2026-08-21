package com.abstergo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "AbstergoOS_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        private val PIN_KEY = stringPreferencesKey("pin")
        private val WALLPAPER_KEY = stringPreferencesKey("wallpaper")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

        const val DEFAULT_PIN = "1234"
    }

    val pin: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) emit(emptyPreferences())
            else throw it
        }
        .map { preferences ->
            preferences[PIN_KEY] ?: DEFAULT_PIN
        }

    val wallpaper: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) emit(emptyPreferences())
            else throw it
        }
        .map { preferences ->
            preferences[WALLPAPER_KEY] ?: "ABSTERGO_BLUE"
        }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .catch {
            if (it is IOException) emit(emptyPreferences())
            else throw it
        }
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: true
        }

    suspend fun setPin(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[PIN_KEY] = pin
        }
    }

    suspend fun setWallpaper(wallpaper: String) {
        context.dataStore.edit { preferences ->
            preferences[WALLPAPER_KEY] = wallpaper
        }
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }
}
