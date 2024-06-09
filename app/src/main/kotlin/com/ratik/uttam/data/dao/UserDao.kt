package com.ratik.uttam.data.dao

import android.content.SharedPreferences
import javax.inject.Inject

class UserDao @Inject constructor(private val sharedPreferences: SharedPreferences) {
  fun setHasOnboarded() {
    sharedPreferences.edit().putBoolean(HAS_ONBOARDED, true).apply()
  }

  fun hasOnboarded(): Boolean {
    return sharedPreferences.getBoolean(HAS_ONBOARDED, false)
  }

  fun setDeviceHeight(height: Int) {
    sharedPreferences.edit().putInt(DEVICE_HEIGHT, height).apply()
  }

  fun getDeviceHeight(): Int {
    return sharedPreferences.getInt(DEVICE_HEIGHT, 0)
  }

  fun setDeviceWidth(width: Int) {
    sharedPreferences.edit().putInt(DEVICE_WIDTH, width).apply()
  }

  fun getDeviceWidth(): Int {
    return sharedPreferences.getInt(DEVICE_WIDTH, 0)
  }

  companion object {
    const val HAS_ONBOARDED = "hasOnboarded"
    const val DEVICE_HEIGHT = "deviceHeight"
    const val DEVICE_WIDTH = "deviceWidth"
  }
}
