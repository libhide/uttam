package com.ratik.uttam.data.dao

import android.content.SharedPreferences
import javax.inject.Inject

class PrefsDao @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun toggleSetWallpaperAutomatically() {
        val shouldSetWallpaperAutomatically = shouldSetWallpaperAutomatically()
        sharedPreferences.edit().putBoolean(SET_AUTOMATICALLY, !shouldSetWallpaperAutomatically)
            .apply()
    }

    fun shouldSetWallpaperAutomatically(): Boolean {
        return sharedPreferences.getBoolean(SET_AUTOMATICALLY, true)
    }

    companion object {
        const val SET_AUTOMATICALLY = "setAutomatically"
    }
}