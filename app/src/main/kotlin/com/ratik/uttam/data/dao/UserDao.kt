package com.ratik.uttam.data.dao

import android.content.SharedPreferences
import javax.inject.Inject

class UserDao @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun setHasOnboarded() {
        sharedPreferences.edit().putBoolean(HAS_ONBOARDED, true).apply()
    }

    fun hasOnboarded(): Boolean {
        return sharedPreferences.getBoolean(HAS_ONBOARDED, false)
    }

    companion object {
        const val HAS_ONBOARDED = "hasOnboarded"
    }
}