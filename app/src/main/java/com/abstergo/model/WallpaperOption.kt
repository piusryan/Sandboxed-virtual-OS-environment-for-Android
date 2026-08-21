package com.abstergo.model

import androidx.annotation.DrawableRes
import com.abstergo.R

enum class WallpaperOption(
    val displayName: String,
    @DrawableRes val imageResId: Int
) {
    ABSTERGO_BLUE("Abstergo Blue", R.drawable.abstergo_logo_blue),
    WALLPAPER_1("Industries", R.drawable.abstergo_wallpaper_1),
    WALLPAPER_2("Animus", R.drawable.abstergo_wallpaper_2),
    WALLPAPER_3("Templar", R.drawable.abstergo_wallpaper_3)
}
