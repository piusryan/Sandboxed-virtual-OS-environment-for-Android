package com.abstergo.ui.desktop

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abstergo.data.AppDatabase
import com.abstergo.data.ClonedAppDao
import com.abstergo.data.ClonedAppEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Info about an installed app that has a known web counterpart.
 */
data class InstallableApp(
    val packageName: String,
    val displayName: String,
    val webUrl: String,
    val iconBitmap: Bitmap?,
    val isAlreadyCloned: Boolean = false
)

class AppPickerViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "AppPickerViewModel"
    }

    private val clonedAppDao: ClonedAppDao = AppDatabase.getInstance(application).clonedAppDao()

    private val _installedApps = MutableStateFlow<List<InstallableApp>>(emptyList())
    val installedApps: StateFlow<List<InstallableApp>> = _installedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredApps: StateFlow<List<InstallableApp>> = combine(installedApps, searchQuery) { apps, query ->
        if (query.isBlank()) apps
        else apps.filter { it.displayName.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Known web counterparts for popular apps.
     * Maps package name -> web URL.
     */
    private val knownWebApps = mapOf(
        "com.google.android.youtube" to "https://m.youtube.com",
        "com.reddit.frontpage" to "https://www.reddit.com",
        "com.zhiliaoapp.musically" to "https://www.tiktok.com",
        "com.linkedin.android" to "https://www.linkedin.com",
        "com.pinterest" to "https://www.pinterest.com",
        "com.snapchat.android" to "https://web.snapchat.com",
        "com.discord" to "https://discord.com",
        "com.slack" to "https://app.slack.com",
        "com.spotify.music" to "https://open.spotify.com",
        "com.netflix.mediaclient" to "https://www.netflix.com",
        "com.amazon.avod.thirdpartyclient" to "https://www.primevideo.com",
        "com.twitter.android" to "https://x.com",
        "com.instagram.android" to "https://www.instagram.com",
        "com.whatsapp" to "https://web.whatsapp.com",
        "org.telegram.messenger" to "https://web.telegram.org",
        "com.facebook.katana" to "https://m.facebook.com",
        "com.facebook.orca" to "https://www.messenger.com",
        "com.google.android.apps.maps" to "https://maps.google.com",
        "com.google.android.gm" to "https://mail.google.com",
        "com.microsoft.office.outlook" to "https://outlook.live.com",
        "com.dropbox.android" to "https://www.dropbox.com",
        "com.tumblr" to "https://www.tumblr.com",
        "com.twitch" to "https://www.twitch.tv",
        "tv.twitch.android.app" to "https://www.twitch.tv",
        "com.quora.android" to "https://www.quora.com",
        "com.medium.android" to "https://medium.com",
        "com.github.android" to "https://github.com",
        "com.amazon.mShop.android.shopping" to "https://www.amazon.com",
        "com.ebay.mobile" to "https://www.ebay.com",
        "com.ubercab" to "https://m.uber.com",
        "com.google.android.apps.meetings" to "https://meet.google.com",
        "com.zoom.videomeetings" to "https://zoom.us/join",
        "com.skype.raider" to "https://web.skype.com",
        "com.microsoft.teams" to "https://teams.microsoft.com",
        "com.pinterest.tws" to "https://www.pinterest.com",
        "com.bereal.ft" to "https://bere.al",
        "com.duolingo" to "https://www.duolingo.com",
        "com.strava" to "https://www.strava.com",
        "com.shazam.android" to "https://www.shazam.com",
        "com.soundcloud.android" to "https://soundcloud.com",
        "fm.castbox.audio.podcast.radio" to "https://castbox.fm"
    )

    fun scanInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val apps = withContext(Dispatchers.IO) {
                    val pm = getApplication<Application>().packageManager
                    val clonedPackages = getClonedPackageNames()

                    // Use getInstalledPackages to get all visible packages
                    val installedPackageInfos = pm.getInstalledPackages(0)
                    val resultList = mutableListOf<InstallableApp>()

                    for (pkgInfo in installedPackageInfos) {
                        val pkgName = pkgInfo.packageName
                        val webUrl = knownWebApps[pkgName]

                        if (webUrl != null) {
                            val label = try {
                                pm.getApplicationLabel(pkgInfo.applicationInfo).toString()
                            } catch (_: Exception) {
                                pkgName
                            }

                            val icon = try {
                                val drawable = pm.getApplicationIcon(pkgInfo.applicationInfo)
                                if (drawable is BitmapDrawable) {
                                    drawable.bitmap
                                } else {
                                    val w = drawable.intrinsicWidth.coerceAtLeast(48)
                                    val h = drawable.intrinsicHeight.coerceAtLeast(48)
                                    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                                    val canvas = Canvas(bitmap)
                                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                                    drawable.draw(canvas)
                                    bitmap
                                }
                            } catch (_: Exception) {
                                null
                            }

                            resultList.add(
                                InstallableApp(
                                    packageName = pkgName,
                                    displayName = label,
                                    webUrl = webUrl,
                                    iconBitmap = icon,
                                    isAlreadyCloned = clonedPackages.contains(pkgName)
                                )
                            )
                        }
                    }

                    resultList.sortedBy { it.displayName.lowercase() }
                }

                _installedApps.value = apps
            } catch (e: Exception) {
                Log.e(TAG, "Error scanning installed apps", e)
                _installedApps.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cloneApp(installableApp: InstallableApp) {
        viewModelScope.launch {
            clonedAppDao.insertClonedApp(
                ClonedAppEntity(
                    packageName = installableApp.packageName,
                    displayName = installableApp.displayName,
                    webUrl = installableApp.webUrl
                )
            )
            _installedApps.value = _installedApps.value.map {
                if (it.packageName == installableApp.packageName) {
                    it.copy(isAlreadyCloned = true)
                } else it
            }
        }
    }

    fun removeClonedApp(packageName: String) {
        viewModelScope.launch {
            clonedAppDao.deleteByPackageName(packageName)
            _installedApps.value = _installedApps.value.map {
                if (it.packageName == packageName) {
                    it.copy(isAlreadyCloned = false)
                } else it
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * One-shot query for already-cloned package names.
     * Uses first() instead of collect() to avoid hanging on the live Flow.
     */
    private suspend fun getClonedPackageNames(): Set<String> {
        return try {
            val entities = clonedAppDao.getAllClonedApps().first()
            entities.map { it.packageName }.toSet()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cloned apps", e)
            emptySet()
        }
    }
}
